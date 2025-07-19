package me.miki.shindo.management.account.microsoft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import me.miki.shindo.Shindo;
import me.miki.shindo.injection.interfaces.IMixinMinecraft;
import me.miki.shindo.logger.ShindoLogger;
import me.miki.shindo.management.account.Account;
import me.miki.shindo.management.account.AccountManager;
import me.miki.shindo.management.account.AccountType;
import me.miki.shindo.management.account.skin.SkinDownloader;
import me.miki.shindo.management.file.FileManager;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.notification.NotificationType;
import me.miki.shindo.utils.network.HttpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftAuthentication {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final SkinDownloader skinDownloader;

    public MicrosoftAuthentication() {
        skinDownloader = new SkinDownloader();
    }

    public void loginWithRefreshToken(String refreshToken) {

        ShindoLogger.info("[DEBUG] call login with refresh token ");
        JsonObject response = HttpUtils.readJson("https://login.live.com/oauth20_token.srf?client_id=000000004C12AE6F&grant_type=refresh_token&refresh_token=" + refreshToken, null);
        ShindoLogger.info("[DEBUG] call login with refresh token after refresh token url");
        if(response.get("access_token") == null) {
            ShindoLogger.error("[DEBUG] call login with refresh token failed");
            return;
        }

        getXboxLiveToken(response.get("access_token").getAsString(), refreshToken);
    }

    public void loginWithUrl(String url) {
        try {
            getMicrosoftToken(new URL(url));
            Shindo.getInstance().getNotificationManager().post(TranslateText.ADDED, "Microsoft Account " + Shindo.getInstance().getAccountManager().getCurrentAccount().getName(), NotificationType.SUCCESS);
        } catch (MalformedURLException e) {
            Shindo.getInstance().getNotificationManager().post(TranslateText.ERROR, "Malformed Url Exception!", NotificationType.ERROR);
        }
    }

    public void loginWithPopUpWindow(Runnable afterLogin) {
        new MicrosoftLoginWindow(afterLogin);
    }

    private void getMicrosoftToken(URL tokenURL) {

        JsonObject response = HttpUtils.readJson("https://login.live.com/oauth20_token.srf?client_id=000000004C12AE6F&grant_type=authorization_code&redirect_uri=https://login.live.com/oauth20_desktop.srf&code=" + tokenURL.toString().split("=")[1], null);

        getXboxLiveToken(response.get("access_token").getAsString(), response.get("refresh_token").getAsString());
    }

    private void getXboxLiveToken(String token, String refreshToken) {

        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", "d=" + token);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "http://auth.xboxlive.com");
        request.addProperty("TokenType", "JWT");

        JsonObject response = HttpUtils.postJson("https://user.auth.xboxlive.com/user/authenticate", request);

        getXSTS(response.get("Token").getAsString(), refreshToken);
    }

    private void getXSTS(String token, String refreshToken) {

        JsonPrimitive jsonToken = new JsonPrimitive(token);
        JsonArray userTokens = new JsonArray();
        userTokens.add(jsonToken);

        JsonObject properties = new JsonObject();
        properties.addProperty("SandboxId", "RETAIL");
        properties.add("UserTokens", userTokens);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        request.addProperty("TokenType", "JWT");

        JsonObject response = HttpUtils.postJson("https://xsts.auth.xboxlive.com/xsts/authorize", request);

        if (response.has("XErr")) {
            switch (response.get("XErr").getAsString()) {
                case "2148916233":
                    ShindoLogger.error("This account doesn't have an Xbox account.");
                    break;
                case "2148916235":
                    ShindoLogger.error("Xbox isn't available in your country.");
                    break;
                case "2148916238":
                    ShindoLogger.error("The account is under 18 and must be added to a Family (https://start.ui.xboxlive.com/AddChildToFamily)");
                    break;
            }
        } else {
            getMinecraftToken(response.getAsJsonObject("DisplayClaims").get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString(), response.get("Token").getAsString(), refreshToken);
        }
    }

    private void getMinecraftToken(String uhs, String token, String refreshToken) {

        JsonObject request = new JsonObject();
        request.addProperty("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));

        JsonObject response = HttpUtils.postJson("https://api.minecraftservices.com/authentication/login_with_xbox", request);

        checkMinecraftOwnership(response.get("access_token").getAsString(), refreshToken);
    }

    private void checkMinecraftOwnership(String token, String refreshToken) {
        Map<String, String> headers = new HashMap<>();
        boolean ownsMinecraft = false;

        headers.put("Authorization", "Bearer " + token);

        JsonObject request = HttpUtils.readJson("https://api.minecraftservices.com/entitlements/mcstore", headers);

        for (int i = 0; i < request.get("items").getAsJsonArray().size(); i++) {
            String itemName = request.get("items").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
            if (itemName.equals("product_minecraft") || itemName.equals("game_minecraft")) {
                ownsMinecraft = true;
                break;
            }
        }

        if (!ownsMinecraft) {
            ShindoLogger.error("User doesn't own Minecraft");
        } else {
            getMinecraftProfile(token, refreshToken);
        }
    }

    private void getMinecraftProfile(String token, String refreshToken) {

        Shindo instance = Shindo.getInstance();
        AccountManager accountManager = instance.getAccountManager();
        FileManager fileManager = instance.getFileManager();
        File headDir = new File(fileManager.getCacheDir(), "head");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        JsonObject request = HttpUtils.readJson("https://api.minecraftservices.com/minecraft/profile", headers);

        String name = request.get("name").getAsString();
        String uuid = request.get("id").getAsString();
        Account account = new Account(name, uuid, refreshToken, AccountType.MICROSOFT);

        if(!headDir.exists()) {
            fileManager.createDir(headDir);
        }

        skinDownloader.downloadFace(headDir, name, UUIDTypeAdapter.fromString(uuid));

        ((IMixinMinecraft) mc).setSession(new Session(name, uuid, token, "mojang"));

        if(accountManager.getAccountByName(account.getName()) == null) {
            accountManager.getAccounts().add(account);
        }

        accountManager.setCurrentAccount(account);
    }

    public SkinDownloader getSkinDownloader() {
        return skinDownloader;
    }
}

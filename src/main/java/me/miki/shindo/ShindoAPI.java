package me.miki.shindo;

import me.miki.shindo.api.ApiManager;
import me.miki.shindo.api.utils.SSLBypass;
import me.miki.shindo.gui.mainmenu.GuiShindoMainMenu;
import me.miki.shindo.gui.modmenu.GuiModMenu;
import me.miki.shindo.management.file.FileManager;
import net.minecraft.client.Minecraft;

import java.io.File;

public class ShindoAPI {

    private ApiManager apiManager;

    private long launchTime;
    private GuiModMenu modMenu;
    private GuiShindoMainMenu mainMenu;
    private final File firstLoginFile;



    public ShindoAPI() {
        FileManager fileManager = Shindo.getInstance().getFileManager();

        firstLoginFile = new File(fileManager.getCacheDir(), "first.tmp");
    }

    public void init() {
        launchTime = System.currentTimeMillis();
        modMenu = new GuiModMenu();
        mainMenu = new GuiShindoMainMenu();
    }

    public void connect() {
        SSLBypass.disableCertificateValidation();

        String username    = Minecraft.getMinecraft().getSession().getUsername();
        String uuid        = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
        String accountType = Shindo.getInstance()
                .getAccountManager()
                .getCurrentAccount()
                .getType()
                .name();

        // Inicializa o ApiManager com dados corretos
        this.apiManager = new ApiManager(uuid, username, accountType);
        apiManager.notifyEvent("join");
    }

    public void disconnect() {
        if (apiManager != null) {
            apiManager.notifyEvent("leave");
            apiManager.shutdown();
        }
    }

    public GuiModMenu getModMenu() {
        return modMenu;
    }

    public long getLaunchTime() {
        return launchTime;
    }

    public GuiShindoMainMenu getMainMenu() {
        return mainMenu;
    }

    public void createFirstLoginFile() {
        Shindo.getInstance().getFileManager().createFile(firstLoginFile);
    }

    public boolean isFirstLogin() {
        return !firstLoginFile.exists();
    }
    
    public boolean isStaff(String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] STAFF: " + apiManager.isStaff(uuid));
        return apiManager.isStaff(uuid);
    }

    public boolean isDiamond(String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] DIAMOND: " + apiManager.isDiamond(uuid));
        return apiManager.isDiamond(uuid);
    }

    public boolean isGold(String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] GOLD: " + apiManager.isGold(uuid));
        return apiManager.isGold(uuid);
    }

    public boolean isOnline(String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] ONLINE: " + apiManager.isOnline(uuid));
        return  apiManager.isOnline(uuid);
    }

    public boolean hasPrivilege(String uuid, String privilege) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] PRIVILEGE: " + apiManager.hasPrivilege(uuid, privilege));
        return apiManager.hasPrivilege(uuid, privilege);

    }

    public String getName (String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] getName: " + apiManager.getName(uuid));
        return apiManager.getName(uuid);
    }

    public String getAccountType (String uuid) {
        //ShindoLogger.info("[API] UUID: " + uuid);
        //ShindoLogger.info("[API] getAccountType: " + apiManager.getAccountType(uuid));
        return apiManager.getAccountType(uuid);
    }

}
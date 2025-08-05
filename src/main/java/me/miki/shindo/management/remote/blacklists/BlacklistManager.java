package me.miki.shindo.management.remote.blacklists;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.miki.shindo.utils.JsonUtils;
import me.miki.shindo.utils.Multithreading;
import me.miki.shindo.utils.network.HttpUtils;

import java.util.concurrent.CopyOnWriteArrayList;

public class BlacklistManager {

    private final CopyOnWriteArrayList<Server> blacklist = new CopyOnWriteArrayList<>();

    public BlacklistManager() {
        check();
    }

    public void check() {
        Multithreading.runAsync(this::loadBlacklists);
    }

    private void loadBlacklists() {

        JsonObject jsonObject = HttpUtils.readJson("https://shindoclient.github.io/data/servers/blacklist.json", null);

        if (jsonObject != null) {

            JsonArray jsonArray = JsonUtils.getArrayProperty(jsonObject, "blacklist");

            if (jsonArray != null) {

                for (JsonElement jsonElement : jsonArray) {

                    Gson gson = new Gson();
                    JsonObject serverJsonObject = gson.fromJson(jsonElement, JsonObject.class);

                    String serverIp = JsonUtils.getStringProperty(serverJsonObject, "serverip", "null");
                    JsonArray modsArray = JsonUtils.getArrayProperty(serverJsonObject, "mods");
                    CopyOnWriteArrayList<String> modsList = new CopyOnWriteArrayList<>();

                    if (modsArray != null) {
                        for (JsonElement modElement : modsArray) {
                            modsList.add(modElement.getAsString());
                        }
                    }

                    blacklist.add(new Server(serverIp, modsList));
                }
            }
        }
    }

    public CopyOnWriteArrayList<Server> getBlacklist() {
        return blacklist;
    }
}

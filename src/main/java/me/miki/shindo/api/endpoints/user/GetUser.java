package me.miki.shindo.api.endpoints.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.miki.shindo.api.ApiSettings;
import me.miki.shindo.api.cache.CacheEntry;
import me.miki.shindo.logger.ShindoLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GetUser {

    private final Map<String, CacheEntry> userCache = new ConcurrentHashMap<>();

    public JsonObject getUserInfo(String uuid) {
        CacheEntry cached = userCache.get(uuid);
        if (cached != null && !cached.isExpired()) {
            return cached.data;
        }

        JsonObject json = fetchUserInfo(uuid);
        if (json != null) {
            userCache.put(uuid, new CacheEntry(json));
        }

        return json;
    }

    private JsonObject fetchUserInfo(String uuid) {
        try {
            URL url = new URL(ApiSettings.CLIENT_STATUS + "?uuid=" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                ShindoLogger.error("[API] Erro ao obter informações para UUID " + uuid + ": HTTP " + responseCode);
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

                ensureDefault(json, "online", false);
                ensureDefault(json, "name", (String) null);
                ensureDefault(json, "accountType", (String) null);

                if (!json.has("privileges") || !json.get("privileges").isJsonArray()) {
                    json.add("privileges", new JsonArray());
                }

                ShindoLogger.info(response.toString());
                return json;
            }

        } catch (Exception e) {
            ShindoLogger.error("[API] Erro ao obter dados do usuário " + uuid + ": " + e.getMessage(), e);
            return null;
        }
    }

    private void ensureDefault(JsonObject obj, String key, Boolean defaultValue) {
        if (!obj.has(key)) {
            obj.addProperty(key, defaultValue);
        }
    }

    private void ensureDefault(JsonObject obj, String key, String defaultValue) {
        if (!obj.has(key)) {
            obj.addProperty(key, defaultValue);
        }
    }
}

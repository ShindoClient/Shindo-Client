package me.miki.shindo.api.endpoints.user;

import com.google.gson.JsonObject;
import me.miki.shindo.api.ApiSettings;
import me.miki.shindo.logger.ShindoLogger;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostUser {

    public void connect(JsonObject json) {

        if (json == null) {
            ShindoLogger.warn("[API] Tentativa de envio com JSON nulo.");
            return;
        }

        try {
            ShindoLogger.info("[API] Enviando evento: " + json);

            HttpURLConnection con = (HttpURLConnection) new URL(ApiSettings.CLIENT_STATUS).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(json.toString());
                writer.flush();
            }

            int code = con.getResponseCode();
            if (code != 200) {
                throw new Exception("Código de resposta HTTP: " + code);
            }

            ShindoLogger.info("[API] Evento enviado com sucesso.");
        } catch (Exception e) {
            ShindoLogger.error("[API] Falha ao enviar evento, adicionando à fila: " + e.getMessage(), e);
        }
    }
}

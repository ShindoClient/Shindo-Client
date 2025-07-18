package me.miki.shindo.websocket;

import me.miki.shindo.logger.ShindoLogger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class ShindoWebSocket extends WebSocketClient {

    public ShindoWebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("[SC] New Connection Opened");
        ShindoLogger.info("[WS] New Connection Opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        ShindoLogger.info("[WS] closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        ShindoLogger.info("[WS] received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        ShindoLogger.info("[WS] received ByteBuffer");
    }


    @Override
    public void onError(Exception ex) {
        ShindoLogger.error("[WS] an error occurred:" + ex);
    }

}

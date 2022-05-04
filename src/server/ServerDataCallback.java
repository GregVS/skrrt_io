package server;

import game.BufferBuilder;
import game.GameEngine;
import org.java_websocket.WebSocket;

import java.util.HashMap;

/**
 * Created by Gregory on 7/30/17.
 */
public class ServerDataCallback extends MessageCallback {


    public ServerDataCallback(HashMap<WebSocket, ClientProfile> connections, GameEngine gameEngine) {
        super(connections, gameEngine);
    }

    @Override
    public void call() {
        SendThread.messages.add(new MsgPair(conn, BufferBuilder.createServerDataByteBuffer( connections.size())));
    }
}

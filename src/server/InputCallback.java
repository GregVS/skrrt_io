package server;

import buffers.InputPacketBuffer;
import game.GameEngine;
import org.java_websocket.WebSocket;

import java.util.HashMap;

/**
 * Created by Gregory on 6/11/17.
 */
public class InputCallback extends MessageCallback {

    public InputCallback(HashMap<WebSocket, ClientProfile> connections, GameEngine gameEngine) {
        super(connections, gameEngine);
    }

    @Override
    public void call() {
        ClientProfile cp = connections.get(conn);
        if(cp == null) {
            return;
        }
        InputPacketBuffer buf = (InputPacketBuffer) table;
        this.gameEngine.attachToInputPacket(cp.getId(), buf);
    }
}

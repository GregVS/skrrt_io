package server;

import game.BufferBuilder;
import game.GameEngine;
import org.java_websocket.WebSocket;

import java.util.HashMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class LeaveCallback extends MessageCallback {

    public static int games = 0;

    public LeaveCallback(HashMap<WebSocket, ClientProfile> connections, GameEngine gameEngine) {
        super(connections, gameEngine);
    }

    @Override
    public void call() {
        games++;
        if (connections.get(conn) == null) return;
        ClientProfile profile = connections.get(conn);

        if(conn.isOpen()) SendThread.messages.add(new MsgPair(conn, BufferBuilder.createDeathBuffer(profile)));

        System.out.println(profile.getPlayer().getName() + " got " + profile.getPlayer().getStats().getXp() + " and Level: " + profile.getPlayer().getStats().getLevel());
        gameEngine.removePlayerEntity(profile.getId());
        connections.remove(conn);

        gameEngine.updateBotCount(connections.size());
    }
}

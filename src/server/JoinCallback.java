package server;

import buffers.JoinDataBuffer;
import entities.Player;
import game.BufferBuilder;
import game.GameEngine;
import game.StaticGlobals;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class JoinCallback extends MessageCallback {


    public JoinCallback(HashMap<WebSocket, ClientProfile> connections, GameEngine gameEngine) {
        super(connections, gameEngine);
    }

    @Override
    public void call() {
        rejectIfFull();
        if(this.connections.containsKey(conn)) return; //exists already
        int id = StaticGlobals.IDCOUNTER.getAndIncrement();
        Player player = gameEngine.addPlayerEntity(id, readPlayerName());
        ClientProfile cp = new ClientProfile(conn, player);
        this.connections.put(conn, cp);
        gameEngine.updateBotCount(connections.size());
    }

    private void rejectIfFull() {
        if(this.connections.size() >= GameServer.MAX_PLAYERS) {
            SendThread.messages.add(new MsgPair(conn, BufferBuilder.createInfoMessage("reject")));
        }
    }

    private String readPlayerName() {
        String name = ((JoinDataBuffer) table).name();
        if(name == null || name.equals("")) name = "skrrt.io";
        name = name.trim();
        if(name.length() > 15) name = name.substring(0, 15);
//        System.out.println(name + " has joined");
        return name;
    }
}

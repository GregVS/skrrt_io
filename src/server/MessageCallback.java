package server;

import com.google.flatbuffers.Table;
import game.GameEngine;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Gregory on 6/9/17.
 */
public abstract class MessageCallback {

    public static LinkedBlockingQueue<MessageCallback> scheduledCallbacks = new LinkedBlockingQueue(); //static
    protected WebSocket conn;
    protected Table table;

    protected HashMap<WebSocket, ClientProfile> connections;
    protected GameEngine gameEngine;

    public MessageCallback(HashMap<WebSocket, ClientProfile> connections, GameEngine gameEngine) {
        this.connections = connections;
        this.gameEngine = gameEngine;
    }

    public abstract void call();

    public void schedule(WebSocket conn, Table table) {
        this.conn = conn;
        this.table = table;
        scheduledCallbacks.add(this);
    }

}

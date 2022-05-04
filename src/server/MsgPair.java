package server;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Created by Gregory on 8/13/17.
 */
public class MsgPair {

    private WebSocket conn;
    private ByteBuffer msg;

    public MsgPair(WebSocket conn, ByteBuffer msg) {
        this.conn = conn;
        this.msg = msg;
    }

    public WebSocket getConn() {
        return conn;
    }

    public ByteBuffer getMsg() {
        return msg;
    }
}

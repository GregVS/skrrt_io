package server;

import buffers.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class WebsocketServerHandler extends WebSocketServer {
    public static final byte JOIN_BYTE = 0;
    public static final byte LEAVE_BYTE = 1;
    public static final byte INPUT_BYTE = 2;
    public static final byte SERVERDATA_BYTE = 3;

    private HashMap<Byte, MessageCallback> messageCallbackMap = new HashMap<>();

    public WebsocketServerHandler(int port) {
        super(new InetSocketAddress(port));
    }

    public void registerMessageCallback(Byte signal, MessageCallback callback) {
        messageCallbackMap.put(signal, callback);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        messageCallbackMap.get(LEAVE_BYTE).schedule(conn, null);
        conn.close();
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        message.rewind();
        MessageBuffer buffer = MessageBuffer.getRootAsMessageBuffer(message);
        switch(buffer.messageType()) {
            case MessageUnion.InputPacketBuffer:
                messageCallbackMap.get(INPUT_BYTE).schedule(conn, buffer.message(new InputPacketBuffer()));
                break;
            case MessageUnion.JoinDataBuffer:
                messageCallbackMap.get(JOIN_BYTE).schedule(conn, buffer.message(new JoinDataBuffer()));
                break;
            case MessageUnion.ServerDataBuffer:
                messageCallbackMap.get(SERVERDATA_BYTE).schedule(conn, buffer.message(new ServerDataBuffer()));
                break;
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {} //ignore strings

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {}

}

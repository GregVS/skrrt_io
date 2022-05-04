package server;

import entities.Player;
import org.java_websocket.WebSocket;

/**
 * Created by Gregory on 7/13/17.
 */
public class ClientProfile {

    private WebSocket socket;
    private Player player;

    public ClientProfile(WebSocket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }

    @Override
    public int hashCode() {
        return socket.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientProfile && socket.equals(((ClientProfile) obj).socket);
    }

    public int getId() {
        return player.getId();
    }

    public Player getPlayer() {
        return player;
    }
}

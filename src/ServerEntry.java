import server.GameServer;

import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * Created by Gregory on 6/9/17.
 */
public class ServerEntry {

    public static void main(String[] args) throws SQLException, UnknownHostException {
        GameServer gameServer = new GameServer();
        gameServer.start();
    }

}

package server;

import game.GameEngine;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gregory on 6/9/17.
 */
public class GameServer extends Thread {

    public static final int MAX_PLAYERS = 50;
    private GameEngine gameEngine = new GameEngine();

    private CommandLineThread cmdThread = new CommandLineThread();
    private WebsocketServerHandler server = new WebsocketServerHandler(Integer.parseInt(System.getenv("PORT")));

    private HashMap<WebSocket, ClientProfile> connections = new HashMap<>();
    private LeaveCallback leaveCallback;

    private SendThread sendThread = new SendThread();

    public void run() {
        System.out.println("Server started @ " + server.getAddress());
        leaveCallback = new LeaveCallback(connections, gameEngine);
        registerServerCallbacks();
        server.start();
        cmdThread.start();
        sendThread.start();
        try {
            startGameLoop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerServerCallbacks() {
        server.registerMessageCallback(WebsocketServerHandler.JOIN_BYTE, new JoinCallback(connections, gameEngine));
        server.registerMessageCallback(WebsocketServerHandler.LEAVE_BYTE, leaveCallback);
        server.registerMessageCallback(WebsocketServerHandler.INPUT_BYTE, new InputCallback(connections, gameEngine));
        server.registerMessageCallback(WebsocketServerHandler.SERVERDATA_BYTE, new ServerDataCallback(connections, gameEngine));
    }

    private void startGameLoop() throws InterruptedException {
        gameEngine.spawnStaticEntities();

        TickTimer packetTimer = new TickTimer(1000 / 15);
        TickTimer updateTimer = new TickTimer(1000 / 66);

        while(true) {
            processCommands(); //execute the command line things
            processServerCallbacks();

            //update the game engine
            if(updateTimer.shouldUpdate()) {
                GameTimeManager.update();
                gameEngine.update();
            }

            //send data to the clients 20 times a second
            if(packetTimer.shouldUpdate()) {
                sendUpdateToClients();
            }
            Thread.sleep(1);
        }
    }

    private void processCommands() {
        for(int i = 0; i < CommandLineThread.queuedCommands.size(); i++) {
            processCommand(CommandLineThread.queuedCommands.poll());
        }
    }

    //I know this is ugly but it works, okay?
    private void processCommand(String cmd) {
        if(cmd.equalsIgnoreCase("list bots")) {
            gameEngine.logBots();
        } else if(cmd.equalsIgnoreCase("count bots")) {
            gameEngine.logBotCount();
        } else if(cmd.equalsIgnoreCase("spawn bot")) {
            gameEngine.initBots(1);
            System.out.println("Bot has been spawned");
        } else if(cmd.equalsIgnoreCase("clear bots")) {
            gameEngine.clearBots();
        } else if (cmd.startsWith("set max bots")) {
            String[] splitArr = cmd.split("\\s+");
            gameEngine.setMaxBots(Integer.parseInt(splitArr[splitArr.length - 1]), connections.size());
            System.out.println("Max bot count set to " + gameEngine.getMaxBots() + "\nYou may have to clear bots for the population to change immediately");
        }else if(cmd.equalsIgnoreCase("count players")) {
            System.out.println("Real players online: " + connections.size());
        } else if(cmd.equalsIgnoreCase("list players")) {
            System.out.println("Players: ");
            for (Map.Entry<WebSocket, ClientProfile> entry : connections.entrySet()) {
                System.out.println(entry.getValue().getPlayer().getName());
            }
        } else if (cmd.equalsIgnoreCase("count games")) {
            System.out.println("Games played: " + LeaveCallback.games);
        } else {
            System.out.println("Command was not recognized");
        }
    }

    private void sendUpdateToClients() {
        for (Map.Entry<WebSocket, ClientProfile> entry : connections.entrySet()) {
            if (gameEngine.getDeadClients().contains(entry.getValue().getId())) {
                gameEngine.getDeadClients().remove(entry.getValue().getId());
                leaveCallback.schedule(entry.getKey(), null);
                continue;
            }

            try {
                sendUpdateToClient(entry.getValue().getId(), entry.getKey());
            } catch (WebsocketNotConnectedException e) {
                System.out.println("Client left - websocket not connected");
                gameEngine.getDeadClients().add(entry.getValue().getId());
            }
        }
    }

    private void sendUpdateToClient(Integer id, WebSocket conn) {
        ByteBuffer byteBuffer = gameEngine.buildSnapshotForPlayer(id);
        if(byteBuffer == null) {
            return;
        }
        SendThread.messages.add(new MsgPair(conn, byteBuffer));
    }

    private void processServerCallbacks() {
        for(int i = 0; i < MessageCallback.scheduledCallbacks.size(); i++) {
            MessageCallback.scheduledCallbacks.poll().call();
        }
    }

}

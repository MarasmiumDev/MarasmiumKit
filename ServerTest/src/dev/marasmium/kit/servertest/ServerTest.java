/**
 * File:        ServerTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's network server test program
 */

package dev.marasmium.kit.servertest;

import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.applib.networking.NetServer;
import dev.marasmium.kit.netinterfacetest.NetMessageTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The main class of the MarasmiumKit's network server test program
 */
public class ServerTest implements NetListener {

    /**
     * Scanner for command line input
     */
    private Scanner commandLine = null;
    /**
     * Whether the server should continue running
     */
    private volatile boolean running = false;
    /**
     * The MarasmiumKit network server managed by the server test program
     */
    private NetServer server = null;
    /**
     * Log source flag for the test server
     */
    private final LogSource logSource = new LogSource("Test Server");
    /**
     * Thread for taking in command line input
     */
    private Thread commandThread = null;
    /**
     * Metrics for the set of players currently connected to the server
     */
    private HashMap<Integer, PlayerMetrics> playerMetrics = new HashMap<>();

    /**
     * The entry point of the server test program
     */
    static void main() {
        Scanner commandLine = new Scanner(System.in);
        System.out.print("Port: ");
        int port;
        try {
            port = Integer.parseInt(commandLine.nextLine());
        } catch (NoSuchElementException | IllegalStateException | NumberFormatException _) {
            System.out.println("Invalid port");
            return;
        }
        System.out.print("Maximum clients: ");
        int maximumClients;
        try {
            maximumClients = Integer.parseInt(commandLine.nextLine());
        } catch (NoSuchElementException | NumberFormatException _) {
            System.out.println("Invalid maximum clients");
            return;
        }
        ServerTest serverTest = new ServerTest();
        if (serverTest.initialize(commandLine, port, maximumClients)) {
            serverTest.run();
        } else {
            System.out.println("Failed to initialize server test");
        }
        if (!serverTest.destroy()) {
            System.out.println("Failed to destroy server test");
        }
    }

    /**
     * Callback for a client connection to the server
     * @param clientID The ID number of the connected network connection
     * @return Whether the connection should be accepted
     */
    @Override
    public boolean netConnected(int clientID) {
        if (server == null) {
            return false;
        }
        if (playerMetrics == null) {
            return false;
        }
        server.getLog().write(logSource, LogLevel.Info, "Client " + clientID + " connected");
        playerMetrics.put(clientID, new PlayerMetrics());
        playerMetrics.get(clientID).initialize(Vector.Zero(), Vector.Zero());
        return true;
    }

    /**
     * Callback for a message received from a client
     * @param clientID The ID number of the network connection
     * @param message The message received
     */
    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        if (server == null) {
            return;
        }
        if (playerMetrics == null) {
            return;
        }
        if (message == null) {
            return;
        }
        server.getLog().write(logSource, LogLevel.Info, "Client " + clientID + " sent message " + message);
        if (message.getType() == NetMessageTypes.Client_Hello) {
            NetMessage helloMessage = new NetMessage();
            helloMessage.initialize(NetMessageTypes.Server_Hello);
            helloMessage.addData(clientID);
            ArrayList<Integer> clientIDs = server.getClientIDs();
            for (Integer id : clientIDs) {
                if (id != clientID) {
                    helloMessage.addData(id);
                    helloMessage.addData(playerMetrics.get(id).getPosition().clone());
                    helloMessage.addData(playerMetrics.get(id).getVelocity().clone());
                }
            }
            server.send(helloMessage, clientID);
            NetMessage clientConnectedMessage = new NetMessage();
            clientConnectedMessage.initialize(NetMessageTypes.Server_Client_Connected);
            clientConnectedMessage.addData(clientID);
            server.broadcast(clientConnectedMessage, clientID);
        } else if (message.getType() == NetMessageTypes.Client_Movement) {
            Vector position = (Vector)message.removeData();
            Vector velocity = (Vector)message.removeData();
            playerMetrics.get(clientID).setPosition(position);
            playerMetrics.get(clientID).setVelocity(velocity);
            NetMessage movementMessage = new NetMessage();
            movementMessage.initialize(NetMessageTypes.Client_Movement);
            movementMessage.addData(position.clone());
            movementMessage.addData(velocity.clone());
            movementMessage.addData(clientID);
            server.broadcast(movementMessage, clientID);
        }
    }

    /**
     * Callback for a client disconnection from the server
     * @param clientID The ID number of the disconnected network connection
     */
    @Override
    public void netDisconnected(int clientID) {
        if (server == null) {
            return;
        }
        if (playerMetrics == null) {
            return;
        }
        server.getLog().write(logSource, LogLevel.Info, "Client " + clientID + " disconnected");
        NetMessage clientDisconnectedMessage = new NetMessage();
        clientDisconnectedMessage.initialize(NetMessageTypes.Server_Client_Disconnected);
        clientDisconnectedMessage.addData(clientID);
        server.broadcast(clientDisconnectedMessage, clientID);
        playerMetrics.remove(clientID);
    }

    /**
     * Initialize the test server's memory and prepare to start the server
     * @param commandLine The scanner for command line input
     * @param port The port for the server to listen for client connections on
     * @param maximumClients The maximum number of clients which can be connected to the server
     * @return Whether the server was initialized successfully
     */
    public boolean initialize(Scanner commandLine, int port, int maximumClients) {
        if (commandLine == null) {
            return false;
        }
        this.commandLine = commandLine;
        server = new NetServer();
        if (!server.initialize("yyyy.MM.dd@HH:mm:ss.SSS", true, true, "MarasmiumKit-Server.log", this, port,
                maximumClients, -1)) {
            return false;
        }
        server.getLog().write(logSource, LogLevel.Info, "Initialized network server");
        commandThread = new Thread(this::takeCommands);
        server.getLog().write(logSource, LogLevel.Info, "Initialized command-accepting thread");
        return true;
    }

    /**
     * Start the test server
     */
    public void run() {
        if (server == null) {
            return;
        }
        if (commandThread == null) {
            return;
        }
        running = true;
        server.getLog().write(logSource, LogLevel.Info, "Starting server");
        server.run();
        server.getLog().write(logSource, LogLevel.Info, "Starting command-accepting thread");
        commandThread.start();
        server.getLog().write(logSource, LogLevel.Info, "Starting client update thread");
        while (running) {
            server.update();
        }
        server.getLog().write(logSource, LogLevel.Info, "Client update thread stopped");
    }

    /**
     * Stop the test server and free its memory
     * @return Whether the test server was destroyed successfully
     */
    public boolean destroy() {
        boolean success = true;
        commandLine = null;
        running = false;
        if (commandThread != null) {
            commandThread.interrupt();
            try {
                commandThread.join();
            } catch (InterruptedException _) {
                if (server != null) {
                    server.getLog().write(logSource, LogLevel.Warning, "Failed to stop command-accepting thread");
                }
                success = false;
            }
            commandThread = null;
        }
        if (server != null) {
            server.getLog().write(logSource, LogLevel.Info, "Destroying network server");
            if (!server.destroy()) {
                success = false;
            }
            server = null;
        }
        for (HashMap.Entry<Integer, PlayerMetrics> entry : playerMetrics.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().destroy();
            }
        }
        playerMetrics.clear();
        playerMetrics = null;
        return success;
    }

    /**
     * Read commands from the command line until the server is stopped
     */
    private void takeCommands() {
        if (commandLine == null) {
            return;
        }
        if (server == null) {
            return;
        }
        while (true) {
            String command;
            try {
                command = commandLine.nextLine();
            } catch (NoSuchElementException | IllegalStateException _) {
                server.getLog().write(logSource, LogLevel.Warning, "Failed to read command from terminal");
                break;
            }
            if (command.equals("stop")) {
                server.getLog().write(logSource, LogLevel.Info, "Stopping network server");
                break;
            }
        }
        running = false;
    }

}

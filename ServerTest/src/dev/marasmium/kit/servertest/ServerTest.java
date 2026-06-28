/**
 * File:        ServerTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.19
 * Purpose:     Defines the main class of the MarasmiumKit server test program
 */

package dev.marasmium.kit.servertest;

import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.*;
import dev.marasmium.kit.netinterfacetest.MessageTypes;

import java.util.Scanner;

public class ServerTest implements NetListener {

    private volatile boolean running = false;
    private NetServer server = null;
    private final LogSource logSource = new LogSource("Test Server");
    private Thread commandThread = null;

    public boolean initialize(NetServerConfig config) {
        // Initialize the network server
        server = new NetServer();
        if (!server.initialize(config)) {
            return false;
        }
        server.getLog().write(logSource, LogLevel.Info, "Initialized network server");
        // Initialize the command-accepting thread
        commandThread = new Thread(() -> {
            command();
        });
        server.getLog().write(logSource, LogLevel.Info, "Initialized command-accepting thread");
        return true;
    }

    public void run() {
        running = true;
        // Launch the server and start updating it and accepting commands
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

    public boolean destroy() {
        boolean success = true;
        running = false;
        // Stop accepting commands
        if (commandThread != null) {
            commandThread.interrupt();
            try {
                commandThread.join();
            } catch (Exception _) {
                server.getLog().write(logSource, LogLevel.Warning, "Failed to stop command-accepting thread");
                success = false;
            }
            commandThread = null;
        }
        // Stop the server
        server.getLog().write(logSource, LogLevel.Info, "Destroying server");
        if (!server.destroy()) {
            success = false;
        }
        server = null;
        return success;
    }

    private void command() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Take a command
            String command;
            try {
                command = scanner.nextLine();
            } catch (Exception _) {
                server.getLog().write(logSource, LogLevel.Error, "Failed to read command from terminal");
                break;
            }
            if (command.equals("list")) {
                // List all client IDs currently connected
                server.getLog().write(logSource, LogLevel.Info, "Clients: ", server.getClientIDs());
            }
            else if (command.startsWith("kick ")) {
                String IDstr = command.substring("kick ".length());
                int ID = Integer.parseInt(IDstr);
                if (server.removeClient(ID)) {
                    server.getLog().write(logSource, LogLevel.Info, "Removed client ", ID);
                } else {
                    server.getLog().write(logSource, LogLevel.Warning, "Failed to remove client ", ID);
                }
            }
            else if (command.startsWith("send ")) {
                // Broadcast a message to all clients
                String messageData = command.substring("send ".length());
                NetMessage message = new NetMessage(MessageTypes.Message);
                message.addData(messageData);
                if (server.broadcast(message)) {
                    server.getLog().write(logSource, LogLevel.Info, "Broadcasted message to all clients");
                } else {
                    server.getLog().write(logSource, LogLevel.Warning, "Failed to broadcast message to all clients");
                }
            } else if (command.equals("stop")) {
                // Stop the server
                server.getLog().write(logSource, LogLevel.Info, "Used requested server should stop");
                break;
            }
        }
        running = false;
    }

    static void main() {
        // Get the port to listen for connections on
        Scanner scanner = new Scanner(System.in);
        System.out.print("Log index: ");
        String logIndex = scanner.nextLine();
        System.out.print("Port: ");
        String portStr = scanner.nextLine();
        int port = Integer.parseInt(portStr);
        System.out.print("Max clients: ");
        String maxClientsStr = scanner.nextLine();
        int maxClients = Integer.parseInt(maxClientsStr);
        // Set up server
        ServerTest serverTest = new ServerTest();
        NetServerConfig config = new NetServerConfig(serverTest, port);
        config.maxClients = maxClients;
        config.log.fileOutputPath = "MarasmiumKit-Server-" + logIndex + ".log";
        if (serverTest.initialize(config)) {
            serverTest.run();
        } else {
            System.out.println("Failed to initialize test server");
        }
        if (!serverTest.destroy()) {
            System.out.println("Failed to destroy test server");
        }
    }

    @Override
    public boolean netConnected(int clientID) {
        // Notify other clients of the new connection
        server.getLog().write(logSource, LogLevel.Info, "New client ", clientID, " connected");
        NetMessage connectionMessage = new NetMessage(MessageTypes.Connection);
        connectionMessage.addData(clientID);
        server.broadcast(connectionMessage, clientID);
        // Welcome the new client with the list of current clients
        NetMessage welcomeMessage = new NetMessage(MessageTypes.Welcome);
        for (int ID : server.getClientIDs()) {
            if (ID != clientID) {
                welcomeMessage.addData(ID);
            }
        }
        server.send(welcomeMessage, clientID);
        return true;
    }

    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        // Give the client ID and notify other clients of the message
        message.addData(clientID);
        server.broadcast(message, clientID);
    }

    @Override
    public void netDisconnected(int clientID) {
        // Notify other clients of the disconnection
        server.getLog().write(logSource, LogLevel.Info, "Client ", clientID, " disconnected");
        NetMessage message = new NetMessage(MessageTypes.Disconnection);
        message.addData(clientID);
        server.broadcast(message, clientID);
    }

}

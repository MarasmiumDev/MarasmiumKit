/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.netinterfacetest.MessageTypes;

import java.util.ArrayList;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    public String hostName;
    public int port;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        return true;
    }

    @Override
    public boolean processInput() {
        // Switch to scene two on 2 key pressed
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Two)) {
            App.SetCurrentScene(AppTest.Test_Scene_2);
        }
        // Test network client functions
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.C)) {
            // Attempt connection to server
            if (App.Network.connect(hostName, port, 10)) {
                App.Log.write(logSource, LogLevel.Info, "Attempting connection to server at ", hostName, ":", port);
            } else {
                App.Log.write(logSource, LogLevel.Warning, "Failed to start connection to server");
            }
        }
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
            // Attempt to send position message
            if (App.Network.isConnected()) {
                NetMessage message = new NetMessage(MessageTypes.Position);
                Vec2D position = App.Input.mouse.getCursorPosition();
                message.addData(position);
                if (App.Network.send(message)) {
                    App.Log.write(logSource, LogLevel.Info, "Dispatched message to server");
                } else {
                    App.Log.write(logSource, LogLevel.Warning, "Failed to dispatch message to server");
                }
            } else {
                App.Log.write(logSource, LogLevel.Warning, "Cannot send messages, not connected to server");
            }
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.D)) {
            // Attempt disconnection from server
            if (App.Network.disconnect()) {
                App.Log.write(logSource, LogLevel.Info, "Disconnected from server");
            } else {
                App.Log.write(logSource, LogLevel.Warning, "Failed to disconnect safely from server");
            }
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.S)) {
            if (App.Network.isConnected()) {
                App.Log.write(logSource, LogLevel.Info, "Sending many messages rapidly");
                new Thread(() -> {
                    int count = 100;
                    for (int i = 0; i < count; i++) {
                        App.Network.send(new NetMessage(count + i));
                    }
                }).start();
            } else {
                App.Log.write(logSource, LogLevel.Info, "Cannot send messages, not connected to server");
            }
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.X)) {
            App.Log.write(logSource, LogLevel.Info, "Connecting/disconnecting rapidly");
            new Thread(() -> {
                int count = 10;
                for (int i = 0; i < count; i++) {
                    App.Network.connect(hostName, port, 10);
                    App.Network.disconnect();
                }
            }).start();
        }
        return true;
    }

    @Override
    public void update(double deltaFrames) {

    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }

    @Override
    public boolean netConnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Network client connected");
        return true;
    }

    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        // Determine message type and process
        if (message.getType() == MessageTypes.Connection) {
            int connectedClientID = (int) message.removeData();
            App.Log.write(logSource, LogLevel.Info, "New client ", connectedClientID, " connected to server");
        } else if (message.getType() == MessageTypes.Welcome) {
            ArrayList<Integer> connectedClientIDs = new ArrayList<>();
            Object obj;
            while ((obj = message.removeData()) != null) {
                connectedClientIDs.add((int)obj);
            }
            App.Log.write(logSource, LogLevel.Info, "Clients already connected: ", connectedClientIDs);
        } else if (message.getType() == MessageTypes.Disconnection) {
            int disconnectedClientID = (int)message.removeData();
            App.Log.write(logSource, LogLevel.Info, "Client ", disconnectedClientID, " disconnected from server");
        } else if (message.getType() == MessageTypes.Position) {
            Vec2D clientPosition = (Vec2D)message.removeData();
            int positionedClientID = (int)message.removeData();
            App.Log.write(logSource, LogLevel.Info, "Client ", positionedClientID, " gave position ", clientPosition);
        } else if (message.getType() == MessageTypes.Message) {
            String messageData = (String)message.removeData();
            App.Log.write(logSource, LogLevel.Info, "Server says: \"", messageData, "\"");
        } else {
            App.Log.write(logSource, LogLevel.Warning, "Unknown message type from client: ", clientID, ": ", message);
        }
    }

    @Override
    public void netDisconnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Network client disconnected");
    }

}

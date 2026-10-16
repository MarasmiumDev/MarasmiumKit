/**
 * File:        ConnectScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.13
 * Purpose:     Defines the information scene for connecting to the MarasmiumKit application framework's test server
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Label;
import dev.marasmium.kit.uilib.component.TextBox;

import java.util.ArrayList;

public class ConnectScene extends Scene implements UIListener, NetListener {

    private final LogSource logSource = new LogSource("Connect Scene");
    private final UIGroup connectUI = new UIGroup();
    private final Label connectLabel = new Label();
    private final TextBox addressTextBox = new TextBox();
    private final TextBox portTextBox = new TextBox();
    private final UIGroup controlsUI = new UIGroup();
    private final Button backButton = new Button();
    private final Button connectButton = new Button();
    private final UIGroup connectingUI = new UIGroup();
    private final Label connectingLabel = new Label();
    private final Camera sparkCamera = new Camera();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private float sparkTimer = 0.0f;
    private boolean connecting = false;

    @Override
    protected boolean initialize() {
        // Declare component animations
        final String[] textBoxAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Text_Box/Text_Box_Unselected.animation",
                "Assets/Animations/UI/Text_Box/Text_Box_Selected.animation",
        };
        final String textBoxCursorAnimationFilePath = "Assets/Animations/UI/Text_Box/Text_Box_Cursor.animation";
        final String[] buttonAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation",
        };
        // Initialize connection UI components
        connectUI.initialize(this, 0, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.20f);
        connectUI.addComponent(connectLabel);
        connectUI.addComponent(addressTextBox);
        connectUI.addComponent(portTextBox);
        connectLabel.initialize(Vector.Cartesian(0.33f, 0.75f), Vector.Cartesian(0.33f, 0.1f), "", "CONNECT TO SERVER",
                Vector.Zero(), Alignment.Center, Alignment.Center);
        addressTextBox.initialize(Vector.Cartesian(0.25f, 0.50f), Vector.Cartesian(0.50f, 0.10f),
                textBoxAnimationFilePaths, "HOST ADDRESS", Alignment.Top, Vector.Cartesian(0.01f, 0.9f),
                textBoxCursorAnimationFilePath, Alignment.Center, null, -1, 8, 1.5f);
        portTextBox.initialize(Vector.Cartesian(0.25f, 0.35f), Vector.Cartesian(0.50f, 0.10f),
                textBoxAnimationFilePaths, "PORT", Alignment.Top, Vector.Cartesian(0.01f, 0.9f),
                textBoxCursorAnimationFilePath, Alignment.Center, "0123456789", 5, 8, 1.5f);
        // Initialize controls UI components
        controlsUI.initialize(this, 1, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        controlsUI.addComponent(backButton);
        controlsUI.addComponent(connectButton);
        backButton.initialize(Vector.Cartesian(0.05f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "BACK", Alignment.Center);
        connectButton.initialize(Vector.Cartesian(0.70f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "CONNECT", Alignment.Center);
        // Initialize connecting UI components
        connectingUI.initialize(this, 2, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        connectingUI.addComponent(connectingLabel);
        connectingLabel.initialize(Vector.Cartesian(0.25f, 0.5f), Vector.Cartesian(0.5f, 0.1f), "", "CONNECTING...",
                Vector.Zero(), Alignment.Center, Alignment.Center);
        // Initialize camera
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        connecting = false;
        connectUI.setEnabled(true);
        connectUI.setVisible(true);
        connectButton.setEnabled(true);
        connectingUI.setEnabled(false);
        connectingUI.setVisible(false);
        addressTextBox.setText("");
        portTextBox.setText("");
        return true;
    }

    @Override
    public boolean processInput() {
        connectUI.processInput();
        controlsUI.processInput();
        connectingUI.processInput();
        return true;
    }

    @Override
    public void draw() {
        connectUI.draw();
        controlsUI.draw();
        connectingUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    @Override
    public void update(float deltaFrames) {
        connectUI.update(deltaFrames);
        controlsUI.update(deltaFrames);
        connectingUI.update(deltaFrames);
        sparkTimer += deltaFrames;
        if (sparkTimer > (float)App.Graphics.getTargetFPS() / 20.0f) {
            sparkTimer = 0.0f;
            Spark spark = new Spark();
            spark.initialize();
            sparks.add(spark);
        }
        sparkCamera.setPosition(App.Window.getDimensions().scalarMultiply(0.5f));
        sparkCamera.update(deltaFrames);
        for (Spark spark : sparks) {
            spark.update(deltaFrames);
        }
        sparks.removeIf(spark -> !spark.isAlive());
    }

    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    @Override
    protected boolean destroy() {
        connectUI.destroy();
        controlsUI.destroy();
        connectingUI.destroy();
        for (Spark spark : sparks) {
            spark.destroy();
        }
        sparks.clear();
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (groupID == controlsUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                if (componentID == backButton.getComponentID()) {
                    if (!connecting) {
                        App.SetCurrentScene(AppTest.Title_Scene);
                    } else {
                        App.Network.disconnect();
                        connecting = false;
                        connectUI.setEnabled(true);
                        connectUI.setVisible(true);
                        connectButton.setEnabled(true);
                        connectingUI.setEnabled(false);
                        connectingUI.setVisible(false);
                    }
                } else if (componentID == connectButton.getComponentID()) {
                    String address = addressTextBox.getText();
                    if (address == null) {
                        return;
                    }
                    if (address.isEmpty()) {
                        return;
                    }
                    String portStr = portTextBox.getText();
                    if (portStr == null) {
                        return;
                    }
                    int port;
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (NumberFormatException _) {
                        return;
                    }
                    connecting = true;
                    connectUI.setEnabled(false);
                    connectUI.setVisible(false);
                    connectButton.setEnabled(false);
                    connectingUI.setEnabled(true);
                    connectingUI.setVisible(true);
                    App.Network.connect(address, port, 10);
                }
            }
        }
    }

    @Override
    public boolean netConnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Connected to server");
        return true;
    }

    @Override
    public void netDisconnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Disconnected from server");
        connecting = false;
        connectUI.setEnabled(true);
        connectUI.setVisible(true);
        connectButton.setEnabled(true);
        connectingUI.setEnabled(false);
        connectingUI.setVisible(false);
    }

}

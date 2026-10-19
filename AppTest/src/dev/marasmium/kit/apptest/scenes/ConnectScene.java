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
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Label;
import dev.marasmium.kit.uilib.component.TextBox;

import java.util.ArrayList;

/**
 * The connect scene for the MarasmiumKit application framework's app test program
 */
public class ConnectScene extends Scene implements UIListener, NetListener {

    /**
     * UI group for main UI components
     */
    private final UIGroup connectUI = new UIGroup();
    /**
     * Main label for the connect scene
     */
    private final Label connectLabel = new Label();
    /**
     * Text box for the server's IP address
     */
    private final TextBox addressTextBox = new TextBox();
    /**
     * Text box for the port to connect to the server on
     */
    private final TextBox portTextBox = new TextBox();
    /**
     * UI group for scene control UI components
     */
    private final UIGroup controlsUI = new UIGroup();
    /**
     * Button to go back to the title scene or cancel a connection to the server
     */
    private final Button backButton = new Button();
    /**
     * Button to attempt a connection to the server
     */
    private final Button connectButton = new Button();
    /**
     * UI group for the connecting status label
     */
    private final UIGroup connectingUI = new UIGroup();
    /**
     * Label to indicate a connection attempt to the server
     */
    private final Label connectingLabel = new Label();
    /**
     * Camera for the background visual artifacts in the connect scene
     */
    private final Camera sparkCamera = new Camera();
    /**
     * The set of background sparks in the connect scene
     */
    private final ArrayList<Spark> sparks = new ArrayList<>();
    /**
     * Timer for adding new background sparks
     */
    private float sparkTimer = 0.0f;
    /**
     * Whether the app test program is currently attempting a connection to the server
     */
    private boolean connecting = false;

    /**
     * Initialize the connect scene's memory and UI components
     * @return Whether the connect scene was initialized successfully
     */
    @Override
    protected boolean initialize() {
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
        controlsUI.initialize(this, 1, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        controlsUI.addComponent(backButton);
        controlsUI.addComponent(connectButton);
        backButton.initialize(Vector.Cartesian(0.05f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "BACK", Alignment.Center);
        connectButton.initialize(Vector.Cartesian(0.70f, 0.05f), Vector.Cartesian(0.25f, 0.10f),
                buttonAnimationFilePaths, "CONNECT", Alignment.Center);
        connectingUI.initialize(this, 2, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        connectingUI.addComponent(connectingLabel);
        connectingLabel.initialize(Vector.Cartesian(0.25f, 0.5f), Vector.Cartesian(0.5f, 0.1f), "", "CONNECTING...",
                Vector.Zero(), Alignment.Center, Alignment.Center);
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        return true;
    }

    /**
     * Enter the connect scene from another scene
     * @param lastScene Reference to the previous scene displayed by the application framework
     * @return Whether the connect scene was entered successfully
     */
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

    /**
     * Process user-input to the connect scene
     * @return Whether the application should continue running
     */
    @Override
    public boolean processInput() {
        connectUI.processInput();
        controlsUI.processInput();
        connectingUI.processInput();
        return true;
    }

    /**
     * Draw the connect scene's graphics
     */
    @Override
    public void draw() {
        connectUI.draw();
        controlsUI.draw();
        connectingUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    /**
     * Update the connect scene's logic and UI components
     * @param deltaFrames The target number of frames to have elapsed since the last update
     */
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

    /**
     * Leave the connect scene from another scene
     * @param nextScene Reference to the next scene to be displayed by the application framework
     * @return Whether the connect scene was left successfully
     */
    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    /**
     * Free the connect scene's memory
     * @return Whether the connect scene was destroyed successfully
     */
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

    /**
     * Callback for a user-interface component event on the options scene
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (event == null) {
            return;
        }
        if (groupID == controlsUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                App.Audio.soundEffects.play("Assets/Audio/Click.audio");
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

    /**
     * Callback for a connection to the server
     * @param clientID The ID number of the connected network connection
     * @return Whether the connection should be accepted
     */
    @Override
    public boolean netConnected(int clientID) {
        App.SetCurrentScene(AppTest.Game_Scene);
        return true;
    }

    /**
     * Callback for a disconnection from the server
     * @param clientID The ID number of the disconnected network connection
     */
    @Override
    public void netDisconnected(int clientID) {
        connecting = false;
        connectUI.setEnabled(true);
        connectUI.setVisible(true);
        connectButton.setEnabled(true);
        connectingUI.setEnabled(false);
        connectingUI.setVisible(false);
    }

}

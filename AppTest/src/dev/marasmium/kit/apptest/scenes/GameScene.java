/**
 * File:        GameScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.16
 * Purpose:     Defines the main game scene of the MarasmiumKit application framework's app test program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Player;
import dev.marasmium.kit.netinterfacetest.NetMessageTypes;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main game scene of the application framework's app test program
 */
public class GameScene extends Scene implements UIListener {

    /**
     * Camera for sprites in the game world
     */
    private final Camera gameCamera = new Camera();
    /**
     * Sprite representing the player
     */
    private final Player player = new Player();
    /**
     * The last recorded velocity of the player in game coordinates
     */
    private final Vector playerVelocity = Vector.Zero();
    /**
     * The client ID of the player assigned by the server
     */
    private int clientID = 0;
    /**
     * Set of sprites representing other players connected to the server
     */
    private final HashMap<Integer, Sprite> partners = new HashMap<>();
    /**
     * Whether the game is currently paused
     */
    private boolean paused = false;
    /**
     * UI group for pause menu components
     */
    private final UIGroup pauseUI = new UIGroup();
    /**
     * Resume button to go back to the game scene from the pause menu
     */
    private final Button resumeButton = new Button();
    /**
     * Quit button to disconnect from the current server and go back to the connect scene
     */
    private final Button quitButton = new Button();

    /**
     * Initialize the main game scene's memory and pause menu components
     * @return Whether the game scene was initialized successfully
     */
    @Override
    protected boolean initialize() {
        gameCamera.initialize(Vector.Zero(), 1.0f, Angle.Zero());
        player.initialize();
        player.playAnimation();
        final String[] buttonAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation",
        };
        pauseUI.initialize(this, 0, 2.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.20f);
        pauseUI.addComponent(resumeButton);
        pauseUI.addComponent(quitButton);
        resumeButton.initialize(Vector.Cartesian(0.33f, 0.50f), Vector.Cartesian(0.33f, 0.1f), buttonAnimationFilePaths,
                "RESUME", Alignment.Center);
        quitButton.initialize(Vector.Cartesian(0.33f, 0.35f), Vector.Cartesian(0.33f, 0.1f), buttonAnimationFilePaths,
                "QUIT", Alignment.Center);
        return true;
    }

    /**
     * Enter the main game scene from another scene
     * @param lastScene Reference to the previous scene displayed by the application framework
     * @return Whether the game scene was connected to a server and was entered successfully
     */
    @Override
    public boolean enter(Scene lastScene) {
        player.setPosition(Vector.Zero());
        clientID = 0;
        partners.clear();
        pauseUI.setEnabled(false);
        pauseUI.setVisible(false);
        paused = false;
        NetMessage helloMessage = new NetMessage();
        helloMessage.initialize(NetMessageTypes.Client_Hello);
        return App.Network.send(helloMessage);
    }

    /**
     * Process user-input to the main game scene
     * @return Whether the application should continue running
     */
    @Override
    public boolean processInput() {
        if (!paused) {
            player.processInput();
        }
        if (!App.Network.isConnected()) {
            netDisconnected(0);
        }
        pauseUI.processInput();
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Escape)) {
            if (paused) {
                pauseUI.setEnabled(false);
                pauseUI.setVisible(false);
                paused = false;
            } else {
                pauseUI.setEnabled(true);
                pauseUI.setVisible(true);
                paused = true;
            }
        }
        return true;
    }

    /**
     * Draw the main game scene's graphics
     */
    @Override
    public void draw() {
        App.Graphics.submit(gameCamera, player);
        App.Graphics.submit(gameCamera, new ArrayList<>(partners.values()));
        pauseUI.draw();
    }

    /**
     * Update the main game scene's logic and UI components
     * @param deltaFrames The target number of frames to have elapsed since the last update
     */
    @Override
    public void update(float deltaFrames) {
        player.update(deltaFrames);
        if (!player.getVelocity().equals(playerVelocity)) {
            NetMessage movementMessage = new NetMessage();
            movementMessage.initialize(NetMessageTypes.Client_Movement);
            movementMessage.addData(player.getPosition().clone());
            movementMessage.addData(player.getVelocity().clone());
            App.Network.send(movementMessage);
            playerVelocity.setX(player.getVelocity().getX());
            playerVelocity.setY(player.getVelocity().getY());
        }
        gameCamera.setPosition(player.getPosition().add(player.getDimensions().scalarMultiply(0.5f)));
        gameCamera.update(deltaFrames);
        for (Sprite partner : partners.values()) {
            partner.update(deltaFrames);
        }
        pauseUI.update(deltaFrames);
    }

    /**
     * Leave the main game scene for another scene
     * @param nextScene Reference to the next scene to be displayed by the application framework
     * @return Whether the main game scene was left successfully
     */
    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    /**
     * Free the main game scene's memory
     * @return Whether the main game scene was destroyed successfully
     */
    @Override
    protected boolean destroy() {
        gameCamera.destroy();
        player.destroy();
        clientID = 0;
        for (Sprite partner : partners.values()) {
            partner.destroy();
        }
        partners.clear();
        pauseUI.destroy();
        return true;
    }

    /**
     * Callback for a message received from the server
     * @param clientID The ID number of the network connection
     * @param message The message received
     */
    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        if (message.getType() == NetMessageTypes.Server_Hello) {
            this.clientID = (int)message.removeData();
            Object obj;
            int partnerID;
            while ((obj = message.removeData()) != null) {
                partnerID = (int)obj;
                Vector position;
                if ((obj = message.removeData()) == null) {
                    break;
                }
                position = (Vector)obj;
                Vector velocity;
                if ((obj = message.removeData()) == null) {
                    break;
                }
                velocity = (Vector)obj;
                addPartner(partnerID, position, velocity);
            }
        } else if (message.getType() == NetMessageTypes.Server_Client_Connected) {
            int partnerID = (int)message.removeData();
            addPartner(partnerID, Vector.Zero(), Vector.Zero());
        } else if (message.getType() == NetMessageTypes.Client_Movement) {
            Vector position = (Vector)message.removeData();
            Vector velocity = (Vector)message.removeData();
            int partnerID = (int)message.removeData();
            if (partnerID == this.clientID) {
                player.setPosition(position.clone());
                player.setVelocity(velocity.clone());
            } else {
                if (partners.containsKey(partnerID)) {
                    partners.get(partnerID).setPosition(position.clone());
                    partners.get(partnerID).setVelocity(velocity.clone());
                }
            }
        } else if (message.getType() == NetMessageTypes.Server_Client_Disconnected) {
            partners.remove((int)message.removeData());
        }
    }

    /**
     * Callback for a disconnection from the server
     * @param clientID The ID number of the disconnected network connection (unused)
     */
    @Override
    public void netDisconnected(int clientID) {
        App.SetCurrentScene(AppTest.Connect_Scene);
    }

    /**
     * Callback for user-interface component events on the pause menu
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (event == null) {
            return;
        }
        if (groupID == pauseUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                App.Audio.soundEffects.play("Assets/Audio/Click.audio");
                if (componentID == resumeButton.getComponentID()) {
                    pauseUI.setEnabled(false);
                    pauseUI.setVisible(false);
                    paused = false;
                } else if (componentID == quitButton.getComponentID()) {
                    App.Network.disconnect();
                }
            }
        }
    }

    /**
     * Add a newly connected player's sprite to the scene
     * @param partnerID The ID of the other player assigned by the server
     * @param position The initial position of the other player in game coordinates
     * @param velocity The initial velocity of the other player in game coordinates
     */
    private void addPartner(int partnerID, Vector position, Vector velocity) {
        if (position == null || velocity == null) {
            return;
        }
        partners.put(partnerID, new Sprite());
        partners.get(partnerID).initialize(position.clone(), 0.5f, Vector.Cartesian(50.0f, 50.0f),
                Angle.Zero(), "Assets/Animations/Partner.animation");
        partners.get(partnerID).setVelocity(velocity.clone());
        partners.get(partnerID).playAnimation();
    }

}

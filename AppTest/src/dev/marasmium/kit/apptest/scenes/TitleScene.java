/**
 * File:        TitleScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     The initial/opening scene of the MarasmiumKit application framework's AppTest program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Label;

import java.util.ArrayList;

/**
 * The title scene of the application framework's test program
 */
public class TitleScene extends Scene implements UIListener {

    /**
     * This scene's log message source flag
     */
    private final LogSource logSource = new LogSource("Title Scene");
    /**
     * The main UI group for the title scene
     */
    private final UIGroup startUI = new UIGroup();
    /**
     * The title label for the title scene
     */
    private final Label label = new Label();
    /**
     * The start button to go to the connect scene
     */
    private final Button startButton = new Button();
    /**
     * The options button to go to the options scene
     */
    private final Button optionsButton = new Button();
    /**
     * The quit button to end the test program
     */
    private final Button quitButton = new Button();
    /**
     * Camera for the sparks appearing in the background of the title scene
     */
    private final Camera sparkCamera = new Camera();
    /**
     * The set of sparks in the background of the title scene
     */
    private final ArrayList<Spark> sparks = new ArrayList<>();
    /**
     * Timer for adding new sparks to the title scene's background
     */
    private float sparkTimer = 0.0f;
    /**
     * Whether the application should continue running
     */
    private boolean running = false;

    /**
     * Initialize the title scene
     * @return Whether the title scene was initialized successfully
     */
    @Override
    public boolean initialize() {
        final String[] buttonAnimations = {
                "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation"
        };
        startUI.initialize(this, 0, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        startUI.addComponent(label);
        startUI.addComponent(startButton);
        startUI.addComponent(optionsButton);
        startUI.addComponent(quitButton);
        label.initialize(Vector.Cartesian(0.33f, 0.75f), Vector.Cartesian(0.33f, 0.1f), "", "APP TEST", Vector.Zero(),
                Alignment.Center, Alignment.Center);
        startButton.initialize(Vector.Cartesian(0.33f, 0.50f), Vector.Cartesian(0.33f, 0.10f), buttonAnimations,
                "START", Alignment.Center);
        optionsButton.initialize(Vector.Cartesian(0.33f, 0.35f), Vector.Cartesian(0.33f, 0.10f), buttonAnimations,
                "OPTIONS", Alignment.Center);
        quitButton.initialize(Vector.Cartesian(0.33f, 0.20f), Vector.Cartesian(0.33f, 0.10f), buttonAnimations,
                "QUIT", Alignment.Center);
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        running = true;
        return true;
    }

    /**
     * Enter the title scene from another scene
     * @param lastScene Reference to the previous scene displayed by the application framework
     * @return Whether the title scene was entered successfully
     */
    @Override
    public boolean enter(Scene lastScene) {
        return true;
    }

    /**
     * Process user-input to the title scene
     * @return Whether the application should continue running
     */
    @Override
    public boolean processInput() {
        startUI.processInput();
        return running;
    }

    /**
     * Draw the title scene's graphics
     */
    @Override
    public void draw() {
        startUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    /**
     * Update the title scene's background sparks and user-interface components
     * @param deltaFrames The target number of frames to have elapsed since the last update
     */
    @Override
    public void update(float deltaFrames) {
        startUI.update(deltaFrames);
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
     * Leave the title scene for another scene
     * @param nextScene Reference to the next scene to be displayed by the application framework
     * @return Whether the title scene was left successfully
     */
    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    /**
     * Free the title scene memory
     * @return Whether the title scene was destroyed successfully
     */
    @Override
    public boolean destroy() {
        startUI.destroy();
        for (Spark spark : sparks) {
            spark.destroy();
        }
        sparks.clear();
        return true;
    }

    /**
     * Callback for a component event on the title scene
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (groupID == startUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                if (componentID == startButton.getComponentID()) {
                    App.SetCurrentScene(AppTest.Connect_Scene);
                } else if (componentID == optionsButton.getComponentID()) {
                    App.SetCurrentScene(AppTest.Options_Scene);
                } else if (componentID == quitButton.getComponentID()) {
                    running = false;
                }
            }
        }
    }

}

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

public class TitleScene extends Scene implements UIListener {

    private final LogSource logSource = new LogSource("Title Scene");
    private final UIGroup UI = new UIGroup();
    private final Label label = new Label();
    private final Button startButton = new Button();
    private final Button optionsButton = new Button();
    private final Button quitButton = new Button();
    private final Camera sparkCamera = new Camera();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private float sparkTimer = 0.0f;
    private boolean running = false;

    @Override
    public boolean initialize() {
        final String[] buttonAnimations = { "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation"
        };
        UI.initialize(this, 0, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        UI.addComponent(label);
        UI.addComponent(startButton);
        UI.addComponent(optionsButton);
        UI.addComponent(quitButton);
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

    @Override
    public boolean enter(Scene lastScene) {
        sparks.clear();
        return true;
    }

    @Override
    public boolean processInput() {
        UI.processInput();
        return running;
    }

    @Override
    public void draw() {
        UI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    @Override
    public void update(float deltaFrames) {
        UI.update(deltaFrames);
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
    public boolean destroy() {
        UI.destroy();
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (groupID == UI.getGroupID()) {
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

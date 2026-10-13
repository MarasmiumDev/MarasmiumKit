/**
 * File:        TitleScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     The initial/opening scene of the MarasmiumKit application framework's AppTest program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Carousel;
import dev.marasmium.kit.uilib.component.Label;
import dev.marasmium.kit.uilib.component.Slider;
import dev.marasmium.kit.uilib.component.TextBox;

import java.util.ArrayList;

public class TitleScene extends Scene implements UIListener {

    private final LogSource logSource = new LogSource("Title Scene");
    private final UIGroup UI = new UIGroup();
    private final Label label = new Label();
    private final Button startButton = new Button();
    private final Button optionsButton = new Button();
    private final Button quitButton = new Button();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private float sparkTimer = 0.0f;

    @Override
    public boolean initialize() {
        final String[] buttonAnimations = { "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation"
        };
        UI.initialize(this, 0, 1.245f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
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
        return true;
    }

    @Override
    public void draw() {
        UI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(UI.getCamera(), spark);
        }
    }

    @Override
    public void update(float deltaFrames) {
        UI.update(deltaFrames);
        sparkTimer += deltaFrames;
        if (sparkTimer > (float)App.Graphics.getTargetFPS() / 10.0f) {
            sparkTimer = 0.0f;
            Spark spark = new Spark();
            spark.initialize();
            sparks.add(spark);
        }
        for (Spark spark : sparks) {
            spark.update(deltaFrames);
        }
        sparks.removeIf(spark -> !spark.isAlive());
    }

    @Override
    public boolean leave(Scene nextScene) {
        UI.destroy();
        return true;
    }

    @Override
    public boolean destroy() {
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        App.Log.write(logSource, LogLevel.Info, "Component ", componentID, " in group ", groupID, " event ", event);
    }

}

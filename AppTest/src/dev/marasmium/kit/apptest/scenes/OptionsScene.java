/**
 * File:        OptionsScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.13
 * Purpose:     Defines the options/setting scene for the MarasmiumKit application framework's test program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Carousel;
import dev.marasmium.kit.uilib.component.Slider;
import dev.marasmium.kit.uilib.component.Switch;

import java.util.ArrayList;

public class OptionsScene extends Scene implements UIListener {

    private final UIGroup optionsUI = new UIGroup();
    private final Carousel resolutionCarousel = new Carousel();
    private final Switch fullscreenSwitch = new Switch();
    private final Carousel monitorCarousel = new Carousel();
    private final Slider musicVolumeSlider = new Slider();
    private final Slider soundEffectsVolumeSlider = new Slider();
    private final Carousel speakerCarousel = new Carousel();
    private final UIGroup controlsUI = new UIGroup();
    private final Button backButton = new Button();
    private final Button defaultsButton = new Button();
    private final Button saveButton = new Button();
    private final Camera sparkCamera = new Camera();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private float sparkTimer = 0.0f;

    @Override
    protected boolean initialize() {
        // Initialize options UI components
        optionsUI.initialize(this, 0, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.20f);
        optionsUI.addComponent(resolutionCarousel);
        optionsUI.addComponent(fullscreenSwitch);
        optionsUI.addComponent(monitorCarousel);
        optionsUI.addComponent(musicVolumeSlider);
        optionsUI.addComponent(soundEffectsVolumeSlider);
        optionsUI.addComponent(speakerCarousel);
        // Initialize control UI components
        final String[] buttonAnimationFilePaths = {
                "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation"
        };
        controlsUI.initialize(this, 1, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        controlsUI.addComponent(backButton);
        controlsUI.addComponent(defaultsButton);
        controlsUI.addComponent(saveButton);
        backButton.initialize(Vector.Cartesian(0.05f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "BACK", Alignment.Center);
        defaultsButton.initialize(Vector.Cartesian(0.375f, 0.05f), Vector.Cartesian(0.25f, 0.10f),
                buttonAnimationFilePaths, "DEFAULTS", Alignment.Center);
        saveButton.initialize(Vector.Cartesian(0.70f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "SAVE", Alignment.Center);
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        sparks.clear();
        return true;
    }

    @Override
    public boolean processInput() {
        optionsUI.processInput();
        controlsUI.processInput();
        return true;
    }

    @Override
    public void draw() {
        optionsUI.draw();
        controlsUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    @Override
    public void update(float deltaFrames) {
        optionsUI.update(deltaFrames);
        controlsUI.update(deltaFrames);
        sparkTimer += deltaFrames;
        if (sparkTimer > App.Graphics.getTargetFPS() / 20.0f) {
            sparkTimer = 0.0f;
            Spark spark = new Spark();
            spark.initialize();
            sparks.add(spark);
        }
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
        optionsUI.destroy();
        controlsUI.destroy();
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (groupID == optionsUI.getGroupID()) {

        } else if (groupID == controlsUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                if (componentID == backButton.getComponentID()) {
                    App.SetCurrentScene(AppTest.Title_Scene);
                }
            }
        }
    }
}

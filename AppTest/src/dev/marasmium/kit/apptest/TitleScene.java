/**
 * File:        TitleScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     The initial/opening scene of the MarasmiumKit application framework's AppTest program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.MouseButton;

public class TitleScene extends Scene {

    private Camera camera;
    private Sprite sprite;

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        camera = new Camera();
        if (!camera.initialize(Vector.Zero(), 1.0f, Angle.Zero())) {
            return false;
        }
        sprite = new Sprite();
        if (!sprite.initialize(Vector.Zero(), 0.0f, Vector.Cartesian(50.0f, 50.0f), Angle.Zero(),
                "Animation/Animation_1.animation")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean processInput() {
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
            sprite.stopAnimation();
            sprite.playAnimation();
        }
        if (App.Input.mouse.isButtonPressed(MouseButton.Right)) {
            sprite.stopAnimation();
            sprite.playAnimation(3);
        }
        return true;
    }

    @Override
    public void draw() {
        App.Graphics.submit(camera, sprite);
    }

    @Override
    public void update(float deltaFrames) {
        camera.update(deltaFrames);
        sprite.update(deltaFrames);
    }

    @Override
    public boolean leave(Scene nextScene) {
        camera.destroy();
        sprite.destroy();
        return true;
    }

    @Override
    public boolean destroy() {
        return true;
    }

}

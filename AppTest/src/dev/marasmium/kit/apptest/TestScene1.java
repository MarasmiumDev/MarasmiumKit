/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.graphics.TextAlignment;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private final Camera camera = new Camera();
    private Sprite sprite;
    private String text;
    private String typefaceFileName;
    private TextAlignment horizontalAlignment;
    private TextAlignment verticalAlignment;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        camera.initialize(Vector.Zero(), 1.0f, Angle.Zero());
        sprite = new Sprite();
        sprite.initialize(Vector.Cartesian(0.0f, 0.0f), 0.0f, Vector.Cartesian(4500.0f, 100.0f), Angle.Zero(),
                "Animation/Animation_2.animation");
        sprite.setAnimationFrame(6);
        typefaceFileName = "Typeface/Fira_Sans.typeface";
        text = "";
        horizontalAlignment = TextAlignment.Left;
        verticalAlignment = TextAlignment.Bottom;
        return true;
    }

    @Override
    public boolean processInput() {
        // Camera controls
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Backspace)) {
            float cameraZoom = 0.01f * camera.getScale();
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Q)) {
                camera.setZoom(-cameraZoom);
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.E)) {
                camera.setZoom(cameraZoom);
            } else {
                camera.setZoom(0.0f);
            }
            float cameraSpeed = 3.5f / camera.getScale();
            if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
                camera.getVelocity().setX(-cameraSpeed);
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.D)) {
                camera.getVelocity().setX(cameraSpeed);
            } else {
                camera.getVelocity().setX(0.0f);
            }
            if (App.Input.keyboard.isKeyDown(KeyboardKey.S)) {
                camera.getVelocity().setY(-cameraSpeed);
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.W)) {
                camera.getVelocity().setY(cameraSpeed);
            } else {
                camera.getVelocity().setY(0.0f);
            }
            float cameraRotation = 0.01f;
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Z)) {
                camera.setRotation(Angle.Radians(-cameraRotation));
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.X)) {
                camera.setRotation(Angle.Radians(cameraRotation));
            } else {
                camera.setRotation(Angle.Zero());
            }
            // Alignment controls
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Left)) {
                horizontalAlignment = TextAlignment.Left;
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Right)) {
                horizontalAlignment = TextAlignment.Right;
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Down)) {
                verticalAlignment = TextAlignment.Bottom;
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Up)) {
                verticalAlignment = TextAlignment.Top;
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Comma)) {
                horizontalAlignment = TextAlignment.Center;
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Period)) {
                verticalAlignment = TextAlignment.Center;
            }
            // Change typeface
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.T)) {
                typefaceFileName = "Typeface/Fira_Sans.typeface";
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Y)) {
                typefaceFileName = "Typeface/Silkscreen.typeface";
            }
        } else {
            text += App.Input.keyboard.getTypedChars();
        }
        return true;
    }

    @Override
    public void draw() {
        App.Graphics.submit(camera, sprite);
        App.Graphics.submit(camera, text, typefaceFileName, sprite.getPosition(), sprite.getDimensions(), 1.0f, 0.5f,
                horizontalAlignment, verticalAlignment);
    }

    @Override
    public void update(float deltaFrames) {
        camera.update(deltaFrames);
        sprite.update(deltaFrames);
    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        camera.destroy();
        sprite.destroy();
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }
    
}

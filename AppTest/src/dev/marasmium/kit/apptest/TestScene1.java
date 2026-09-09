/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.assets.Animation;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.applib.windowing.Monitor;

import java.util.ArrayList;
import java.util.Random;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private final Camera camera = new Camera();
    private Sprite center = new Sprite();
    private final ArrayList<Sprite> sprites = new ArrayList<>();
    private int frames = 0;
    private double frameTimer = 0.0d;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        center.initialize(Vector.Zero(), 0.0f, Vector.Cartesian(10.0f, 10.0f), Angle.Zero(),
                "Animation/Animation_1.animation");
        camera.initialize(Vector.Zero(), 1.0f, Angle.Zero());
        return true;
    }

    @Override
    public boolean processInput() {
        // Control camera
        float translateSpeed = 1.0f;
        if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
            camera.getVelocity().setX(-translateSpeed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.D)) {
            camera.getVelocity().setX(translateSpeed);
        } else {
            camera.getVelocity().setX(0.0f);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.S)) {
            camera.getVelocity().setY(-translateSpeed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.W)) {
            camera.getVelocity().setY(translateSpeed);
        } else {
            camera.getVelocity().setY(0.0f);
        }
        float zoomSpeed = 0.01f;
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Q)) {
            camera.setZoom(-zoomSpeed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.E)) {
            camera.setZoom(zoomSpeed);
        } else {
            camera.setZoom(0.0f);
        }
        float rotateSpeed = 0.01f;
        if (App.Input.keyboard.isKeyDown(KeyboardKey.R)) {
            camera.setRotation(Angle.Radians(-rotateSpeed));
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.T)) {
            camera.setRotation(Angle.Radians(rotateSpeed));
        } else {
            camera.setRotation(Angle.Zero());
        }
        // Add sprites
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
            Sprite s = new Sprite();
            s.initialize(App.Input.mouse.getCursorPosition(camera), 0.0f, Vector.Cartesian(50.0f, 50.0f), Angle.Zero(),
                    "Animation/Animation_2.animation");
            s.playAnimation();
            sprites.add(s);
        }
        return true;
    }

    @Override
    public void draw() {
        App.Graphics.submit(camera, sprites);
        App.Graphics.submit(camera, center);
        frames++;
    }

    @Override
    public void update(float deltaFrames) {
        camera.update(deltaFrames);
        for (Sprite sprite : sprites) {
            sprite.update(deltaFrames);
        }
        if (frameTimer > App.Graphics.getTargetFPS()) {
            App.Log.write(logSource, LogLevel.Info, "Rendered ", frames, " frames");
            frameTimer = 0.0d;
            frames = 0;
        }
        frameTimer += deltaFrames;
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
        App.Log.write(logSource, LogLevel.Info, "Network connected");
        return true;
    }

    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        App.Log.write(logSource, LogLevel.Info, "Received message: ", message);
    }

    @Override
    public void netDisconnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Network disconnected");
    }
    
}

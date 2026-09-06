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
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
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
    private final ArrayList<Sprite> sprites = new ArrayList<>();
    private final Sprite player = new Sprite();
    private int frames = 0;
    private double frameTimer = 0.0d;
    private double spriteTimer = 0.0d;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        player.initialize(Vector.Cartesian(0.0d, 0.0d), 1.0d, Vector.Cartesian(0.25d, 0.25d), Angle.Radians(0.0d),
                Colour.Blue);
        return true;
    }

    @Override
    public boolean processInput() {
        // Control the player
        final double speed = 0.01d;
        if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
            player.getVelocity().setX(-speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.D)) {
            player.getVelocity().setX(speed);
        } else {
            player.getVelocity().setX(0.0d);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.S)) {
            player.getVelocity().setY(-speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.W)) {
            player.getVelocity().setY(speed);
        } else {
            player.getVelocity().setY(0.0d);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.N)) {
            player.setRotation(Angle.Radians(-speed));
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.M)) {
            player.setRotation(Angle.Radians(speed));
        } else {
            player.setRotation(Angle.Radians(0.0d));
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Left)) {
            player.getGrowth().setX(-speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.Right)) {
            player.getGrowth().setX(speed);
        } else {
            player.getGrowth().setX(0.0d);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Down)) {
            player.getGrowth().setY(-speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.Up)) {
            player.getGrowth().setY(speed);
        } else {
            player.getGrowth().setY(0.0d);
        }
        return true;
    }

    @Override
    public void draw() {
        App.Graphics.submit(sprites);
        App.Graphics.submit(player);
        frames++;
    }

    @Override
    public void update(double deltaFrames) {
        for (Sprite sprite : sprites) {
            sprite.update(deltaFrames);
        }
        player.update(deltaFrames);
        if (frameTimer > App.Graphics.getTargetFPS()) {
            App.Log.write(logSource, LogLevel.Info, "Rendered ", frames, " frames of ", sprites.size(), " sprites");
            frameTimer = 0.0d;
            frames = 0;
        }
        if (spriteTimer > App.Graphics.getTargetFPS()) {
            Sprite sprite = new Sprite();
            sprite.initialize(Vector.Cartesian(Math.random() * 2.0d - 1.0d, Math.random() * 2.0d - 1.0d), 0.0d,
                    Vector.Cartesian(Math.random() * 0.75d, Math.random() * 0.75d), Angle.Radians(0.0d),
                    Colour.Channels(Math.random(), Math.random(), Math.random(), Math.random()));
            sprite.setVelocity(Vector.Cartesian(Math.random() * 0.02d - 0.01d, Math.random() * 0.02d - 0.01d));
            sprite.setGrowth(Vector.Cartesian(Math.random() * 0.02d - 0.01d, Math.random() * 0.02d - 0.01d));
            sprite.setRotation(Angle.Radians(Math.random() * 0.01d - 0.005d));
            sprites.add(sprite);
            spriteTimer = 0.0d;
        }
        frameTimer += deltaFrames;
        spriteTimer += deltaFrames;
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

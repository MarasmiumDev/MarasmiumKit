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
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private final Camera camera = new Camera();
    private Sprite sprite1;
    private Sprite sprite2;
    private int spriteIndex = 0;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        camera.initialize(Vector.Zero(), 1.0f, Angle.Zero());
        sprite1 = new Sprite();
        sprite1.initialize(Vector.Cartesian(0.0f, 0.0f), 0.0f, Vector.Cartesian(100.0f, 100.0f), Angle.Zero(),
                "Animation/Animation_1.animation");
        sprite2 = new Sprite();
        sprite2.initialize(Vector.Cartesian(-100.0f, 0.0f), 1.0f, Vector.Cartesian(160.0f, 90.0f), Angle.Radians(1.2f),
                "Animation/Animation_2.animation");
        spriteIndex = 0;
        return true;
    }

    @Override
    public boolean processInput() {
        // Control sprite
        final float velocity = 2.5f;
        final float rotation = 0.01f;
        final float growth = 1.5f;
        if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
            sprite1.getVelocity().setX(-velocity);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.D)) {
            sprite1.getVelocity().setX(velocity);
        } else {
            sprite1.getVelocity().setX(0.0f);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.S)) {
            sprite1.getVelocity().setY(-velocity);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.W)) {
            sprite1.getVelocity().setY(velocity);
        } else {
            sprite1.getVelocity().setY(0.0f);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Q)) {
            sprite1.setRotation(Angle.Radians(-rotation));
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.E)) {
            sprite1.setRotation(Angle.Radians(rotation));
        } else {
            sprite1.setRotation(Angle.Zero());
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.J)) {
            sprite1.getGrowth().setX(-growth);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.L)) {
            sprite1.getGrowth().setX(growth);
        } else {
            sprite1.getGrowth().setX(0.0f);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.K)) {
            sprite1.getGrowth().setY(-growth);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.I)) {
            sprite1.getGrowth().setY(growth);
        } else {
            sprite1.getGrowth().setY(0.0f);
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.T)) {
            App.Log.write(logSource, LogLevel.Info, "Contains: ", sprite1.contains(sprite2), ", Disjoint: ",
                    sprite1.isDisjointFrom(sprite2), ", Intersects: ", sprite1.intersectsWith(sprite2));
        }
        return true;
    }

    @Override
    public void draw() {
        App.Graphics.submit(camera, sprite1);
        App.Graphics.submit(camera, sprite2);
    }

    @Override
    public void update(float deltaFrames) {
        camera.update(deltaFrames);
        sprite1.update(deltaFrames);
        sprite2.update(deltaFrames);
    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        camera.destroy();
        sprite1.destroy();
        sprite2.destroy();
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }
    
}

/**
 * File:        Spark.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.12
 * Purpose:     Defines a sprite representing a spark / small visual artifact
 */

package dev.marasmium.kit.apptest.sprites;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Sprite;

public class Spark extends Sprite {

    private boolean alive = false;
    private float timer = 0.0f;

    public void initialize() {
        Vector position = Vector.Cartesian((float)Math.random() * App.Window.getDimensions().getX(), -16.0f);
        Vector dimensions = Vector.Cartesian((float)Math.random() * 23.0f, (float)Math.random() * 24.0f);
        super.initialize(position, 0.0f, dimensions, Angle.Zero(),
                "Assets/Animations/Spark.animation");
        playAnimation(1);
        velocity = Vector.Cartesian(0.0f, (float)Math.random() * 10.0f);
        rotation = Angle.Radians(((2.0f * (float)Math.random()) - 1.0f) * 0.1f);
    }

    public void update(float deltaFrames) {
        super.update(deltaFrames);
        alive = isAnimationPlaying();
        timer += deltaFrames;
        if (timer > App.Graphics.getTargetFPS() / 2.0f) {
            velocity.setX(((float)Math.random() * 3.0f) - 1.5f);
            timer = 0.0f;
        }
    }

    public boolean isAlive() {
        return alive;
    }

}

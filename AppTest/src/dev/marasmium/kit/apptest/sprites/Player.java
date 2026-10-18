/**
 * File:        Player.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.16
 * Purpose:     Defines a controllable player class for the MarasmiumKit application framework's app test program
 */

package dev.marasmium.kit.apptest.sprites;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.KeyboardKey;

/**
 * A sprite representing the player's position
 */
public class Player extends Sprite {

    /**
     * The player's movement speed
     */
    private static final float Movement_Speed = 5.3f;

    /**
     * Initialize this player's position and animation
     * @return Whether this player was initialized successfully
     */
    public boolean initialize() {
        return super.initialize(Vector.Zero(), 0.0f, Vector.Cartesian(50.0f, 50.0f), Angle.Zero(),
                "Assets/Animations/Player.animation");
    }

    /**
     * Process user-input to this player and update its velocity
     */
    public void processInput() {
        if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
            velocity.setX(-Movement_Speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.D)) {
            velocity.setX(Movement_Speed);
        } else {
            velocity.setX(0.0f);
        }
        if (App.Input.keyboard.isKeyDown(KeyboardKey.S)) {
            velocity.setY(-Movement_Speed);
        } else if (App.Input.keyboard.isKeyDown(KeyboardKey.W)) {
            velocity.setY(Movement_Speed);
        } else {
            velocity.setY(0.0f);
        }
    }

}

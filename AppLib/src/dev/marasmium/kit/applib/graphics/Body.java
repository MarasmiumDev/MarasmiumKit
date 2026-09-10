/**
 * File:        Body.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.08
 * Purpose:     Defines an abstract 2D body with position, velocity, angle, and rotation
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;

/**
 * An abstract 2D body with a position, velocity, angle, and rotation
 */
public class Body {

    /**
     * The current position of this body in world coordinates
     */
    protected Vector position = null;
    /**
     * The current velocity of this body in world coordinates
     */
    protected Vector velocity = null;
    /**
     * The current angle of this body
     */
    protected Angle angle = null;
    /**
     * The current rate of change of the angle of this body
     */
    protected Angle rotation = null;

    /**
     * Initialize this body with a position and an angle
     * @param position The position for this body in world coordinates
     * @param angle The angle of this body
     * @return Whether the given position and angle were valid
     */
    public boolean initialize(Vector position, Angle angle) {
        if (!setPosition(position)) {
            return false;
        }
        if (!setVelocity(Vector.Zero())) {
            return false;
        }
        if (!setAngle(angle)) {
            return false;
        }
        if (!setRotation(Angle.Zero())) {
            return false;
        }
        return true;
    }

    /**
     * Update the position and angle of this body by their rates of change
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    public void update(float deltaFrames) {
        position = position.add(velocity.scalarMultiply(deltaFrames));
        angle = angle.add(rotation.scalarMultiply(deltaFrames));
    }

    /**
     * Free this body's memory
     */
    public void destroy() {
        position = null;
        velocity = null;
        angle = null;
        rotation = null;
    }

    /**
     * Get the current position of this body
     * @return The current position of this body in world coordinates
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Set the position of this body
     * @param position The new position for this body in world coordinates
     * @return Whether the given position is valid
     */
    public boolean setPosition(Vector position) {
        if (position == null) {
            return false;
        }
        this.position = position;
        return true;
    }

    /**
     * Get the current velocity of this body
     * @return The current velocity of this body in world coordinates
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Set the velocity of this body
     * @param velocity The new velocity for this body in world coordinates
     * @return Whether the given velocity is valid
     */
    public boolean setVelocity(Vector velocity) {
        if (velocity == null) {
            return false;
        }
        this.velocity = velocity;
        return true;
    }

    /**
     * Get the current angle of this body
     * @return The current angle of this body
     */
    public Angle getAngle() {
        return angle;
    }

    /**
     * Set the angle of this body
     * @param angle The new angle for this body
     * @return Whether the given angle is valid
     */
    public boolean setAngle(Angle angle) {
        if (angle == null) {
            return false;
        }
        this.angle = angle;
        return true;
    }

    /**
     * Get the current rate of change of the angle of this body
     * @return The current rate of change of the angle of this body
     */
    public Angle getRotation() {
        return rotation;
    }

    /**
     * Set the rate of change of the angle of this body
     * @param rotation The new rate of change of the angle of this body
     * @return Whether the given angle was valid
     */
    public boolean setRotation(Angle rotation) {
        if (rotation == null) {
            return false;
        }
        this.rotation = rotation;
        return true;
    }

}

/**
 * File:        Body.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.08
 * Purpose:     Defines an abstract 2D body with position, velocity, angle, and rotation
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;

public class Body {

    protected Vector position = null;
    protected Vector velocity = null;
    protected Angle angle = null;
    protected Angle rotation = null;

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

    public void update(float deltaFrames) {
        position = position.add(velocity.scalarMultiply(deltaFrames));
        angle = angle.add(rotation.scalarMultiply(deltaFrames));
    }

    public void destroy() {
        position = null;
        velocity = null;
        angle = null;
        rotation = null;
    }

    public Vector getPosition() {
        return position;
    }

    public boolean setPosition(Vector position) {
        if (position == null) {
            return false;
        }
        this.position = position;
        return true;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public boolean setVelocity(Vector velocity) {
        if (velocity == null) {
            return false;
        }
        this.velocity = velocity;
        return true;
    }

    public Angle getAngle() {
        return angle;
    }

    public boolean setAngle(Angle angle) {
        if (angle == null) {
            return false;
        }
        this.angle = angle;
        return true;
    }

    public Angle getRotation() {
        return rotation;
    }

    public boolean setRotation(Angle rotation) {
        if (rotation == null) {
            return false;
        }
        this.rotation = rotation;
        return true;
    }

}

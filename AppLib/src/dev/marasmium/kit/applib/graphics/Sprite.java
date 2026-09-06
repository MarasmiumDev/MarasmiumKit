/**
 * File:        Sprite.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.06
 * Purpose:     Defines a structure representing a drawable sprite as an animated quad
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

public class Sprite {

    private Vector position = null;
    private double depth = 0.0d;
    private Vector velocity = null;
    private Vector dimensions = null;
    private Vector growth = null;
    private Angle angle = null;
    private Angle rotation = null;
    private Colour colour = null;

    public boolean initialize(Vector position, double depth, Vector dimensions, Angle angle, Colour colour) {
        if (!setPosition(position)) {
            return false;
        }
        setDepth(depth);
        if (!setVelocity(Vector.Cartesian(0.0d, 0.0d))) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setGrowth(Vector.Cartesian(0.0d, 0.0d))) {
            return false;
        }
        if (!setAngle(angle)) {
            return false;
        }
        if (!setRotation(Angle.Radians(0.0d))) {
            return false;
        }
        if (!setColour(colour)) {
            return false;
        }
        return true;
    }

    public void update(double deltaFrames) {
        position = position.add(velocity.scalarMultiply(deltaFrames));
        dimensions = dimensions.add(growth.scalarMultiply(deltaFrames));
        angle = angle.add(rotation.scalarMultiply(deltaFrames));
    }

    public void destroy() {
        position = null;
        depth = 0.0d;
        velocity = null;
        dimensions = null;
        growth = null;
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

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
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

    public Vector getDimensions() {
        return dimensions;
    }

    public boolean setDimensions(Vector dimensions) {
        if (dimensions == null) {
            return false;
        }
        this.dimensions = dimensions;
        return true;
    }

    public Vector getGrowth() {
        return growth;
    }

    public boolean setGrowth(Vector growth) {
        if (growth == null) {
            return false;
        }
        this.growth = growth;
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

    public Colour getColour() {
        return colour;
    }

    public boolean setColour(Colour colour) {
        if (colour == null) {
            return false;
        }
        this.colour = colour;
        return true;
    }

}

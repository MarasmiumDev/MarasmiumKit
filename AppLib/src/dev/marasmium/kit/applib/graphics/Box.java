/**
 * File:        Box.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.20
 * Purpose:     Defines an abstract 2D body with depth and dimensions
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;

/**
 * An abstract 2D body with depth and dimensions
 */
public class Box extends Body {

    /**
     * The depth of this box
     */
    protected float depth = 0.0f;
    /**
     * The dimensions of this box in world coordinates
     */
    protected Vector dimensions = null;
    /**
     * The rate of change of the dimensions of this box
     */
    protected Vector growth = null;

    /**
     * Initialize this box with a position, angle, dimensions, and depth
     * @param position The initial position of this box in world coordinates
     * @param depth The initial depth of this box
     * @param dimensions The initial dimensions of this box in world coordinates
     * @param angle The initial angle of this box
     * @return Whether the given parameters were valid
     */
    public boolean initialize(Vector position, float depth, Vector dimensions, Angle angle) {
        if (!super.initialize(position, angle)) {
            return false;
        }
        setDepth(depth);
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setGrowth(Vector.Zero())) {
            return false;
        }
        return true;
    }

    /**
     * Update this box's dimensions by its rate of change
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        super.update(deltaFrames);
        if (dimensions != null) {
            dimensions = dimensions.add(growth.scalarMultiply(deltaFrames));
            if (dimensions.getX() < 0.0f) {
                dimensions.setX(0.0f);
            }
            if (dimensions.getY() < 0.0f) {
                dimensions.setY(0.0f);
            }
        }
    }

    /**
     * Free this box's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        depth = 0.0f;
        dimensions = null;
        growth = null;
    }

    /**
     * Get the depth of this box
     * @return The current depth of this box
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Set the depth of this box
     * @param depth The new depth for this box
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     * Get the dimensions of this box
     * @return The current dimensions of this box in world coordinates
     */
    public Vector getDimensions() {
        return dimensions;
    }

    /**
     * Set the dimensions of this box
     * @param dimensions The new dimensions for this box in world coordinates
     * @return Whether the given dimensions were valid
     */
    public boolean setDimensions(Vector dimensions) {
        if (dimensions == null) {
            return false;
        }
        if (dimensions.getX() < 0.0f || dimensions.getY() < 0.0f) {
            return false;
        }
        this.dimensions = dimensions;
        return true;
    }

    /**
     * Get the rate of change of this box's dimensions
     * @return The current rate of change of this box's dimensions
     */
    public Vector getGrowth() {
        return growth;
    }

    /**
     * Set the rate of change of this box's dimensions
     * @param growth The new rate of change of this box's dimensions
     * @return Whether the given rate of change was valid
     */
    public boolean setGrowth(Vector growth) {
        if (growth == null) {
            return false;
        }
        this.growth = growth;
        return true;
    }

    /**
     * Test whether this box is fully inside another box
     * @param box The box to test this one against
     * @return Whether this box is inside the given box
     */
    public boolean isInside(Box box) {
        if (box == null) {
            return false;
        }
        if (position.getX() < box.getPosition().getX()
                || position.getX() + dimensions.getX() > box.getPosition().getX() + box.getDimensions().getX()) {
            return false;
        }
        if (position.getY() < box.getPosition().getY()
                || position.getY() + dimensions.getY() > box.getPosition().getY() + box.getDimensions().getY()) {
            return false;
        }
        return true;
    }

    /**
     * Test whether this box is fully outside another box
     * @param box The box to test this one against
     * @return Whether this box is outside the given box
     */
    public boolean isOutside(Box box) {
        if (box == null) {
            return false;
        }
        if (position.getX() + dimensions.getX() < box.getPosition().getX()
                || position.getX() > box.getPosition().getX() + box.getDimensions().getX()) {
            return false;
        }
        if (position.getY() + dimensions.getY() < box.getPosition().getY()
                || position.getY() > box.getPosition().getY() + box.getDimensions().getY()) {
            return false;
        }
        return true;
    }

    /**
     * Test whether this box intersects with another box
     * @param box The box to test this one against
     * @return Whether this box intersects with the given box
     */
    public boolean intersects(Box box) {
        return !this.isOutside(box) && !box.isOutside(this);
    }

}

/**
 * File:        Angle.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Data structure representing a signed angle in radians, degrees, gradians, or revolutions with related
 *              mathematical operations
 */

package dev.marasmium.kit.applib.data;

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Angle data structure and related constants and mathematical operations
 */
public class Angle implements Serializable, Cloneable {

    /**
     * The value of this angle in radians
     */
    private float theta;

    /**
     * Create an angle with a measure in radians
     * @param theta The measure of the angle in radians
     * @return An angle with the given measure
     */
    public static Angle Radians(float theta) {
        Angle newAngle = new Angle();
        newAngle.setRadians(theta);
        return newAngle;
    }

    /**
     * Create an angle with a measure in degrees
     * @param theta The measure of the angle in degrees
     * @return An angle with the given measure
     */
    public static Angle Degrees(float theta) {
        Angle newAngle = new Angle();
        newAngle.setDegrees(theta);
        return newAngle;
    }

    /**
     * Create an angle with a measure in gradians
     * @param theta The measure of the angle in gradians
     * @return An angle with the given measure
     */
    public static Angle Gradians(float theta) {
        Angle newAngle = new Angle();
        newAngle.setGradians(theta);
        return newAngle;
    }

    /**
     * Create an angle with a measure in revolutions
     * @param theta The measure of the angle in revolutions
     * @return An angle with the given measure
     */
    public static Angle Revolutions(float theta) {
        Angle newAngle = new Angle();
        newAngle.setRevolutions(theta);
        return newAngle;
    }

    /**
     * Construct a zero angle
     * @return A zero angle
     */
    public static Angle Zero() {
        return new Angle();
    }

    /**
     * Construct a right angle
     * @return A right angle
     */
    public static Angle Right() {
        Angle newAngle = new Angle();
        newAngle.setRadians(Constants.Pi / 2.0f);
        return newAngle;
    }

    /**
     * Construct an angle given serialized data
     * @param data The data to deserialize
     * @return A deserialized angle
     */
    public static Angle Deserialize(byte[] data) {
        if (data == null) {
            return null;
        }
        Angle newAngle = new Angle();
        try {
            newAngle.setRadians(ByteBuffer.wrap(data, 0, Float.BYTES).getFloat());
        } catch (BufferUnderflowException _) {
            return null;
        }
        return newAngle;
    }

    /**
     * Construct a zero angle
     */
    private Angle() {
        this.theta = 0.0f;
    }

    /**
     * Compute the sum of this vector and another one
     * @param angle The angle to add to this one
     * @return The sum of this angle and theta
     */
    public Angle add(Angle angle) {
        if (angle == null) {
            return null;
        }
        return Angle.Radians(this.theta + angle.theta);
    }

    /**
     * Compute the difference of this vector and another one
     * @param angle The angle to subtract from this one
     * @return The difference of this angle and theta
     */
    public Angle subtract(Angle angle) {
        if (angle == null) {
            return null;
        }
        return Angle.Radians(this.theta - angle.theta);
    }

    /**
     * Compute the product of this vector and a scalar
     * @param scalar The scalar to multiply this angle by
     * @return The scaled angle
     */
    public Angle scalarMultiply(float scalar) {
        return Angle.Radians(this.theta * scalar);
    }

    /**
     * Compute the quotient of this vector and a scalar
     * @param scalar The scalar to divide this angle by
     * @return The scaled angle
     */
    public Angle scalarDivide(float scalar) {
        return Angle.Radians(this.theta / scalar);
    }

    /**
     * Compute the measure of this angle when represented in standard position
     * @return This angle in standard position
     */
    public Angle standardize() {
        Angle newAngle = new Angle();
        newAngle.theta = theta;
        if (newAngle.isZero()) {
            return newAngle;
        }
        if (newAngle.theta < 0.0f) {
            while (newAngle.theta < 0.0f) {
                newAngle.theta += 2.0f * Constants.Pi;
            }
        } else {
            while (newAngle.theta > 2.0f * Constants.Pi) {
                newAngle.theta -= 2.0f * Constants.Pi;
            }
        }
        return newAngle;
    }

    /**
     * Test whether this angle is coterminal to another one
     * @param angle The angle to test
     * @return Whether this angle is coterminal to a
     */
    public boolean coterminalTo(Angle angle) {
        if (angle == null) {
            return false;
        }
        return standardize().equals(angle.standardize());
    }

    /**
     * Convert this angle to serialized data
     * @return This angle as serialized data
     */
    public byte[] serialize() {
        byte[] data;
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate(Float.BYTES);
            buffer.putFloat(theta);
            data = buffer.array();
        } catch (IllegalArgumentException | BufferOverflowException | UnsupportedOperationException _) {
            return null;
        }
        return data;
    }

    /**
     * Test whether this angle has the same measure as another one
     * @param o The object to compare against (must be an Angle)
     * @return Whether this angle is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Angle angle)) {
            return false;
        }
        return Math.abs(theta - angle.theta) < Constants.Epsilon;
    }

    /**
     * Convert this angle to a string
     * @return The string representation of this angle
     */
    @Override
    public String toString() {
        return "angle(" + theta + "rad)";
    }

    /**
     * Make a copy of this angle
     * @return A copy of this angle
     */
    @Override
    public Angle clone() {
        Angle newAngle;
        try {
            newAngle = (Angle)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        newAngle.theta = theta;
        return newAngle;
    }

    /**
     * Get the measure of this angle in radians
     * @return The measure of this angle in radians
     */
    public float getRadians() {
        return theta;
    }

    /**
     * Set the measure of this angle in radians
     * @param theta The new measure for this angle in radians
     */
    public void setRadians(float theta) {
        this.theta = theta;
    }

    /**
     * Get the measure of this angle in degrees
     * @return The measure of this angle in degrees
     */
    public float getDegrees() {
        return (theta * 180.0f) / Constants.Pi;
    }

    /**
     * Set the measure of this angle in degrees
     * @param theta The new measure for this angle in degrees
     */
    public void setDegrees(float theta) {
        this.theta = theta * (Constants.Pi / 180.0f);
    }

    /**
     * Get the measure of this angle in gradians
     * @return The measure of this angle in gradians
     */
    public float getGradians() {
        return (theta * 200.0f) / Constants.Pi;
    }

    /**
     * Set the measure of this angle in gradians
     * @param theta The new measure for this angle in gradians
     */
    public void setGradians(float theta) {
        this.theta = theta * (Constants.Pi / 200.0f);
    }

    /**
     * Get the measure of this angle in revolutions
     * @return The measure of this angle in revolutions
     */
    public float getRevolutions() {
        return theta / (2.0f * Constants.Pi);
    }

    /**
     * Set the measure of this angle in revolutions
     * @param theta The new measure for this angle in revolutions
     */
    public void setRevolutions(float theta) {
        this.theta = theta * 2.0f * Constants.Pi;
    }

    /**
     * Test whether this angle is zero
     * @return Whether this angle is zero
     */
    public boolean isZero() {
        return Math.abs(theta) < Constants.Epsilon;
    }

    /**
     * Test whether this angle is acute (between 0 and 90 degrees)
     * @return Whether this angle is acute
     */
    public boolean isAcute() {
        return theta > 0.0f && theta < Constants.Pi / 2.0f;
    }

    /**
     * Test whether this angle is right (90 degrees)
     * @return Whether this angle is right
     */
    public boolean isRight() {
        return Math.abs(theta - (Constants.Pi / 2.0f)) < Constants.Epsilon;
    }

    /**
     * Test whether this angle is obtuse (between 90 and 180 degrees)
     * @return Whether this angle is obtuse
     */
    public boolean isObtuse() {
        return theta > Constants.Pi / 2.0f && theta < Constants.Pi;
    }

    /**
     * Test whether this angle is straight (180 degrees)
     * @return Whether this angle is straight
     */
    public boolean isStraight() {
        return Math.abs(theta - Constants.Pi) < Constants.Epsilon;
    }

    /**
     * Test whether this angle is reflex (between 180 and 360 degrees)
     * @return Whether this angle is reflex
     */
    public boolean isReflex() {
        return theta > Constants.Pi && theta < 2.0f * Constants.Pi;
    }

    /**
     * Get the quadrant of the plane this angle's endpoint is in
     * @return The quadrant of this angle
     */
    public int getQuadrant() {
        Angle angle = standardize();
        return ((int)(angle.getRevolutions() * 4.0f)) + 1;
    }

}

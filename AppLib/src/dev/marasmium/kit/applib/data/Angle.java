/**
 * File:        Angle.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Data structure representing a signed angle in radians, degrees, gradians, or revolutions with related
 *              mathematical operations
 */

package dev.marasmium.kit.applib.data;

import java.io.Serializable;

/**
 * Angle data structure and related constants and mathematical operations
 */
public class Angle implements Serializable {

    /**
     * Small value for comparing with floating-point rounding error
     */
    public static final double Epsilon = 1.0E-6d;
    /**
     * Circle constant
     */
    public static final double Pi = 3.141592653589793d;

    /**
     * The value of this angle in radians
     */
    private double theta;

    /**
     * Create an angle with a measure in radians
     * @param theta The measure of the angle in radians
     * @return An angle with the given measure
     */
    public static Angle Radians(double theta) {
        Angle a = new Angle();
        a.setRadians(theta);
        return a;
    }

    /**
     * Create an angle with a measure in degrees
     * @param theta The measure of the angle in degrees
     * @return An angle with the given measure
     */
    public static Angle Degrees(double theta) {
        Angle a = new Angle();
        a.setDegrees(theta);
        return a;
    }

    /**
     * Create an angle with a measure in gradians
     * @param theta The measure of the angle in gradians
     * @return An angle with the given measure
     */
    public static Angle Gradians(double theta) {
        Angle a = new Angle();
        a.setGradians(theta);
        return a;
    }

    /**
     * Create an angle with a measure in revolutions
     * @param theta The measure of the angle in revolutions
     * @return An angle with the given measure
     */
    public static Angle Revolutions(double theta) {
        Angle a = new Angle();
        a.setRevolutions(theta);
        return a;
    }

    /**
     * Construct a zero angle
     */
    private Angle() {
        this.theta = 0.0d;
    }

    /**
     * Compute the sum of this vector and another one
     * @param theta The angle to add to this one
     * @return The sum of this angle and theta
     */
    public Angle add(Angle theta) {
        if (theta == null) {
            return null;
        }
        return Angle.Radians(this.theta + theta.theta);
    }

    /**
     * Compute the difference of this vector and another one
     * @param theta The angle to subtract from this one
     * @return The difference of this angle and theta
     */
    public Angle subtract(Angle theta) {
        if (theta == null) {
            return null;
        }
        return Angle.Radians(this.theta - theta.theta);
    }

    /**
     * Compute the product of this vector and a scalar
     * @param a The scalar to multiply this angle by
     * @return The scaled angle
     */
    public Angle scalarMultiply(double a) {
        return Angle.Radians(this.theta * a);
    }

    /**
     * Compute the quotient of this vector and a scalar
     * @param a The scalar to divide this angle by
     * @return The scaled angle
     */
    public Angle scalarDivide(double a) {
        return Angle.Radians(this.theta / a);
    }

    /**
     * Compute the measure of this angle when represented in standard position
     * @return This angle in standard position
     */
    public Angle standardize() {
        Angle a = new Angle();
        a.theta = theta;
        if (a.isZero()) {
            return a;
        }
        if (a.theta < 0.0d) {
            while (a.theta < 0.0d) {
                a.theta += 2.0d * Pi;
            }
        } else {
            while (a.theta > 2.0d * Pi) {
                a.theta -= 2.0d * Pi;
            }
        }
        return a;
    }

    /**
     * Get the measure of this angle in radians
     * @return The measure of this angle in radians
     */
    public double getRadians() {
        return theta;
    }

    /**
     * Set the measure of this angle in radians
     * @param theta The new measure for this angle in radians
     */
    public void setRadians(double theta) {
        this.theta = theta;
    }

    /**
     * Get the measure of this angle in degrees
     * @return The measure of this angle in degrees
     */
    public double getDegrees() {
        return (theta * 180.0d) / Pi;
    }

    /**
     * Set the measure of this angle in degrees
     * @param theta The new measure for this angle in degrees
     */
    public void setDegrees(double theta) {
        this.theta = theta * (Pi / 180.0d);
    }

    /**
     * Get the measure of this angle in gradians
     * @return The measure of this angle in gradians
     */
    public double getGradians() {
        return (theta * 200.0d) / Pi;
    }

    /**
     * Set the measure of this angle in gradians
     * @param theta The new measure for this angle in gradians
     */
    public void setGradians(double theta) {
        this.theta = theta * (Pi / 200.0d);
    }

    /**
     * Get the measure of this angle in revolutions
     * @return The measure of this angle in revolutions
     */
    public double getRevolutions() {
        return theta / (2.0d * Pi);
    }

    /**
     * Set the measure of this angle in revolutions
     * @param theta The new measure for this angle in revolutions
     */
    public void setRevolutions(double theta) {
        this.theta = theta * 2.0d * Pi;
    }

    /**
     * Test whether this angle is zero
     * @return Whether this angle is zero
     */
    public boolean isZero() {
        return Math.abs(theta) < Epsilon;
    }

    /**
     * Test whether this angle is coterminal to another one
     * @param theta The angle to test
     * @return Whether this angle is coterminal to a
     */
    public boolean isCoterminalTo(Angle theta) {
        if (theta == null) {
            return false;
        }
        return standardize().equals(theta.standardize());
    }

    /**
     * Get the quadrant of the plane this angle's endpoint is in
     * @return The quadrant of this angle
     */
    public int getQuadrant() {
        Angle a = standardize();
        return ((int)(a.getRevolutions() * 4.0d)) + 1;
    }

    /**
     * Test whether this angle is acute (between 0 and 90 degrees)
     * @return Whether this angle is acute
     */
    public boolean isAcute() {
        return theta > 0.0d && theta < Pi / 2.0d;
    }
    /**
     * Test whether this angle is right (90 degrees)
     * @return Whether this angle is right
     */
    public boolean isRight() {
        return Math.abs(theta - (Pi / 2.0d)) < Epsilon;
    }

    /**
     * Test whether this angle is obtuse (between 90 and 180 degrees)
     * @return Whether this angle is obtuse
     */
    public boolean isObtuse() {
        return theta > Pi / 2.0d && theta < Pi;
    }

    /**
     * Test whether this angle is straight (180 degrees)
     * @return Whether this angle is straight
     */
    public boolean isStraight() {
        return Math.abs(theta - Pi) < Epsilon;
    }

    /**
     * Test whether this angle is reflex (between 180 and 360 degrees)
     * @return Whether this angle is reflex
     */
    public boolean isReflex() {
        return theta > Pi && theta < 2.0d * Pi;
    }

    /**
     * Test whether this angle has the same measure as another one
     * @param o The object to compare against (must be an Angle)
     * @return Whether this angle is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Angle a)) {
            return false;
        }
        return Math.abs(theta - a.theta) < Epsilon;
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
        Angle a = new Angle();
        a.theta = theta;
        return a;
    }

}

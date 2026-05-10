/**
 * File:        Vec2D.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Defines a 2D vector data structure and related mathematical operations
 */

package dev.marasmium.kit.applib.data;

/**
 * 2D vector data structure with related constants and mathematical operations
 */
public class Vec2D {

    /**
     * Small value for comparing with floating-point rounding error
     */
    public static final double Epsilon = 1.0E-6d;

    /**
     * The horizontal component of this vector on the Cartesian plane
     */
    private double x;
    /**
     * The vertical component of this vector on the Cartesian plane
     */
    private double y;

    /**
     * Create a vector given coordinates on the Cartesian plane
     * @param x The horizontal coordinate for the vector
     * @param y The vertical coordinate for this vector
     * @return A vector to the Cartesian point (x, y)
     */
    public static Vec2D Cartesian(double x, double y) {
        Vec2D v = new Vec2D();
        v.setX(x);
        v.setY(y);
        return v;
    }

    /**
     * Create a vector given polar coordinates
     * @param length The length/magnitude for the vector
     * @param angle The angle/direction for the vector
     * @return A vector to the polar coordinates (length, angle)
     */
    public static Vec2D Polar(double length, Angle angle) {
        Vec2D v = new Vec2D();
        v.setX(1.0d);
        v.setLength(length);
        v.setAngle(angle);
        return v;
    }

    /**
     * Construct a zero vector
     */
    private Vec2D() {
        this.x = 0.0d;
        this.y = 0.0d;
    }

    /**
     * Compute the sum of this vector and another one
     * @param v The vector to add to this one
     * @return The sum of this vector and v
     */
    public Vec2D add(Vec2D v) {
        return Vec2D.Cartesian(x + v.x, y + v.y);
    }

    /**
     * Compute the difference of this vector and another one
     * @param v The vector to subtract from this one
     * @return The difference of this vector and v
     */
    public Vec2D subtract(Vec2D v) {
        return Vec2D.Cartesian(x - v.x, y - v.y);
    }

    /**
     * Compute the negative of this vector
     * @return The negative of this vector
     */
    public Vec2D negate() {
        return Vec2D.Cartesian(-x, -y);
    }

    /**
     * Compute the product of this vector and a scalar
     * @param a The scalar to multiply this vector by
     * @return The product of this vector and a
     */
    public Vec2D scalarMultiply(double a) {
        return Vec2D.Cartesian(x * a, y * a);
    }

    /**
     * Compute the quotient of this vector and a scalar
     * @param a The scalar to divide this vector by
     * @return The quotient of this vector and a
     */
    public Vec2D scalarDivide(double a) {
        if (a == 0.0d) {
            return null;
        }
        return scalarMultiply(1.0d / a);
    }

    /**
     * Compute the element-wise product of this vector and another one
     * @param v The vector to multiply this vector by
     * @return The element-wise product of this vector and v
     */
    public Vec2D elementMultiply(Vec2D v) {
        return Vec2D.Cartesian(x * v.x, y * v.y);
    }

    /**
     * Compute the element-wise quotient of this vector and another one
     * @param v The vector to divide this vector by
     * @return The element-wise quotient of this vector and v
     */
    public Vec2D elementDivide(Vec2D v) {
        return Vec2D.Cartesian(x / v.x, y / v.y);
    }

    /**
     * Compute the dot product of this vector and another one
     * @param v The vector to multiply this vector by
     * @return The dot product of this vector and v
     */
    public double dotMultiply(Vec2D v) {
        return Vec2D.Cartesian(x * v.x, y * v.y).getElementSum();
    }

    /**
     * Compute the squared distance between this vector and another one
     * @param v The vector to compare to
     * @return The squared distance between this vector and v
     */
    public double getDistanceToSquared(Vec2D v) {
        return subtract(v).getLengthSquared();
    }

    /**
     * Compute the distance between this vector and another one
     * @param v The vector to compare to
     * @return The distance between this vector and v
     */
    public double getDistanceTo(Vec2D v) {
        return Math.sqrt(getDistanceToSquared(v));
    }

    /**
     * Compute the normalized (unit length) vector with the same direction as this one
     * @return The normalized version of this vector
     */
    public Vec2D normalize() {
        return scalarDivide(getLength());
    }

    /**
     * Compute the 2D cross product of this vector and another one
     * @param v The vector to multiply this vector by
     * @return The 2D cross product of this vector and v (this x v)
     */
    public double crossMultiply(Vec2D v) {
        return (x * v.y) - (y * v.x);
    }

    /**
     * Compute the result of this vector rotated by an angle
     * @param theta The angle to rotate this vector by
     * @return The rotated version of this vector
     */
    public Vec2D rotate(Angle theta) {
        return Vec2D.Cartesian((x * Math.cos(theta.getRadians())) - (y * Math.sin(theta.getRadians())),
                (x * Math.sin(theta.getRadians())) + (y * Math.cos(theta.getRadians())));
    }

    /**
     * Compute the resulting of this vector rotated by an angle about the endpoint of another vector
     * @param theta The angle to rotate this vector by
     * @param o The vector whose endpoint to rotate this vector about
     * @return The rotated version of this vector
     */
    public Vec2D rotateAbout(Angle theta, Vec2D o) {
        return subtract(o).rotate(theta).add(o);
    }

    /**
     * Get the angle between this vector and another one
     * @param v The vector to compare to
     * @return The angle between this vector and v
     */
    public Angle getAngleTo(Vec2D v) {
        double numerator = getLengthSquared() + v.getLengthSquared() - getDistanceToSquared(v);
        double denominator = 2.0d * getLength() * v.getLength();
        if (denominator == 0.0d) {
            return Angle.Radians(0.0d);
        }
        return Angle.Radians(Math.acos(numerator / denominator));
    }

    /**
     * Compute the horizontal reflection of this vector
     * @return The horizontal reflection of this vector
     */
    public Vec2D reflectHorizontally() {
        return Vec2D.Cartesian(-x, y);
    }

    /**
     * Compute the vertical reflection of this vector
     * @return The vertical reflection of this vector
     */
    public Vec2D reflectVertically() {
        return Vec2D.Cartesian(x, -y);
    }

    /**
     * Compute the vector to the point a given percentage along the distance between this vector and another one
     * @param v The vector to compare this to (100% along the distance)
     * @param t The percentage of the distance to move between this vector and v
     * @return The vector t% of the way from this vector to v
     */
    public Vec2D interpolate(Vec2D v, double t) {
        return add(subtract(v).scalarMultiply(t));
    }

    /**
     * Compute the vector to the midpoint between this vector and another one
     * @param v The vector to compare to this
     * @return The vector at the midpoint between this vector and v
     */
    public Vec2D midpoint(Vec2D v) {
        return interpolate(v, 0.5d);
    }

    /**
     * Compute the floating-point floor of this vector
     * @return The floor of this vector
     */
    public Vec2D floor() {
        return Vec2D.Cartesian(Math.floor(x), Math.floor(y));
    }

    /**
     * Compute the floating-point ceiling of this vector
     * @return The ceiling of this vector
     */
    public Vec2D ceiling() {
        return Vec2D.Cartesian(Math.ceil(x), Math.ceil(y));
    }

    /**
     * Get the horizontal coordinate of this vector on the Cartesian plane
     * @return The horizontal coordinate of this vector
     */
    public double getX() {
        return x;
    }

    /**
     * Set the horizontal coordinate of this vector on the Cartesian plane
     * @param x The new horizontal coordinate for this vector
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get the vertical coordinate of this vector on the Cartesian plane
     * @return The vertical coordinate of this vector
     */
    public double getY() {
        return y;
    }

    /**
     * Set the vertical coordinate of this vector on the Cartesian plane
     * @param y The new vertical coordinate for this vector
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the sum of the Cartesian coordinates of this vector
     * @return The sum of this vector coordinates
     */
    public double getElementSum() {
        return x + y;
    }

    /**
     * Get the squared length/magnitude of this vector
     * @return The squared length of this vector
     */
    public double getLengthSquared() {
        return (x * x) + (y * y);
    }

    /**
     * Get the length/magnitude of this vector
     * @return The length of this vector
     */
    public double getLength() {
        return Math.sqrt(getLengthSquared());
    }

    /**
     * Test whether this vector is the zero vector
     * @return Whether this vector is the zero vector
     */
    public boolean isZero() {
        return getLength() < Epsilon;
    }

    /**
     * Test whether this vector is normalized (has unit length)
     * @return Whether this vector is normalized
     */
    public boolean isNormalized() {
        return Math.abs(getLength() - 1.0d) < Epsilon;
    }

    /**
     * Set the length/magnitude of this vector
     * @param length The new length for this vector
     */
    public void setLength(double length) {
        if (isZero()) {
            return;
        }
        Vec2D tmp = this.scalarMultiply(length / getLength());
        this.x = tmp.x;
        this.y = tmp.y;
    }

    /**
     * Get the polar angle of this vector
     * @return The polar angle of this vector
     */
    public Angle getAngle() {
        return Angle.Radians(Math.atan2(y, x));
    }

    /**
     * Set the polar angle of this vector
     * @param theta The polar angle for this vector
     */
    public void setAngle(Angle theta) {
        Vec2D tmp = this.rotate(Angle.Radians(theta.getRadians() - getAngle().getRadians()));
        this.x = tmp.x;
        this.y = tmp.y;
    }

    /**
     * Test whether this vector is parallel to another one
     * @param v The vector to compare this one to
     * @return Whether this vector is parallel to v
     */
    public boolean isParallelTo(Vec2D v) {
        if (isZero() || v.isZero()) {
            return false;
        }
        return Math.abs(crossMultiply(v)) < Epsilon;
    }

    /**
     * Test whether this vector is perpendicular to another one
     * @param v The vector to compare this one to
     * @return Whether this vector is perpendicular to v
     */
    public boolean isPerpendicularTo(Vec2D v) {
        if (isZero() || v.isZero()) {
            return false;
        }
        return Math.abs(dotMultiply(v)) < Epsilon;
    }

    /**
     * Test whether this vector is equal to another one
     * @param o The object to compare this vector to (must be a Vector)
     * @return Whether this vector is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec2D v)) {
            return false;
        }
        return Math.abs(x - v.x) < Epsilon && Math.abs(y - v.y) < Epsilon;
    }

    /**
     * Convert this vector to a string
     * @return The string representation of this vector
     */
    @Override
    public String toString() {
        return "vector(" + x + ", " + y + ")";
    }

}

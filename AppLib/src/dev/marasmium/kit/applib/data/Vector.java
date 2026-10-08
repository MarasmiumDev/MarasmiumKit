/**
 * File:        Vector.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Defines a 2D vector data structure and related mathematical operations
 */

package dev.marasmium.kit.applib.data;

import dev.marasmium.kit.applib.graphics.Box;

import java.io.Serializable;

/**
 * 2D vector data structure with related constants and mathematical operations
 */
public class Vector implements Serializable, Cloneable {

    /**
     * The horizontal component of this vector on the Cartesian plane
     */
    private float x;
    /**
     * The vertical component of this vector on the Cartesian plane
     */
    private float y;

    /**
     * Create a vector given coordinates on the Cartesian plane
     * @param x The horizontal coordinate for the vector
     * @param y The vertical coordinate for this vector
     * @return A vector to the Cartesian point (x, y)
     */
    public static Vector Cartesian(float x, float y) {
        Vector v = new Vector();
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
    public static Vector Polar(float length, Angle angle) {
        if (angle == null) {
            return null;
        }
        Vector v = new Vector();
        v.setX(1.0f);
        v.setLength(length);
        v.setAngle(angle);
        return v;
    }

    /**
     * Create a zero vector
     * @return The zero vector
     */
    public static Vector Zero() {
        return new Vector();
    }

    /**
     * Construct a zero vector
     */
    private Vector() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    /**
     * Compute the sum of this vector and another one
     * @param vector The vector to add to this one
     * @return The sum of this vector and v
     */
    public Vector add(Vector vector) {
        if (vector == null) {
            return null;
        }
        return Vector.Cartesian(x + vector.x, y + vector.y);
    }

    /**
     * Compute the difference of this vector and another one
     * @param vector The vector to subtract from this one
     * @return The difference of this vector and v
     */
    public Vector subtract(Vector vector) {
        if (vector == null) {
            return null;
        }
        return Vector.Cartesian(x - vector.x, y - vector.y);
    }

    /**
     * Compute the negative of this vector
     * @return The negative of this vector
     */
    public Vector negate() {
        return Vector.Cartesian(-x, -y);
    }

    /**
     * Compute the product of this vector and a scalar
     * @param angle The scalar to multiply this vector by
     * @return The product of this vector and a
     */
    public Vector scalarMultiply(float angle) {
        return Vector.Cartesian(x * angle, y * angle);
    }

    /**
     * Compute the quotient of this vector and a scalar
     * @param angle The scalar to divide this vector by
     * @return The quotient of this vector and a
     */
    public Vector scalarDivide(float angle) {
        if (angle == 0.0f) {
            return null;
        }
        return scalarMultiply(1.0f / angle);
    }

    /**
     * Compute the element-wise product of this vector and another one
     * @param vector The vector to multiply this vector by
     * @return The element-wise product of this vector and v
     */
    public Vector elementMultiply(Vector vector) {
        if (vector == null) {
            return null;
        }
        return Vector.Cartesian(x * vector.x, y * vector.y);
    }

    /**
     * Compute the element-wise quotient of this vector and another one
     * @param vector The vector to divide this vector by
     * @return The element-wise quotient of this vector and v
     */
    public Vector elementDivide(Vector vector) {
        if (vector == null) {
            return null;
        }
        return Vector.Cartesian(x / vector.x, y / vector.y);
    }

    /**
     * Compute the dot product of this vector and another one
     * @param vector The vector to multiply this vector by
     * @return The dot product of this vector and v or 0 if v is null
     */
    public float dotMultiply(Vector vector) {
        if (vector == null) {
            return Float.NaN;
        }
        return Vector.Cartesian(x * vector.x, y * vector.y).getElementSum();
    }

    /**
     * Compute the squared distance between this vector and another one
     * @param vector The vector to compare to
     * @return The squared distance between this vector and v or 0 if v is null
     */
    public float getDistanceToSquared(Vector vector) {
        if (vector == null) {
            return Float.NaN;
        }
        return subtract(vector).getLengthSquared();
    }

    /**
     * Compute the distance between this vector and another one
     * @param vector The vector to compare to
     * @return The distance between this vector and v or 0 if v is null
     */
    public float getDistanceTo(Vector vector) {
        if (vector == null) {
            return Float.NaN;
        }
        return (float)Math.sqrt(getDistanceToSquared(vector));
    }

    /**
     * Compute the normalized (unit length) vector with the same direction as this one
     * @return The normalized version of this vector
     */
    public Vector normalize() {
        return scalarDivide(getLength());
    }

    /**
     * Compute the 2D cross product of this vector and another one
     * @param vector The vector to multiply this vector by
     * @return The 2D cross product of this vector and v (this x v) or 0 if v is null
     */
    public float crossMultiply(Vector vector) {
        if (vector == null) {
            return Float.NaN;
        }
        return (x * vector.y) - (y * vector.x);
    }

    /**
     * Compute the result of this vector rotated by an angle
     * @param theta The angle to rotate this vector by
     * @return The rotated version of this vector
     */
    public Vector rotate(Angle theta) {
        if (theta == null) {
            return null;
        }
        float cosine = (float)Math.cos(theta.getRadians());
        float sine = (float)Math.sin(theta.getRadians());
        return Vector.Cartesian((x * cosine) - (y * sine), (x * sine) + (y * cosine));
    }

    /**
     * Compute the resulting of this vector rotated by an angle about the endpoint of another vector
     * @param theta The angle to rotate this vector by
     * @param origin The vector whose endpoint to rotate this vector about
     * @return The rotated version of this vector
     */
    public Vector rotateAbout(Angle theta, Vector origin) {
        if (theta == null || origin == null) {
            return null;
        }
        return subtract(origin).rotate(theta).add(origin);
    }

    /**
     * Get the angle between this vector and another one
     * @param vector The vector to compare to
     * @return The angle between this vector and v
     */
    public Angle getAngleTo(Vector vector) {
        if (vector == null) {
            return null;
        }
        float numerator = getLengthSquared() + vector.getLengthSquared() - getDistanceToSquared(vector);
        float denominator = 2.0f * getLength() * vector.getLength();
        if (denominator == 0.0f) {
            return Angle.Radians(0.0f);
        }
        return Angle.Radians((float)Math.acos(numerator / denominator));
    }

    /**
     * Compute the horizontal reflection of this vector
     * @return The horizontal reflection of this vector
     */
    public Vector reflectHorizontally() {
        return Vector.Cartesian(-x, y);
    }

    /**
     * Compute the vertical reflection of this vector
     * @return The vertical reflection of this vector
     */
    public Vector reflectVertically() {
        return Vector.Cartesian(x, -y);
    }

    /**
     * Compute the vector to the point a given percentage along the distance between this vector and another one
     * @param vector The vector to compare this to (100% along the distance)
     * @param t The percentage of the distance to move between this vector and v
     * @return The vector t% of the way from this vector to v
     */
    public Vector interpolate(Vector vector, float t) {
        if (vector == null) {
            return null;
        }
        return add(subtract(vector).scalarMultiply(t));
    }

    /**
     * Compute the vector to the midpoint between this vector and another one
     * @param vector The vector to compare to this
     * @return The vector at the midpoint between this vector and v
     */
    public Vector midpoint(Vector vector) {
        if (vector == null) {
            return null;
        }
        return interpolate(vector, 0.5f);
    }

    /**
     * Compute the floating-point floor of this vector
     * @return The floor of this vector
     */
    public Vector floor() {
        return Vector.Cartesian((float)Math.floor(x), (float)Math.floor(y));
    }

    /**
     * Compute the floating-point ceiling of this vector
     * @return The ceiling of this vector
     */
    public Vector ceiling() {
        return Vector.Cartesian((float)Math.ceil(x), (float)Math.ceil(y));
    }

    /**
     * Test whether this vector is parallel to another one
     * @param vector The vector to compare this one to
     * @return Whether this vector is parallel to v
     */
    public boolean parallelTo(Vector vector) {
        if (vector == null) {
            return false;
        }
        if (isZero() || vector.isZero()) {
            return false;
        }
        return Math.abs(crossMultiply(vector)) < Constants.Epsilon;
    }

    /**
     * Test whether this vector is perpendicular to another one
     * @param vector The vector to compare this one to
     * @return Whether this vector is perpendicular to v
     */
    public boolean perpendicularTo(Vector vector) {
        if (vector == null) {
            return false;
        }
        if (isZero() || vector.isZero()) {
            return false;
        }
        return Math.abs(dotMultiply(vector)) < Constants.Epsilon;
    }

    /**
     * Test whether this vector is positioned to the left of a line
     * @param line The line to test this vector against
     * @return Whether this vector is to the left of the given line
     */
    public boolean leftOf(Line line) {
        if (line == null) {
            return false;
        }
        if (line.isVertical()) {
            return x <= line.getXIntercept();
        }
        return y >= line.sampleY(x);
    }

    /**
     * Test whether this vector is positioned to the right of a line
     * @param line The line to test this vector against
     * @return Whether this vector is to the right of the given line
     */
    public boolean rightOf(Line line) {
        if (line == null) {
            return false;
        }
        if (line.isVertical()) {
            return x >= line.getXIntercept();
        }
        return y <= line.sampleY(x);
    }

    /**
     * Test whether this vector is positioned on a line
     * @param line The line to test this vector against
     * @return Whether this vector is on the given line
     */
    public boolean on(Line line) {
        if (line == null) {
            return false;
        }
        return line.contains(this);
    }

    /**
     * Test whether this vector is positioned between two lines
     * @param line1 The first line to test this vector against
     * @param line2 The second line to test this vector against
     * @return Whether this vector is between the given lines
     */
    public boolean between(Line line1, Line line2) {
        if (line1 == null || line2 == null) {
            return false;
        }
        return (rightOf(line1) && leftOf(line2)) || (leftOf(line1) && rightOf(line2));
    }

    /**
     * Test whether this vector is positioned inside a box
     * @param box The box to test this vector against
     * @return Whether this vector is inside the given box
     */
    public boolean inside(Box box) {
        if (box == null) {
            return false;
        }
        return box.contains(this);
    }

    /**
     * Test whether this vector is positioned outside a box
     * @param box The box to test this vector against
     * @return Whether this vector is outside the given box
     */
    public boolean outside(Box box) {
        if (box == null) {
            return false;
        }
        return !inside(box);
    }

    /**
     * Test whether this vector is equal to another one
     * @param o The object to compare this vector to (must be a Vector)
     * @return Whether this vector is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector vector)) {
            return false;
        }
        return Math.abs(x - vector.x) < Constants.Epsilon && Math.abs(y - vector.y) < Constants.Epsilon;
    }

    /**
     * Convert this vector to a string
     * @return The string representation of this vector
     */
    @Override
    public String toString() {
        return "vector(" + x + ", " + y + ")";
    }

    /**
     * Make a copy of this vector
     * @return A copy of this vector
     */
    @Override
    public Vector clone() {
        Vector v;
        try {
            v = (Vector)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        v.x = x;
        v.y = y;
        return v;
    }

    /**
     * Get the horizontal coordinate of this vector on the Cartesian plane
     * @return The horizontal coordinate of this vector
     */
    public float getX() {
        return x;
    }

    /**
     * Set the horizontal coordinate of this vector on the Cartesian plane
     * @param x The new horizontal coordinate for this vector
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get the vertical coordinate of this vector on the Cartesian plane
     * @return The vertical coordinate of this vector
     */
    public float getY() {
        return y;
    }

    /**
     * Set the vertical coordinate of this vector on the Cartesian plane
     * @param y The new vertical coordinate for this vector
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get the sum of the Cartesian coordinates of this vector
     * @return The sum of this vector's coordinates
     */
    public float getElementSum() {
        return x + y;
    }

    /**
     * Get the product of the Cartesian coordinates of this vector
     * @return The product of this vector's coordinates
     */
    public float getElementProduct() {
        return x * y;
    }

    /**
     * Get the squared length/magnitude of this vector
     * @return The squared length of this vector
     */
    public float getLengthSquared() {
        return (x * x) + (y * y);
    }

    /**
     * Get the length/magnitude of this vector
     * @return The length of this vector
     */
    public float getLength() {
        return (float)Math.sqrt(getLengthSquared());
    }

    /**
     * Test whether this vector is the zero vector
     * @return Whether this vector is the zero vector
     */
    public boolean isZero() {
        return getLength() < Constants.Epsilon;
    }

    /**
     * Test whether this vector is normalized (has unit length)
     * @return Whether this vector is normalized
     */
    public boolean isNormalized() {
        return Math.abs(getLength() - 1.0f) < Constants.Epsilon;
    }

    /**
     * Set the length/magnitude of this vector
     * @param length The new length for this vector
     */
    public void setLength(float length) {
        if (isZero()) {
            return;
        }
        Vector tmp = this.scalarMultiply(length / getLength());
        this.x = tmp.x;
        this.y = tmp.y;
    }

    /**
     * Get the polar angle of this vector
     * @return The polar angle of this vector
     */
    public Angle getAngle() {
        return Angle.Radians((float)Math.atan2(y, x));
    }

    /**
     * Set the polar angle of this vector
     * @param theta The polar angle for this vector
     */
    public void setAngle(Angle theta) {
        if (theta == null) {
            return;
        }
        Vector tmp = this.rotate(Angle.Radians(theta.getRadians() - getAngle().getRadians()));
        this.x = tmp.x;
        this.y = tmp.y;
    }

}

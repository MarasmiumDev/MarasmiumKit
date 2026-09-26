/**
 * File:        Line.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.22
 * Purpose:     Defines a data structure representing a line as a linear function
 */

package dev.marasmium.kit.applib.data;

import java.io.Serializable;

/**
 * Linear function data structure representing a 2D line
 */
public class Line implements Serializable, Cloneable {

    /**
     * Small value for comparing with floating-point rounding error
     */
    public static final float Epsilon = 0.0001f;

    /**
     * The rise/run slope of this line
     */
    private float slope;
    /**
     * The x-coordinate of the x-intercept of this line
     */
    private float xIntercept;
    /**
     * The y-coordinate of the y-intercept of this line
     */
    private float yIntercept;

    /**
     * Create a horizontal line with a given y-intercept
     * @param yIntercept The y-coordinate of the y-intercept for the line
     * @return The horizontal line with the given y-intercept or null if the y-intercept was invalid
     */
    public static Line Horizontal(float yIntercept) {
        if (!Float.isFinite(yIntercept)) {
            return null;
        }
        Line l = new Line();
        l.slope = 0.0f;
        l.xIntercept = Float.NaN;
        l.yIntercept = yIntercept;
        return l;
    }

    /**
     * Create a horizontal line passing through a given point
     * @param point The point for the line to pass through
     * @return The horizontal line passing through the given point or null if the point was invalid
     */
    public static Line Horizontal(Vector point) {
        if (point == null) {
            return null;
        }
        return Horizontal(point.getY());
    }

    /**
     * Create a vertical line with a given x-intercept
     * @param xIntercept The x-coordinate of the x-intercept for the line
     * @return The vertical line with the given y-intercept or null if the y-intercept was invalid
     */
    public static Line Vertical(float xIntercept) {
        if (!Float.isFinite(xIntercept)) {
            return null;
        }
        Line l = new Line();
        l.slope = Float.NaN;
        l.xIntercept = xIntercept;
        l.yIntercept = Float.NaN;
        return l;
    }

    /**
     * Create a vertical line passing through a given point
     * @param point The point for the line to pass through
     * @return The vertical line passing through the given point or null if the point was invalid
     */
    public static Line Vertical(Vector point) {
        if (point == null) {
            return null;
        }
        return Vertical(point.getX());
    }

    /**
     * Create a line with a given slope and x-intercept
     * @param slope The rise/run slope for the line
     * @param xIntercept The x-coordinate of the x-intercept for the line
     * @return The line with the given slope and x-intercept or null if the parameters were invalid
     */
    public static Line Slope_X_Intercept(float slope, float xIntercept) {
        if (Math.abs(slope) < Epsilon) {
            return null;
        }
        if (!Float.isFinite(xIntercept)) {
            return null;
        }
        if (!Float.isFinite(slope)) {
            return Vertical(xIntercept);
        } else {
            Line l = new Line();
            l.slope = slope;
            l.xIntercept = xIntercept;
            l.yIntercept = -slope * xIntercept;
            return l;
        }
    }

    /**
     * Create a line with a given slope and y-intercept
     * @param slope The rise/run slope for the line
     * @param yIntercept The y-coordinate of the y-intercept for the line
     * @return The line with the given slope and y-intercept or null if the parameters were invalid
     */
    public static Line Slope_Y_Intercept(float slope, float yIntercept) {
        if (!Float.isFinite(slope)) {
            return null;
        }
        if (!Float.isFinite(yIntercept)) {
            return null;
        }
        if (Math.abs(slope) < Epsilon) {
            return Horizontal(yIntercept);
        } else {
            Line l = new Line();
            l.slope = slope;
            l.xIntercept = -yIntercept / slope;
            l.yIntercept = yIntercept;
            return l;
        }
    }

    /**
     * Create a line with a given slope passing through a given point
     * @param point The point for the line to pass through
     * @param slope The rise/run slope for the line
     * @return The line with the given slope passing through the given point or null if the parameters were invalid
     */
    public static Line Point_Slope(Vector point, float slope) {
        if (point == null) {
            return null;
        }
        if (Math.abs(slope) < Epsilon) {
            return Horizontal(point);
        }
        if (!Float.isFinite(slope)) {
            return Vertical(point);
        }
        Line l = new Line();
        l.slope = slope;
        l.yIntercept = point.getY() - (slope * point.getX());
        l.xIntercept = -l.yIntercept / slope;
        return l;
    }

    /**
     * Create a line passing through two given points
     * @param point1 The first point for the line to pass through
     * @param point2 The second point for the line to pass through
     * @return The line passing through both of the given points or null if the parameters were invalid
     */
    public static Line Point_Point(Vector point1, Vector point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        float rise = point2.getY() - point1.getY();
        float run = point2.getX() - point1.getX();
        if (Math.abs(run) < Epsilon) {
            return Vertical(point1);
        }
        return Point_Slope(point1, rise / run);
    }

    /**
     * Create a line parallel to another line passing through a given point
     * @param line The line to create a parallel line to
     * @param point The point for the line to pass through
     * @return The line parallel to the given line passing through the given point or null if the parameters were
     * invalid
     */
    public static Line Parallel_Through(Line line, Vector point) {
        return Point_Slope(point, line.slope);
    }

    /**
     * Create a line perpendicular to another line passing through a given point
     * @param line The line to create a perpendicular line to
     * @param point The point for the line to pass through
     * @return The line perpendicular to the given line passing through the given point or null if the parameters were
     * invalid
     */
    public static Line Perpendicular_Through(Line line, Vector point) {
        if (line.isHorizontal()) {
            return Vertical(point);
        }
        return Point_Slope(point, -1.0f / line.slope);
    }

    /**
     * Create a line with the identity function y=x
     * @return The identity line
     */
    public static Line Identity() {
        Line l = new Line();
        l.slope = 1.0f;
        l.xIntercept = 0.0f;
        l.yIntercept = 0.0f;
        return l;
    }

    /**
     * Create a line lying on the x-axis function y=0
     * @return The x-axis line
     */
    public static Line X_Axis() {
        Line l = new Line();
        l.slope = 0.0f;
        l.xIntercept = Float.NaN;
        l.yIntercept = 0.0f;
        return l;
    }

    /**
     * Create a line lying on the y-axis function x=0
     * @return  The y-axis line
     */
    public static Line Y_Axis() {
        Line l = new Line();
        l.slope = Float.NaN;
        l.xIntercept = 0.0f;
        l.yIntercept = Float.NaN;
        return l;
    }

    /**
     * Construct a line with empty parameters
     */
    private Line() {
        this.slope = 0.0f;
        this.xIntercept = 0.0f;
        this.yIntercept = 0.0f;
    }

    /**
     * Compute the line with a given horizontal and vertical translation from this line
     * @param translation The horizontal and vertical translation to apply
     * @return The translated line
     */
    public Line translate(Vector translation) {
        if (translation == null) {
            return null;
        }
        Line l = new Line();
        if (isHorizontal()) {
            l.slope = 0.0f;
            l.xIntercept = Float.NaN;
            l.yIntercept = yIntercept + translation.getY();
            return l;
        }
        if (isVertical()) {
            l.slope = Float.NaN;
            l.xIntercept = xIntercept + translation.getX();
            l.yIntercept = Float.NaN;
            return l;
        }
        l.slope = slope;
        l.yIntercept = (-slope * translation.getX()) + yIntercept + translation.getY();
        l.xIntercept = -l.yIntercept / slope;
        return l;
    }

    /**
     * Compute the line with a given scale from this line
     * @param s The scale to apply
     * @return The scaled line
     */
    public Line scale(float s) {
        if (isVertical()) {
            return this.clone();
        }
        Line l = new Line();
        l.slope = slope * s;
        l.xIntercept = xIntercept;
        l.yIntercept = yIntercept * s;
        return l;
    }

    /**
     * Get the rise/run slope of this line
     * @return The slope of this line
     */
    public float getSlope() {
        return slope;
    }

    /**
     * Get the x-coordinate of the x-intercept of this line
     * @return The x-intercept of this line
     */
    public float getXIntercept() {
        return xIntercept;
    }

    /**
     * Get the y-coordinate of the y-intercept of this line
     * @return The y-intercept of this line
     */
    public float getYIntercept() {
        return yIntercept;
    }

    /**
     * Get the x-coordinate corresponding to a given y-coordinate on this line
     * @param y The y-coordinate to test
     * @return The x-coordinate corresponding to the given y-coordinate or NaN if there was none
     */
    public float getX(float y) {
        if (isHorizontal()) {
            return Float.NaN;
        }
        if (isVertical()) {
            return xIntercept;
        }
        return (y - yIntercept) / slope;
    }

    /**
     * Get the y-coordinate corresponding to a given x-coordinate on this line
     * @param x The x-coordinate to test
     * @return The y-coordinate corresponding to the given x-coordinate or NaN if there was none
     */
    public float getY(float x) {
        if (isHorizontal()) {
            return yIntercept;
        }
        if (isVertical()) {
            return Float.NaN;
        }
        return (slope * x) + yIntercept;
    }

    /**
     * Test whether this line is horizontal (has slope 0)
     * @return Whether this line is horizontal
     */
    public boolean isHorizontal() {
        if (isVertical()) {
            return false;
        }
        return Math.abs(slope) < Epsilon;
    }

    /**
     * Test whether this line is vertical (has slope +/-infinity)
     * @return Whether this line is vertical
     */
    public boolean isVertical() {
        return !Float.isFinite(slope);
    }

    /**
     * Get the angle this line's slope makes with the x-axis
     * @return The angle of this line
     */
    public Angle getAngle() {
        if (isHorizontal()) {
            return Angle.Zero();
        }
        if (isVertical()) {
            return Angle.Right();
        }
        return Angle.Radians((float)Math.atan(slope));
    }

    /**
     * Test whether this line is parallel to another line
     * @param l The line to test against this one
     * @return Whether this line is parallel to the given line
     */
    public boolean isParallelTo(Line l) {
        if (l == null) {
            return false;
        }
        if (isVertical() && l.isVertical()) {
            return true;
        }
        return Math.abs(slope - l.slope) < Epsilon;
    }

    /**
     * Test whether this line is perpendicular to another line
     * @param l The line to test against this one
     * @return Whether this line is perpendicular to the given line
     */
    public boolean isPerpendicularTo(Line l) {
        if (l == null) {
            return false;
        }
        if (isHorizontal() || isVertical() || l.isHorizontal() || l.isVertical()) {
            return (isHorizontal() && l.isVertical()) || (isVertical() && l.isHorizontal());
        }
        return Math.abs(slope + (1.0f / l.slope)) < Epsilon;
    }

    /**
     * Test whether this line contains a given point
     * @param p The point to test against this line
     * @return Whether the given point lies on this line
     */
    public boolean contains(Vector p) {
        if (p == null) {
            return false;
        }
        if (isVertical()) {
            return Math.abs(p.getX() - xIntercept) < Epsilon;
        }
        return Math.abs(getY(p.getX()) - p.getY()) < Epsilon;
    }

    /**
     * Get the intersection point of this line and another line
     * @param l The line to test against this one
     * @return The intersection point of this line and the given line or null if no intersection point exists
     */
    public Vector getIntersectionPoint(Line l) {
        if (l == null) {
            return null;
        }
        if (isParallelTo(l)) {
            return null;
        }
        if (isVertical()) {
            return Vector.Cartesian(xIntercept, l.getY(xIntercept));
        }
        if (l.isVertical()) {
            return Vector.Cartesian(l.xIntercept, getY(l.xIntercept));
        }
        float x = (l.yIntercept - yIntercept) / (slope - l.slope);
        return Vector.Cartesian(x, getY(x));
    }

    /**
     * Get the closest point on this line to a given point
     * @param p The point to find the closest point to
     * @return The closest point on this line to the given point
     */
    public Vector getClosestPointTo(Vector p) {
        if (p == null) {
            return null;
        }
        return Perpendicular_Through(this, p).getIntersectionPoint(this);
    }

    /**
     * Get the distance from a given point to the closest point on this line
     * @param p The point to find the distance to
     * @return The distance between the given point and this line
     */
    public float getDistanceTo(Vector p) {
        return p.subtract(getClosestPointTo(p)).getLength();
    }

    /**
     * Test whether this line intersects with another line
     * @param l The line to test against this one
     * @return Whether this line intersects with a given line
     */
    public boolean intersectsWith(Line l) {
        return !isParallelTo(l);
    }

    /**
     * Test whether this line is equal to another object (must be an instance of Line)
     * @param o The reference object with which to compare.
     * @return Whether the given line has the same slope, x-intercept, and y-intercept as this one
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Line l)) {
            return false;
        }
        if (isHorizontal() != l.isHorizontal()) {
            return false;
        }
        if (isVertical() != l.isVertical()) {
            return false;
        }
        if (isHorizontal()) {
            return Math.abs(yIntercept - l.yIntercept) < Epsilon;
        } else if (isVertical()) {
            return Math.abs(xIntercept - l.xIntercept) < Epsilon;
        }
        return Math.abs(slope - l.slope) < Epsilon && Math.abs(xIntercept - l.xIntercept) < Epsilon
                && Math.abs(yIntercept - l.yIntercept) < Epsilon;
    }

    /**
     * Convert this line to a string representation
     * @return The string representation of the equation of this line
     */
    @Override
    public String toString() {
        if (isHorizontal()) {
            return "line(y = " + yIntercept + ")";
        } else if (isVertical()) {
            return "line(x = " + xIntercept + ")";
        }
        return "line(y = " + slope + " x + " + yIntercept + ")";
    }

    /**
     * Make a copy of this line with the same slope and x and y-intercepts
     * @return A copy of this line
     */
    @Override
    public Line clone() {
        Line l;
        try {
            l = (Line)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        l.slope = slope;
        l.xIntercept = xIntercept;
        l.yIntercept = yIntercept;
        return l;
    }

}

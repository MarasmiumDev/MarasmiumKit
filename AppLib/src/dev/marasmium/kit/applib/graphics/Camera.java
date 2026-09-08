/**
 * File:        Camera.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.08
 * Purpose:     Defines a 2D camera for rendering sprites with translation, scale, and rotation properties
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;

public class Camera {

    private Vector position = null;
    private double scale = 0.0d;
    private Angle angle = null;

    public boolean initialize(Vector position, double scale, Angle angle) {
        if (!setPosition(position)) {
            return false;
        }
        if (!setScale(scale)) {
            return false;
        }
        if (setAngle(angle)) {
            return false;
        }
        return true;
    }

    public void destroy() {
        position = null;
        scale = 0.0d;
        angle = null;
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

    public double getScale() {
        return scale;
    }

    public boolean setScale(double scale) {
        if (scale <= 0.0d) {
            return false;
        }
        this.scale = scale;
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

    public float[] getProjectionMatrix() {
        float tx = (float)position.getX();
        float ty = (float)position.getY();
        float scale = (float)this.scale;
        float theta = (float)angle.getRadians();
        float width = (float)App.Window.getDimensions().getX();
        float height = (float)App.Window.getDimensions().getY();
        float c = (float)Math.cos(theta);
        float q = (float)Math.sin(theta);
        float sx = (float)(2.0d * scale / width);
        float sy = (float)(2.0d * scale / height);
        return new float[] {
                sx * c, -sy * q, 0.0f, 0.0f,
                sx * q, sy * c, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                -sx * (c * tx + q * ty), sy * (q * tx - c * ty), 0.0f, 1.0f,
        };
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Camera c)) {
            return false;
        }
        return c.position.equals(position) && c.scale == scale && c.angle.equals(angle);
    }

}

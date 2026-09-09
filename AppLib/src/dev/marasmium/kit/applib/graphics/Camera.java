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

public class Camera extends Body {

    private float scale = 0.0f;
    private float zoom = 0.0f;

    public boolean initialize(Vector position, float scale, Angle angle) {
        if (!super.initialize(position, angle)) {
            return false;
        }
        if (!setScale(scale)) {
            return false;
        }
        setZoom(0.0f);
        return true;
    }

    public void update(float deltaFrames) {
        super.update(deltaFrames);
        scale += zoom * deltaFrames;
        if (scale < 0.0f) {
            scale = 0.0f;
        }
    }

    public void destroy() {
        super.destroy();
        scale = 0.0f;
    }

    public float getScale() {
        return scale;
    }

    public boolean setScale(float scale) {
        if (scale < 0.0f) {
            return false;
        }
        this.scale = scale;
        return true;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
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
        float sx = (float)(2.0f * scale / width);
        float sy = (float)(2.0f * scale / height);
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

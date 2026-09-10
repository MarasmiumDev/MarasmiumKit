/**
 * File:        Camera.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.08
 * Purpose:     Defines a 2D camera for rendering sprites with position, scale, and rotation properties
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;

/**
 * A 2D camera for rendering sprites with position, scale, and rotation
 */
public class Camera extends Body {

    /**
     * The current scale of this camera's view
     */
    private float scale = 0.0f;
    /**
     * The rate of change of this camera's scale
     */
    private float zoom = 0.0f;

    /**
     * Initialize this camera with a position, scale, and angle
     * @param position The initial position of the centre of this camera's view
     * @param scale The initial scale of this camera's view
     * @param angle The initial angle of this camera's view
     * @return Whether the given position, scale, and angle were valid
     */
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

    /**
     * Update the position, scale, and rotation of this camera by their rates of change
     * @param deltaFrames The number of frames which have elapsed since the last call to update
     */
    public void update(float deltaFrames) {
        super.update(deltaFrames);
        scale += zoom * deltaFrames;
        if (scale < 0.0f) {
            scale = 0.0f;
        }
    }

    /**
     * Free this camera's memory
     */
    public void destroy() {
        super.destroy();
        scale = 0.0f;
    }

    /**
     * Get the current scale of this camera's view
     * @return The current scale of this camera's view
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the scale of this camera's view
     * @param scale The new scale of this camera's view
     * @return Whether the given scale was valid
     */
    public boolean setScale(float scale) {
        if (scale < 0.0f) {
            return false;
        }
        this.scale = scale;
        return true;
    }

    /**
     * Get the current rate of change of this camera's view
     * @return The current rate of change of this camera's view
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Set the rate of change of this camera's view
     * @param zoom The new rate of change of this camera's view
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Get the orthographic projection matrix for this camera's position, scale, and angle
     * @return This camera's projection matrix
     */
    public float[] getProjectionMatrix() {
        float positionX = position.getX();
        float positionY = position.getY();
        float scale = this.scale;
        float angle = this.angle.getRadians();
        float width = App.Window.getDimensions().getX();
        float height = App.Window.getDimensions().getY();
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        float scaleX = (2.0f * scale) / width;
        float scaleY = (2.0f * scale) / height;
        return new float[] {
                scaleX * cos, -scaleY * sin, 0.0f, 0.0f, scaleX * sin, scaleY * cos, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -scaleX * (cos * positionX + sin * positionY), scaleY * (sin * positionX - cos * positionY), 0.0f, 1.0f,
        };
    }

    /**
     * Test whether this camera has the same projection as another
     * @param o The reference object with which to compare (must be an instance of Camera)
     * @return Whether the given camera is equal to this one
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Camera c)) {
            return false;
        }
        return c.position.equals(position) && c.scale == scale && c.angle.equals(angle);
    }

}

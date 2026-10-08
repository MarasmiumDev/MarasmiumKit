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
    @Override
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
    @Override
    public void destroy() {
        super.destroy();
        scale = 0.0f;
        zoom = 0.0f;
    }

    /**
     * Test whether this camera has the same projection as another
     * @param o The reference object with which to compare (must be an instance of Camera)
     * @return Whether the given camera is equal to this one
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Camera camera)) {
            return false;
        }
        if (position == null || angle == null) {
            return false;
        }
        return camera.position.equals(position) && camera.scale == scale && camera.angle.equals(angle);
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
     * @return This camera's projection matrix or null if this camera has not yet been initialized
     */
    public float[] getProjectionMatrix() {
        if (position == null || angle == null) {
            return null;
        }
        Vector windowDimensions = App.Window.getDimensions();
        if (windowDimensions == null) {
            return null;
        }
        if (windowDimensions.getX() == 0.0f || windowDimensions.getY() == 0.0f) {
            return null;
        }
        Vector position = getPosition();
        float cosine = (float)Math.cos(angle.getRadians());
        float sine = (float)Math.sin(angle.getRadians());
        float scaleX = (2.0f * scale) / windowDimensions.getX();
        float scaleY = (2.0f * scale) / windowDimensions.getY();
        return new float[] {
                scaleX * cosine, -scaleY * sine, 0.0f, 0.0f, scaleX * sine, scaleY * cosine, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, -scaleX * (cosine * position.getX() + sine * position.getY()),
                scaleY * (sine * position.getX() - cosine * position.getY()), 0.0f, 1.0f,
        };
    }

}

/**
 * File:        Sprite.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.06
 * Purpose:     Defines a structure representing a drawable sprite as an animated quad
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

/**
 * A structure representing a drawable sprite
 */
public class Sprite extends Body {

    /**
     * The depth of this sprite in the rendered scene
     */
    private float depth = 0.0f;
    /**
     * The horizontal and vertical dimensions of this sprite
     */
    private Vector dimensions = null;
    /**
     * The rate of change of the dimensions of this sprite
     */
    private Vector growth = null;
    /**
     * The file path of the animation to draw on this sprite
     */
    private String animationFilePath = null;
    /**
     * The current frame index of this sprite's animation
     */
    private int animationFrame = 0;
    /**
     * Whether this sprite's animation is currently playing
     */
    private boolean animationPlaying = false;
    /**
     * Timer for animation frame updates
     */
    private float animationTimer = 0.0f;
    /**
     * Whether this sprite's animation is flipped horizontally when drawn
     */
    private boolean flippedHorizontally = false;
    /**
     * Whether this sprite's animation is flipped vertically when drawn
     */
    private boolean flippedVertically = false;

    /**
     * Initialize this sprite with a position, depth, dimensions, angle, and animation file path
     * @param position The initial position of this sprite
     * @param depth The initial depth of this sprite in the rendered scene
     * @param dimensions The initial dimensions of this sprite
     * @param angle The initial angle of this sprite
     * @param animationFilePath The initial animation file path for this sprite
     * @return Whether all parameters were valid and this sprite was initialized successfully
     */
    public boolean initialize(Vector position, float depth, Vector dimensions, Angle angle, String animationFilePath) {
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
        if (!setAnimationFilePath(animationFilePath)) {
            return false;
        }
        stopAnimation();
        setFlippedHorizontally(false);
        setFlippedVertically(false);
        return true;
    }

    /**
     * Update this sprite's position, dimensions, and angle by their rates of change and update its animation
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    public void update(float deltaFrames) {
        super.update(deltaFrames);
        dimensions = dimensions.add(growth.scalarMultiply(deltaFrames));
        if (animationPlaying) {
            final float targetFPS = App.Assets.getAnimation(animationFilePath).getTargetFPS();
            final float animationFrameTime = (float)App.Graphics.getTargetFPS() / targetFPS;
            animationTimer += deltaFrames;
            if (animationTimer >= animationFrameTime) {
                animationFrame += 1;
                animationFrame %= App.Assets.getAnimation(animationFilePath).getFrameCount();
                animationTimer = 0.0f;
            }
        }
    }

    /**
     * Free this sprite's memory
     */
    public void destroy() {
        super.destroy();
        depth = 0.0f;
        dimensions = null;
        growth = null;
        animationFilePath = null;
        animationFrame = 0;
        animationPlaying = false;
        animationTimer = 0.0f;
        flippedVertically = false;
        flippedHorizontally = false;
    }

    /**
     * Get the depth of this sprite in the rendered scene
     * @return The depth of this sprite
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Set the depth of this sprite in the rendered scene
     * @param depth The new depth for this sprite
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     * Get the dimensions of this sprite
     * @return The dimensions of this sprite
     */
    public Vector getDimensions() {
        return dimensions;
    }

    /**
     * Set the dimensions of this sprite
     * @param dimensions The new dimensions for this sprite
     * @return Whether the given dimensions are valid
     */
    public boolean setDimensions(Vector dimensions) {
        if (dimensions == null) {
            return false;
        }
        this.dimensions = dimensions;
        return true;
    }

    /**
     * Get the rate of change of the dimensions of this sprite
     * @return The rate of change of the dimensions of this sprite
     */
    public Vector getGrowth() {
        return growth;
    }

    /**
     * Set the rate of change of the dimensions of this sprite
     * @param growth The new rate of change of the dimensions of this sprite
     * @return Whether the given rate of change is valid
     */
    public boolean setGrowth(Vector growth) {
        if (growth == null) {
            return false;
        }
        this.growth = growth;
        return true;
    }

    /**
     * Get the file path of the current animation of this sprite
     * @return The current animation file path of this sprite
     */
    public String getAnimationFilePath() {
        return animationFilePath;
    }

    /**
     * Set the file path of the current animation of this sprite
     * @param animationFilePath The new animation file path for this sprite
     * @return Whether the given file path is valid
     */
    public boolean setAnimationFilePath(String animationFilePath) {
        if (animationFilePath == null) {
            return false;
        }
        if (animationFilePath.isEmpty()) {
            return false;
        }
        if (App.Assets.getAnimation(animationFilePath) == null) {
            return false;
        }
        this.animationFilePath = animationFilePath;
        return true;
    }

    /**
     * Get the current frame index of this sprite's animation
     * @return The current frame of this sprite's animation
     */
    public int getAnimationFrame() {
        return animationFrame;
    }

    /**
     * Set the frame index of this sprite's animation
     * @param animationFrame The new frame of this sprite's animation
     * @return Whether the given animation frame is valid
     */
    public boolean setAnimationFrame(int animationFrame) {
        if (animationFrame < 0 || animationFrame >= App.Assets.getAnimation(animationFilePath).getFrameCount()) {
            return false;
        }
        this.animationFrame = animationFrame;
        return true;
    }

    /**
     * Test whether this sprite's animation is currently playing
     * @return Whether this sprite's animation is playing
     */
    public boolean isAnimationPlaying() {
        return animationPlaying;
    }

    /**
     * Set this sprite's current animation to advance on updates by its target FPS
     */
    public void playAnimation() {
        animationPlaying = true;
    }

    /**
     * Pause this sprite's current animation
     */
    public void pauseAnimation() {
        animationPlaying = false;
    }

    /**
     * Pause this sprite's current animation and reset its frame index to the beginning of the animation
     */
    public void stopAnimation() {
        animationPlaying = false;
        animationFrame = 0;
        animationTimer = 0.0f;
    }

    /**
     * Test whether this sprite's animation is reflected horizontally when drawn
     * @return Whether this sprite's animation is reflected horizontally
     */
    public boolean isFlippedHorizontally() {
        return flippedHorizontally;
    }

    /**
     * Set whether this sprite's animation is reflected horizontally when drawn
     * @param flippedHorizontally Whether this sprite's animation should be reflected horizontally
     */
    public void setFlippedHorizontally(boolean flippedHorizontally) {
        this.flippedHorizontally = flippedHorizontally;
    }

    /**
     * Test whether this sprite's animation is reflected vertically when drawn
     * @return Whether this sprite's animation is reflected vertically
     */
    public boolean isFlippedVertically() {
        return flippedVertically;
    }

    /**
     * Set whether this sprite's animation is reflected vertically when drawn
     * @param flippedVertically Whether this sprite's animation should be reflected vertically
     */
    public void setFlippedVertically(boolean flippedVertically) {
        this.flippedVertically = flippedVertically;
    }

}

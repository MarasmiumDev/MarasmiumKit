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

public class Sprite extends Body {

    private float depth = 0.0f;
    private Vector dimensions = null;
    private Vector growth = null;
    private String animationFilePath = null;
    private int animationFrame = 0;
    private boolean animationPlaying = false;
    private float animationTimer = 0.0f;
    private boolean flippedHorizontally = false;
    private boolean flippedVertically = false;

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

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public Vector getDimensions() {
        return dimensions;
    }

    public boolean setDimensions(Vector dimensions) {
        if (dimensions == null) {
            return false;
        }
        this.dimensions = dimensions;
        return true;
    }

    public Vector getGrowth() {
        return growth;
    }

    public boolean setGrowth(Vector growth) {
        if (growth == null) {
            return false;
        }
        this.growth = growth;
        return true;
    }

    public String getAnimationFilePath() {
        return animationFilePath;
    }

    public boolean setAnimationFilePath(String animationFilePath) {
        if (animationFilePath == null) {
            return false;
        }
        if (animationFilePath.isEmpty()) {
            return false;
        }
        this.animationFilePath = animationFilePath;
        return true;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }

    public boolean setAnimationFrame(int animationFrame) {
        if (animationFrame < 0 || animationFrame >= App.Assets.getAnimation(animationFilePath).getFrameCount()) {
            return false;
        }
        this.animationFrame = animationFrame;
        return true;
    }

    public boolean isAnimationPlaying() {
        return animationPlaying;
    }

    public void playAnimation() {
        animationPlaying = true;
    }

    public void pauseAnimation() {
        animationPlaying = false;
    }

    public void stopAnimation() {
        animationPlaying = false;
        animationFrame = 0;
        animationTimer = 0.0f;
    }

    public boolean isFlippedHorizontally() {
        return flippedHorizontally;
    }

    public void setFlippedHorizontally(boolean flippedHorizontally) {
        this.flippedHorizontally = flippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return flippedVertically;
    }

    public void setFlippedVertically(boolean flippedVertically) {
        this.flippedVertically = flippedVertically;
    }

}

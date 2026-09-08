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

public class Sprite {

    private Vector position = null;
    private double depth = 0.0d;
    private Vector velocity = null;
    private Vector dimensions = null;
    private Vector growth = null;
    private Angle angle = null;
    private Angle rotation = null;
    private String animationFilePath = null;
    private int animationFrame = 0;
    private boolean animationPlaying = false;
    private double animationTimer = 0.0d;
    private boolean flippedHorizontally = false;
    private boolean flippedVertically = false;

    public boolean initialize(Vector position, double depth, Vector dimensions, Angle angle, String animationFilePath) {
        if (!setPosition(position)) {
            return false;
        }
        setDepth(depth);
        if (!setVelocity(Vector.Cartesian(0.0d, 0.0d))) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setGrowth(Vector.Cartesian(0.0d, 0.0d))) {
            return false;
        }
        if (!setAngle(angle)) {
            return false;
        }
        if (!setRotation(Angle.Radians(0.0d))) {
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

    public void update(double deltaFrames) {
        position = position.add(velocity.scalarMultiply(deltaFrames));
        dimensions = dimensions.add(growth.scalarMultiply(deltaFrames));
        angle = angle.add(rotation.scalarMultiply(deltaFrames));
        if (animationPlaying) {
            final double targetFPS = App.Assets.getAnimation(animationFilePath).getTargetFPS();
            final double animationFrameTime = (double)App.Graphics.getTargetFPS() / targetFPS;
            animationTimer += deltaFrames;
            if (animationTimer >= animationFrameTime) {
                animationFrame += 1;
                animationFrame %= App.Assets.getAnimation(animationFilePath).getFrameCount();
                animationTimer = 0.0d;
            }
        }
    }

    public void destroy() {
        position = null;
        depth = 0.0d;
        velocity = null;
        dimensions = null;
        growth = null;
        angle = null;
        rotation = null;
        animationFilePath = null;
        animationFrame = 0;
        animationPlaying = false;
        animationTimer = 0.0d;
        flippedVertically = false;
        flippedHorizontally = false;
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

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public boolean setVelocity(Vector velocity) {
        if (velocity == null) {
            return false;
        }
        this.velocity = velocity;
        return true;
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

    public Angle getRotation() {
        return rotation;
    }

    public boolean setRotation(Angle rotation) {
        if (rotation == null) {
            return false;
        }
        this.rotation = rotation;
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
        animationTimer = 0.0d;
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

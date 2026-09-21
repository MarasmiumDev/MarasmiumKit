/**
 * File:        Glyph.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.16
 * Purpose:     Defines a data structure containing the identification and positioning metrics of a glyph in a typeface
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.data.Vector;

/**
 * Data structure containing the identification and positioning metrics of a glyph in a typeface
 */
public class Glyph {

    /**
     * The frame index of this glyph's image in the typeface's animation
     */
    private int animationFrame = 0;
    /**
     * The horizontal and vertical advances of this glyph
     */
    private Vector advances = null;
    /**
     * The vertical offset of this glyph in a string in pixels
     */
    private int offset = 0;

    /**
     * Initialize this glyph with an animation frame index, horizontal advance, and vertical offset
     * @param animationFrame The frame index of this glyph's image in the typeface's animation
     * @param advances The horizontal and vertical advances of this glyph
     * @param offset The vertical offset of this glyph
     * @return Whether all parameters were valid and this glyph was initialized successfully
     */
    public boolean initialize(int animationFrame, Vector advances, int offset) {
        if (!setAnimationFrame(animationFrame)) {
            return false;
        }
        if (!setAdvances(advances)) {
            return false;
        }
        setOffset(offset);
        return true;
    }

    /**
     * Free this glyph's memory
     */
    public void destroy() {
        animationFrame = 0;
        advances = null;
        offset = 0;
    }

    /**
     * Get this glyph's animation frame index in the typeface's animation
     * @return This glyph's animation frame index
     */
    public int getAnimationFrame() {
        return animationFrame;
    }

    /**
     * Set this glyph's animation frame index in the typeface's animation
     * @param animationFrame This glyph's new animation frame index
     * @return Whether the given index was valid and was set successfully
     */
    public boolean setAnimationFrame(int animationFrame) {
        if (animationFrame < 0) {
            return false;
        }
        this.animationFrame = animationFrame;
        return true;
    }

    /**
     * Get the horizontal and vertical advances of this glyph
     * @return This glyph's horizontal and vertical advances
     */
    public Vector getAdvances() {
        return advances;
    }

    /**
     * Set the horizontal and vertical advances of this glyph
     * @param advances This glyph's new horizontal and vertical advances
     * @return Whether the given advances were valid and was set successfully
     */
    public boolean setAdvances(Vector advances) {
        if (advances == null) {
            return false;
        }
        if (advances.getX() < 0.0f) {
            return false;
        }
        this.advances = advances;
        return true;
    }

    /**
     * Get the vertical offset of this glyph in a string in pixels
     * @return This glyph's vertical offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Set the vertical offset of this glyph in a string in pixels
     * @param offset This glyph's new vertical offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Get a string representing this glyph
     * @return This glyph's string representation
     */
    @Override
    public String toString() {
        return "glyph(frame " + animationFrame + ", advances " + advances + "px, offset " + offset + "px)";
    }

}

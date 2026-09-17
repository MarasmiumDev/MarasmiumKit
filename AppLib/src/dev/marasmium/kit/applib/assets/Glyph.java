/**
 * File:        Glyph.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.16
 * Purpose:     Defines a data structure containing the identification and positioning metrics of a glyph in a typeface
 */

package dev.marasmium.kit.applib.assets;

/**
 * Data structure containing the identification and positioning metrics of a glyph in a typeface
 */
public class Glyph {

    /**
     * The frame index of this glyph's image in the typeface's animation
     */
    private int animationFrame = 0;
    /**
     * The horizontal distance between the left border of this glyph and the glyph after it in a string in pixels
     */
    private int advance = 0;
    /**
     * The vertical offset of this glyph in a string in pixels
     */
    private int offset = 0;

    /**
     * Initialize this glyph with an animation frame index, horizontal advance, and vertical offset
     * @param animationFrame The frame index of this glyph's image in the typeface's animation
     * @param advance The horizontal advance of this glyph
     * @param offset The vertical offset of this glyph
     * @return Whether all parameters were valid and this glyph was initialized successfully
     */
    public boolean initialize(int animationFrame, int advance, int offset) {
        if (!setAnimationFrame(animationFrame)) {
            return false;
        }
        if (!setAdvance(advance)) {
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
        advance = 0;
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
     * Get the horizontal distance from the left border of this glyph to the next glyph in a string in pixels
     * @return This glyph's horizontal advance
     */
    public int getAdvance() {
        return advance;
    }

    /**
     * Set the horizontal distance from the left border of this glyph to the next glyph in a string in pixels
     * @param advance This glyph's new horizontal advance
     * @return Whether the given advance was valid and was set successfully
     */
    public boolean setAdvance(int advance) {
        if (advance < 0) {
            return false;
        }
        this.advance = advance;
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

}

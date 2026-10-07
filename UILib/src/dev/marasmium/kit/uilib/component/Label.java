/**
 * File:        Label.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.28
 * Purpose:     Defines a text label user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;

/**
 * A text label user-interface component
 */
public class Label extends UIComponent {

    /**
     * The text to appear in this label
     */
    protected String text = null;
    /**
     * The starting position of the text when not aligned
     */
    protected Vector textPosition = null;
    /**
     * The horizontal alignment of the text within this label's background
     */
    protected Alignment horizontalTextAlignment = null;
    /**
     * The vertical alignment of the text within this label's background
     */
    protected Alignment verticalTextAlignment = null;

    /**
     * Initialize this label's memory
     * @param position The initial position of this label in percent of the application's window dimensions
     * @param dimensions The initial dimensions of this label in percent of the application's window dimensions
     * @param animationFilePath The animation to be drawn on the background of this label
     * @param text The text to appear in this label
     * @param textPosition The starting position of the text to appear in this label when not aligned in pixels
     * @param horizontalAlignment The horizontal alignment of this label's text within its background
     * @param verticalAlignment The vertical alignment of this label's text within its background
     * @return Whether all parameters were valid and this label was initialized successfully
     */
    public boolean initialize(Vector position, Vector dimensions, String animationFilePath, String text,
                              Vector textPosition, Alignment horizontalAlignment, Alignment verticalAlignment) {
        setEnabled(true);
        setVisible(true);
        if (!sprite.initialize(Vector.Zero(), 0.0f, Vector.Zero(), Angle.Zero(), animationFilePath)) {
            return false;
        }
        sprite.stopAnimation();
        if (!setText(text)) {
            return false;
        }
        if (!setTextPosition(textPosition)) {
            return false;
        }
        setHorizontalTextAlignment(horizontalAlignment);
        setVerticalTextAlignment(verticalAlignment);
        if (!setPosition(position)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        return true;
    }

    /**
     * Draw graphics for this label's background and text if this label is visible
     */
    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        App.Graphics.submit(parent.getCamera(), sprite);
        App.Graphics.submit(parent.getCamera(), text, parent.getTypefaceFilePath(), sprite, parent.getTextPadding(),
                parent.getBaseDepth() + 0.1f, parent.getTextSize(), textPosition, horizontalTextAlignment,
                verticalTextAlignment);
    }

    /**
     * Update this label's background animation
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
    }

    /**
     * Free this label's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        text = null;
        horizontalTextAlignment = null;
        verticalTextAlignment = null;
    }

    /**
     * Get the text currently appearing in this label
     * @return This label's text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text to appear in this label
     * @param text This label's new text
     * @return Whether the given text was set successfully
     */
    public boolean setText(String text) {
        if (text == null) {
            return false;
        }
        this.text = text;
        return true;
    }

    /**
     * Get the starting position of this label's text when not aligned in pixels
     * @return THis label's starting text position
     */
    public Vector getTextPosition() {
        return textPosition;
    }

    /**
     * Set the starting position of this label's text when not aligned in pixels
     * @param textPosition The starting position for this label's text when not aligned in pixels
     * @return Whether the given position was valid
     */
    public boolean setTextPosition(Vector textPosition) {
        if (textPosition == null) {
            return false;
        }
        this.textPosition = textPosition;
        return true;
    }

    /**
     * Get the horizontal alignment of this label's text within its background
     * @return The horizontal alignment of this label
     */
    public Alignment getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }

    /**
     * Set the horizontal alignment for this label's text within its background
     * @param horizontalAlignment The new horizontal alignment for this label
     */
    public void setHorizontalTextAlignment(Alignment horizontalAlignment) {
        this.horizontalTextAlignment = horizontalAlignment;
    }

    /**
     * Get the vertical alignment of this label's text within its background
     * @return The vertical alignment of this label
     */
    public Alignment getVerticalTextAlignment() {
        return verticalTextAlignment;
    }

    /**
     * Set the vertical alignment for this label's text within its background
     * @param verticalAlignment The new vertical alignment for this label
     */
    public void setVerticalTextAlignment(Alignment verticalAlignment) {
        this.verticalTextAlignment = verticalAlignment;
    }

}

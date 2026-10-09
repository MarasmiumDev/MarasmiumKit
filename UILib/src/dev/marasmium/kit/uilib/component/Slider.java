/**
 * File:        Slider.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.01
 * Purpose:     Defines a slider user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Constants;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.uilib.UIEvent;

/**
 * A slider user-interface component
 */
public class Slider extends UIComponent {

    /**
     * Whether this slider is currently selected
     */
    protected boolean selected = false;
    /**
     * The animation to play when this slider is no longer selected
     */
    protected String unselectedAnimationFilePath = null;
    /**
     * The animation to play when this slider is selected
     */
    protected String selectedAnimationFilePath = null;
    /**
     * The text label to appear on this slider
     */
    protected final Label label = new Label();
    /**
     * The alignment of this slider's text label about its background
     */
    protected Alignment labelAlignment = null;
    /**
     * This slider's cursor/indicator representing its current value
     */
    protected final Button cursor = new Button();
    /**
     * The vertical alignment of this slider's cursor within its background
     */
    protected Alignment verticalCursorAlignment = null;
    /**
     * The minimum value this slider represents
     */
    protected float minimumValue = 0.0f;
    /**
     * The maximum value this slider represents
     */
    protected float maximumValue = 0.0f;
    /**
     * The number of discrete, evenly-spaced values this slider's cursor snaps to
     */
    protected int valueCount = 0;
    /**
     * The current value of represented by this slider
     */
    protected float value = 0.0f;

    /**
     * Initialize this slider's memory
     * @param position The initial position of this slider in percent of the application's window dimensions
     * @param dimensions The initial dimensions of this slider in percent of the application's window dimensions
     * @param animationFilePaths This slider's unselected and selected animations
     * @param labelText The text to appear in this slider's label
     * @param labelAlignment The alignment for this slider's text label about its background
     * @param cursorDimensions The initial dimensions of this slider's cursor/indicator in percent of its background's
     *                         dimensions
     * @param cursorAnimationFilePaths The unselected, selected, and pressed animations for this slider's cursor
     * @param verticalCursorAlignment The vertical alignment for this slider's cursor on its background
     * @param minimumValue The minimum value for this slider to represent
     * @param maximumValue The maximum value for this slider to represent
     * @param valueCount The number of discrete, evenly-spaced values for this slider's cursor to snap to
     * @return Whether all parameters were valid and this slider was initialized successfully
     */
    public boolean initialize(Vector position, Vector dimensions, String[] animationFilePaths, String labelText,
                              Alignment labelAlignment, Vector cursorDimensions, String[] cursorAnimationFilePaths,
                              Alignment verticalCursorAlignment, float minimumValue, float maximumValue,
                              int valueCount) {
        if (animationFilePaths == null) {
            return false;
        }
        if (animationFilePaths.length != 2) {
            return false;
        }
        setEnabled(true);
        setVisible(true);
        if (!sprite.initialize(Vector.Zero(), 0.0f, Vector.Zero(), Angle.Zero(), animationFilePaths[0])) {
            return false;
        }
        sprite.setAnimationFrame(sprite.getAnimationFrameCount() - 1);
        if (!setUnselectedAnimationFilePath(animationFilePaths[0])) {
            return false;
        }
        if (!setSelectedAnimationFilePath(animationFilePaths[1])) {
            return false;
        }
        if (!label.initialize(Vector.Zero(), Vector.Zero(), "", labelText, Vector.Zero(), Alignment.Center,
                Alignment.Center)) {
            return false;
        }
        if (!addComponent(label)) {
            return false;
        }
        if (!cursor.initialize(Vector.Zero(), Vector.Zero(), cursorAnimationFilePaths, "", Alignment.Center)) {
            return false;
        }
        if (!addComponent(cursor)) {
            return false;
        }
        setMinimumValue(minimumValue);
        setMaximumValue(maximumValue);
        if (!setValueCount(valueCount)) {
            return false;
        }
        this.verticalCursorAlignment = verticalCursorAlignment;
        if (!setPosition(position)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setCursorDimensions(cursorDimensions)) {
            return false;
        }
        if (!setVerticalCursorAlignment(verticalCursorAlignment)) {
            return false;
        }
        if (!setLabelAlignment(labelAlignment)) {
            return false;
        }
        if (!setValue(minimumValue)) {
            return false;
        }
        return true;
    }

    /**
     * Process user-input to this slider
     */
    @Override
    public void processInput() {
        if (!enabled) {
            return;
        }
        if (parent == null) {
            return;
        }
        label.processInput();
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)
                && App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
            if (!selected) {
                setSelected(true);
            }
        } else {
            if (selected && !cursor.isPressed()) {
                setSelected(false);
                if (parent != null) {
                    parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Slider_Value_Set);
                }
            }
        }
        if (selected && App.Input.mouse.isButtonDown(MouseButton.Left)) {
            if (App.Window.getDimensions().getX() == 0.0f) {
                return;
            }
            Vector mouseCursorPosition = App.Input.mouse.getCursorPosition(parent.getCamera());
            Vector cursorPosition = cursor.getPosition();
            cursorPosition.setX((mouseCursorPosition.getX() - cursor.getSprite().getDimensions().getX() * 0.5f)
                    / App.Window.getDimensions().getX());
            cursor.setPosition(cursorPosition);
            float percent = (cursor.getSprite().getPosition().getX() - sprite.getPosition().getX())
                    / (sprite.getDimensions().getX() - cursor.getSprite().getDimensions().getX());
            setValue(minimumValue + ((maximumValue - minimumValue) * percent));
            cursor.setPressed(true);
        } else {
            cursor.setPressed(false);
            cursor.setSelected(App.Input.mouse.getCursorPosition(parent.getCamera()).inside(cursor.getSprite()));
        }
    }

    /**
     * Draw graphics for this slider's background, cursor, and label
     */
    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        if (parent != null) {
            App.Graphics.submit(parent.getCamera(), sprite);
        }
        label.draw();
        cursor.draw();
    }

    /**
     * Update the cursor logic, background animation, and background animations of this slider
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
        label.update(deltaFrames);
        cursor.update(deltaFrames);
    }

    /**
     * Free this slider's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        unselectedAnimationFilePath = null;
        selectedAnimationFilePath = null;
        label.destroy();
        labelAlignment = null;
        cursor.destroy();
        verticalCursorAlignment = null;
        minimumValue = 0.0f;
        maximumValue = 0.0f;
        valueCount = 0;
        value = 0.0f;
    }

    /**
     * Override for the user-interface event callback function for children of this slider
     * @param groupID Any integer value
     * @param componentID Any integer value
     * @param event Any event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {}

    /**
     * Set this slider's position in percent of the application's window dimensions
     * @param position This component's new position in percent of the application's window dimensions
     * @return Whether this slider's position was set successfully
     */
    @Override
    public boolean setPosition(Vector position) {
        if (!super.setPosition(position)) {
            return false;
        }
        Vector labelPosition = position.clone();
        if (labelAlignment == Alignment.Left) {
            labelPosition.setX(position.getX() - getDimensions().getX());
        } else if (labelAlignment == Alignment.Right) {
            labelPosition.setX(position.getX() + getDimensions().getX());
        } else if (labelAlignment == Alignment.Bottom) {
            labelPosition.setY(position.getY() - getDimensions().getY());
        } else if (labelAlignment == Alignment.Top) {
            labelPosition.setY(position.getY() + getDimensions().getY());
        }
        if (!label.setPosition(labelPosition)) {
            return false;
        }
        if (!setValue(value)) {
            return false;
        }
        if (!setVerticalCursorAlignment(verticalCursorAlignment)) {
            return false;
        }
        return true;
    }

    /**
     * Set this slider's dimensions in percent of the application's window dimensions
     * @param dimensions This component's new dimensions in percent of the application's window dimensions
     * @return Whether this slider's dimensions were set successfully
     */
    @Override
    public boolean setDimensions(Vector dimensions) {
        Vector cursorDimensions = getCursorDimensions();
        if (!super.setDimensions(dimensions)) {
            return false;
        }
        if (!label.setDimensions(dimensions)) {
            return false;
        }
        if (!setCursorDimensions(cursorDimensions)) {
            return false;
        }
        if (getPosition() == null) {
            return true;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Set this slider's minimum dimensions in pixels
     * @param minimumDimensions This component's new minimum dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    @Override
    public boolean setMinimumDimensions(Vector minimumDimensions) {
        if (!super.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        if (!label.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        if (getPosition() == null) {
            return true;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Set this slider's maximum dimensions in pixels
     * @param maximumDimensions This component's new maximum dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    @Override
    public boolean setMaximumDimensions(Vector maximumDimensions) {
        if (!super.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        if (!label.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        if (getPosition() == null) {
            return true;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Test whether this slider is currently selected
     * @return Whether this slider is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set whether this slider is selected and play the corresponding animation
     * @param selected Whether this slider should be selected
     * @return Whether this slider's selected state was changed successfully
     */
    public boolean setSelected(boolean selected) {
        if (this.selected == selected) {
            return false;
        }
        if (parent != null) {
            if (selected) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Slider_Selected);
            } else {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Slider_Unselected);
            }
        }
        this.selected = selected;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(selected ? selectedAnimationFilePath : unselectedAnimationFilePath);
        sprite.playAnimation(1);
        cursor.setSelected(selected);
        return true;
    }

    /**
     * Get the animation to play when this slider is no longer selected
     * @return This slider's unselected animation
     */
    public String getUnselectedAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    /**
     * Set the animation to play when this slider is no longer selected
     * @param unselectedAnimationFilePath This slider's new unselected animation
     * @return Whether the given animation was valid
     */
    public boolean setUnselectedAnimationFilePath(String unselectedAnimationFilePath) {
        if (unselectedAnimationFilePath == null) {
            return false;
        }
        this.unselectedAnimationFilePath = unselectedAnimationFilePath;
        return true;
    }

    /**
     * Get the animation to play when this slider is selected
     * @return This slider's selected animation
     */
    public String getSelectedAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    /**
     * Set the animation to play when this slider is selected
     * @param selectedAnimationFilePath This slider's new selected animation
     * @return Whether the given animation was valid
     */
    public boolean setSelectedAnimationFilePath(String selectedAnimationFilePath) {
        if (selectedAnimationFilePath == null) {
            return false;
        }
        this.selectedAnimationFilePath = selectedAnimationFilePath;
        return true;
    }

    /**
     * Get this slider's text label
     * @return This slider's text label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Get the alignment of this slider's text label about its background
     * @return This slider's label alignment
     */
    public Alignment getLabelAlignment() {
        return labelAlignment;
    }

    /**
     * Set the alignment of this slider's text label about its background
     * @param labelAlignment This slider's new label alignment
     * @return Whether the given alignment was valid
     */
    public boolean setLabelAlignment(Alignment labelAlignment) {
        if (labelAlignment == null) {
            return false;
        }
        this.labelAlignment = labelAlignment;
        if (labelAlignment == Alignment.Left) {
            label.setHorizontalTextAlignment(Alignment.Right);
        } else if (labelAlignment == Alignment.Right) {
            label.setHorizontalTextAlignment(Alignment.Left);
        } else if (labelAlignment == Alignment.Bottom) {
            label.setVerticalTextAlignment(Alignment.Top);
        } else if (labelAlignment == Alignment.Top) {
            label.setVerticalTextAlignment(Alignment.Bottom);
        }
        if (!setPosition(getPosition().clone())) {
            return false;
        }
        return true;
    }

    /**
     * Get this slider cursor/indicator
     * @return This slider's cursor
     */
    public Button getCursor() {
        return cursor;
    }

    /**
     * Get the dimensions of this slider's cursor in percent of its background's dimensions
     * @return This slider's cursor dimensions in percent of its background's dimensions
     */
    public Vector getCursorDimensions() {
        if (cursor.getDimensions() == null || getDimensions() == null) {
            return null;
        }
        return cursor.getDimensions().elementDivide(getDimensions());
    }

    /**
     * Set the dimensions of this slider's cursor in percent of its background's dimensions
     * @param cursorDimensions This slider's new cursor dimensions in percent of its background's dimensions
     * @return Whether the given dimensions were valid
     */
    public boolean setCursorDimensions(Vector cursorDimensions) {
        if (cursorDimensions == null || getDimensions() == null) {
            return false;
        }
        return cursor.setDimensions(cursorDimensions.elementMultiply(getDimensions()));
    }

    /**
     * Get the minimum dimensions of this slider's cursor in pixels
     * @return This slider's minimum cursor dimensions in pixels
     */
    public Vector getMinimumCursorDimensions() {
        return cursor.getMinimumDimensions();
    }

    /**
     * Set the minimum dimensions of this slider's cursor in pixels
     * @param minimumCursorDimensions This slider's new minimum cursor dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    public boolean setMinimumCursorDimensions(Vector minimumCursorDimensions) {
        return cursor.setMinimumDimensions(minimumCursorDimensions);
    }

    /**
     * Get the maximum dimensions of this slider's cursor in pixels
     * @return This slider's maximum cursor dimensions in pixels
     */
    public Vector getMaximumCursorDimensions() {
        return cursor.getMaximumDimensions();
    }

    /**
     * Set the maximum dimensions of this slider's cursor in pixels
     * @param maximumCursorDimensions This slider's new maximum cursor dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    public boolean setMaximumCursorDimensions(Vector maximumCursorDimensions) {
        return cursor.setMaximumDimensions(maximumCursorDimensions);
    }

    /**
     * Get the vertical alignment of this slider's cursor on its background
     * @return This slider's vertical cursor alignment
     */
    public Alignment getVerticalCursorAlignment() {
        return verticalCursorAlignment;
    }

    /**
     * Set the vertical alignment of this slider's cursor on its background
     * @param verticalCursorAlignment This slider's new vertical cursor alignment
     * @return Whether the given alignment was valid
     */
    public boolean setVerticalCursorAlignment(Alignment verticalCursorAlignment) {
        if (verticalCursorAlignment == null) {
            return false;
        }
        if (cursor.getPosition() == null || getCursorDimensions() == null || getDimensions() == null
                || sprite.getPosition() == null || sprite.getDimensions() == null) {
            return false;
        }
        this.verticalCursorAlignment = verticalCursorAlignment;
        Vector cursorPixelPosition = cursor.getPosition().elementMultiply(App.Window.getDimensions());
        Vector cursorPixelDimensions = getCursorDimensions().elementMultiply(getDimensions()).elementMultiply(
                App.Window.getDimensions());
        if (verticalCursorAlignment == Alignment.Bottom) {
            cursorPixelPosition.setY(sprite.getPosition().getY());
        } else if (verticalCursorAlignment == Alignment.Top) {
            cursorPixelPosition.setY(sprite.getPosition().getY() + sprite.getDimensions().getY()
                    - cursorPixelDimensions.getY());
        } else if (verticalCursorAlignment == Alignment.Center) {
            cursorPixelPosition.setY(sprite.getPosition().getY() + (sprite.getDimensions().getY() * 0.5f)
                    - (cursorPixelDimensions.getY() * 0.5f));
        } else {
            return false;
        }
        if (!cursor.setPosition(cursorPixelPosition.elementDivide(App.Window.getDimensions()))) {
            return false;
        }
        return setValue(value);
    }

    /**
     * Get the minimum value represented by this slider
     * @return This slider's minimum value
     */
    public float getMinimumValue() {
        return minimumValue;
    }

    /**
     * Set the minimum value to be represented by this slider
     * @param minimumValue This slider's new minimum value
     * @return Whether the minimum value was set successfully
     */
    public boolean setMinimumValue(float minimumValue) {
        this.minimumValue = minimumValue;
        return setValue(value);
    }

    /**
     * Get the maximum value represented by this slider
     * @return This slider's maximum value
     */
    public float getMaximumValue() {
        return maximumValue;
    }

    /**
     * Set the maximum value to be represented by this slider
     * @param maximumValue This slider's new maximum value
     * @return Whether the maximum value was set successfully
     */
    public boolean setMaximumValue(float maximumValue) {
        this.maximumValue = maximumValue;
        return setValue(value);
    }

    /**
     * Get the number of discrete, evenly-spaced values this slider's cursor snaps to
     * @return This slider's value count
     */
    public int getValueCount() {
        return valueCount;
    }

    /**
     * Set the number of discrete, evenly-spaced values this slider's cursor should snap to
     * @param valueCount This slider's new value count (0 for continuous)
     * @return Whether the value could was set successfully
     */
    public boolean setValueCount(int valueCount) {
        if (valueCount < 0 || valueCount == 1) {
            return false;
        }
        this.valueCount = valueCount;
        return true;
    }

    /**
     * Get the current value represented by this slider
     * @return This slider's value
     */
    public float getValue() {
        return value;
    }

    /**
     * Set the value represented by this slider
     * @param value The new value for this slider, between its minimum and maximum values
     * @return Whether the new value was set successfully
     */
    public boolean setValue(float value) {
        if (maximumValue - minimumValue < Constants.Epsilon) {
            return false;
        }
        if (value < minimumValue) {
            value = minimumValue;
        } else if (value > maximumValue) {
            value = maximumValue;
        }
        if (valueCount > 0) {
            float valueStep = (maximumValue - minimumValue) / (float)(valueCount - 1);
            for (float step = minimumValue; step < maximumValue; step += valueStep) {
                float midStep = step + (valueStep * 0.5f);
                float fullStep = step + valueStep;
                if (step < value && value <= midStep) {
                    value = step;
                } else if (midStep < value && value <= fullStep) {
                    value = fullStep;
                }
            }
        }
        this.value = value;
        float percent = (value - minimumValue) / (maximumValue - minimumValue);
        if (cursor.getPosition() == null || sprite.getPosition() == null || sprite.getDimensions() == null
                || cursor.getSprite() == null) {
            return false;
        }
        if (cursor.getSprite().getDimensions() == null) {
            return false;
        }
        Vector cursorPixelPosition = cursor.getPosition().elementMultiply(App.Window.getDimensions());
        cursorPixelPosition.setX(sprite.getPosition().getX()
                + ((sprite.getDimensions().getX() - cursor.getSprite().getDimensions().getX()) * percent));
        return cursor.setPosition(cursorPixelPosition.elementDivide(App.Window.getDimensions()));
    }

}

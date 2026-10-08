/**
 * File:        Button.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.28
 * Purpose:     Defines a button user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.uilib.UIEvent;

/**
 * A clickable button user-interface component
 */
public class Button extends UIComponent {

    /**
     * Whether this button is currently selected (moused-over)
     */
    protected boolean selected = false;
    /**
     * Whether this button is currently pressed (clicked down)
     */
    protected boolean pressed = false;
    /**
     * The animation to play when this button is no longer selected
     */
    protected String unselectedAnimationFilePath = null;
    /**
     * The animation to play when this button is selected
     */
    protected String selectedAnimationFilePath = null;
    /**
     * The animation to play when this button is pressed down
     */
    protected String pressedAnimationFilePath = null;
    /**
     * The text label to appear on this button
     */
    protected final Label label = new Label();
    /**
     * The alignment of this button's text label about its background
     */
    protected Alignment labelAlignment = null;

    /**
     * Initialize this button's memory
     * @param position The initial position for this button in percent of the application's window dimensions
     * @param dimensions The initial dimensions for this button in percent of the application's window dimensions
     * @param animationFilePaths The animations to play when this button is unselected, selected, and pressed
     * @param labelText The text to appear on this button's label
     * @param labelAlignment The alignment for this button's label about its background
     * @return Whether all parameters wre valid and this button was initialized successfully
     */
    public boolean initialize(Vector position, Vector dimensions, String[] animationFilePaths, String labelText,
                              Alignment labelAlignment) {
        if (animationFilePaths == null) {
            return false;
        }
        if (animationFilePaths.length != 3) {
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
        if (!setPressedAnimationFilePath(animationFilePaths[2])) {
            return false;
        }
        if (!label.initialize(Vector.Zero(), Vector.Zero(), "", labelText, Vector.Zero(), Alignment.Center,
                Alignment.Center)) {
            return false;
        }
        if (!addComponent(label)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setPosition(position)) {
            return false;
        }
        if (!setLabelAlignment(labelAlignment)) {
            return false;
        }
        return true;
    }

    /**
     * Process user-input to this button
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
        if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
            setSelected(true);
        } else {
            if (!pressed) {
                setSelected(false);
            }
        }
        if (pressed) {
            if (App.Input.mouse.isButtonReleased(MouseButton.Left)) {
                setPressed(false);
            }
        }
        if (selected) {
            if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
                if (!pressed) {
                    setPressed(true);
                }
            }
        }
    }

    /**
     * Draw this button's background animation and text label
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
    }

    /**
     * Update this button's background animation
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
        label.update(deltaFrames);
    }

    /**
     * Free this button's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        pressed = false;
        unselectedAnimationFilePath = null;
        selectedAnimationFilePath = null;
        pressedAnimationFilePath = null;
        label.destroy();
        labelAlignment = null;
    }

    /**
     * Set this button's position in percent of the application's window dimensions
     * @param position This button's new position in percent of the application's window dimensions
     * @return Whether the given position was valid
     */
    @Override
    public boolean setPosition(Vector position) {
        if (getDimensions() == null) {
            System.out.println("Error");
            return false;
        }
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
        return true;
    }

    /**
     * Set this button's dimensions in percent of the application's window dimensions
     * @param dimensions This button's new dimensions in percent of the application's window dimensions
     * @return Whether the given dimensions were valid
     */
    @Override
    public boolean setDimensions(Vector dimensions) {
        if (!super.setDimensions(dimensions)) {
            return false;
        }
        if (!label.setDimensions(dimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Set the minimum dimensions for this button in pixels
     * @param minimumDimensions This button's new minimum dimensions in pixels
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
        return true;
    }

    /**
     * Set the maximum dimensions for this button in pixels
     * @param maximumDimensions This button's new maximum dimensions in pixels
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
        return true;
    }

    /**
     * Test whether this button is currently selected / moused-over
     * @return Whether this button is currently selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set whether this button should be selected / moused-over and play the corresponding animation
     * @param selected Whether this button should be unselected or selected
     * @return Whether this button's selected state was set successfully
     */
    public boolean setSelected(boolean selected) {
        if (this.selected == selected) {
            return false;
        }
        if (parent != null) {
            if (selected) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Selected);
            } else {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Unselected);
            }
        }
        this.selected = selected;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(selected ? selectedAnimationFilePath : unselectedAnimationFilePath);
        sprite.playAnimation(1);
        if (!selected) {
            pressed = false;
        }
        return true;
    }

    /**
     * Test whether this button is currently pressed down
     * @return Whether this button is currently pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Set whether this button should be pressed down and play the corresponding animation
     * @param pressed Whether this button should be pressed down
     * @return Whether this button's pressed state was set successfully
     */
    public boolean setPressed(boolean pressed) {
        if (this.pressed == pressed) {
            return false;
        }
        if (parent != null) {
            if (pressed) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Pressed);
            } else {
                if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
                    parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Released);
                }
            }
        }
        this.pressed = pressed;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(pressed ? pressedAnimationFilePath : selectedAnimationFilePath);
        sprite.playAnimation(1);
        return true;
    }

    /**
     * Get the animation to be played when this button is no longer selected/moused-over
     * @return This button's unselected animation
     */
    public String getUnselectedAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    /**
     * Set the animation to be played when this button is no longer selected/moused-over
     * @param unselectedAnimationFilePath This button's new unselected animation
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
     * Get the animation to be played when this button is selected/moused-over
     * @return This button's selected animation
     */
    public String getSelectedAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    /**
     * Set the animation to be played when this button is selected/moused-over
     * @param selectedAnimationFilePath This button's new selected animation
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
     * Get the animation to be played when this button is pressed down
     * @return This button's pressed animation
     */
    public String getPressedAnimationFilePath() {
        return pressedAnimationFilePath;
    }

    /**
     * Set the animation to be played when this button is pressed down
     * @param pressedAnimationFilePath This button's new pressed animation
     * @return Whether the given animation was valid
     */
    public boolean setPressedAnimationFilePath(String pressedAnimationFilePath) {
        if (pressedAnimationFilePath == null) {
            return false;
        }
        this.pressedAnimationFilePath = pressedAnimationFilePath;
        return true;
    }

    /**
     * Get this button's text label
     * @return This button's text label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Get the alignment of this button's text label about its background
     * @return This button's label alignment
     */
    public Alignment getLabelAlignment() {
        return labelAlignment;
    }

    /**
     * Set the alignment of this button's text label about its background
     * @param labelAlignment This button's new label alignment
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

}

/**
 * File:        Switch.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.29
 * Purpose:     Defines a switch user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.uilib.UIEvent;

/**
 * A switch/toggle-button user-interface component
 */
public class Switch extends Button {

    /**
     * Whether this switch is currently toggled on
     */
    protected boolean on = false;

    /**
     * Initialize this switch's memory
     * @param position The initial position for this switch in percent of the application's window dimensions
     * @param dimensions The initial dimensions for this switch in percent of the application's window dimensions
     * @param animationFilePaths The animations to play when this switch is toggled off and on
     * @param labelText The text to appear on this switch's label
     * @param labelAlignment The alignment for this switch's label about its background
     * @return Whether all parameters were valid and this switch was initialized successfully
     */
    public boolean initialize(Vector position, Vector dimensions, String[] animationFilePaths, String labelText,
                              Alignment labelAlignment) {
        if (animationFilePaths == null) {
            return false;
        }
        if (animationFilePaths.length != 2) {
            return false;
        }
        if (!super.initialize(position, dimensions, new String[] { "", "", "" }, labelText, labelAlignment)) {
            return false;
        }
        if (!setOffAnimationFilePath(animationFilePaths[0])) {
            return false;
        }
        if (!setOnAnimationFilePath(animationFilePaths[1])) {
            return false;
        }
        sprite.setAnimationFilePath(animationFilePaths[0]);
        sprite.setAnimationFrame(sprite.getAnimationFrameCount() - 1);
        setOn(false);
        return true;
    }

    /**
     * Process user-input to this switch
     */
    @Override
    public void processInput() {
        if (!enabled) {
            return;
        }
        label.processInput();
        if (parent == null) {
            return;
        }
        if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
            if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
                if (!selected) {
                    selected = true;
                }
            }
        } else {
            selected = false;
        }
        if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite) && selected) {
            if (App.Input.mouse.isButtonReleased(MouseButton.Left)) {
                setOn(!on);
            }
        }
    }

    /**
     * Free this switch's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        on = false;
    }

    /**
     * Override for the Button class's unselected animation getter function
     * @return Always null
     */
    @Override
    public String getUnselectedAnimationFilePath() {
        return null;
    }

    /**
     * Override for the Button class's unselected animation setter function
     * @param unselectedAnimationFilePath Any string value
     * @return Always true
     */
    @Override
    public boolean setUnselectedAnimationFilePath(String unselectedAnimationFilePath) {
        return true;
    }

    /**
     * Override for the Button class's selected animation getter function
     * @return Always null
     */
    @Override
    public String getSelectedAnimationFilePath() {
        return null;
    }

    /**
     * Override for the Button class's selected animation setter function
     * @param selectedAnimationFilePath Any string value
     * @return Always true
     */
    @Override
    public boolean setSelectedAnimationFilePath(String selectedAnimationFilePath) {
        return true;
    }

    /**
     * Override for the Button class's pressed animation getter function
     * @return Always null
     */
    @Override
    public String getPressedAnimationFilePath() {
        return null;
    }

    /**
     * Override for the Button class's pressed animation setter function
     * @param pressedAnimationFilePath Any string value
     * @return Always true
     */
    @Override
    public boolean setPressedAnimationFilePath(String pressedAnimationFilePath) {
        return true;
    }

    /**
     * Override for the Button class's selected getter function
     * @return Always false
     */
    @Override
    public boolean isSelected() {
        return false;
    }

    /**
     * Override for the Button class's selected setter function
     * @param selected Any boolean value
     * @return Always false
     */
    @Override
    public boolean setSelected(boolean selected) {
        return false;
    }

    /**
     * Get the animation to be played when this switch is toggled off
     * @return This switch's off animation
     */
    public String getOffAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    /**
     * Set the animation to be played when this switch is toggled off
     * @param offAnimationFilePath This switch's new off animation
     * @return Whether the given animation was valid
     */
    public boolean setOffAnimationFilePath(String offAnimationFilePath) {
        if (offAnimationFilePath == null) {
            return false;
        }
        unselectedAnimationFilePath = offAnimationFilePath;
        return true;
    }

    /**
     * Get the animation to be played when this switch is toggled on
     * @return This switch's on animation
     */
    public String getOnAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    /**
     * Set the animation to be played when this switch is toggled on
     * @param onAnimationFilePath This switch's new on animation
     * @return Whether the given animation was valid
     */
    public boolean setOnAnimationFilePath(String onAnimationFilePath) {
        if (onAnimationFilePath == null) {
            return false;
        }
        selectedAnimationFilePath = onAnimationFilePath;
        return true;
    }

    /**
     * Test whether this switch is currently toggled on
     * @return Whether this switch is on
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Set whether this switch should be toggled on and play the corresponding animation
     * @param on Whether this switch should be toggled on
     */
    public void setOn(boolean on) {
        if (!on && this.on) {
            this.on = false;
            sprite.stopAnimation();
            sprite.setAnimationFilePath(unselectedAnimationFilePath);
            sprite.playAnimation(1);
            if (parent != null) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Switch_Off);
            }
        } else if (on && !this.on) {
            this.on = true;
            sprite.stopAnimation();
            sprite.setAnimationFilePath(selectedAnimationFilePath);
            sprite.playAnimation(1);
            if (parent != null) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Switch_On);
            }
        }
    }

}

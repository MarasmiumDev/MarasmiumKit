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

public class Switch extends Button {

    protected boolean on = false;

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

    @Override
    public void processInput() {
        label.processInput();
        if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
            if (App.Input.mouse.isButtonReleased(MouseButton.Left)) {
                setOn(!on);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        on = false;
    }

    @Override
    public String getUnselectedAnimationFilePath() {
        return null;
    }

    @Override
    public boolean setUnselectedAnimationFilePath(String unselectedAnimationFilePath) {
        return true;
    }

    @Override
    public String getSelectedAnimationFilePath() {
        return null;
    }

    @Override
    public boolean setSelectedAnimationFilePath(String selectedAnimationFilePath) {
        return true;
    }

    @Override
    public String getPressedAnimationFilePath() {
        return null;
    }

    @Override
    public boolean setPressedAnimationFilePath(String pressedAnimationFilePath) {
        return true;
    }

    public String getOffAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    public boolean setOffAnimationFilePath(String offAnimationFilePath) {
        if (offAnimationFilePath == null) {
            return false;
        }
        unselectedAnimationFilePath = offAnimationFilePath;
        return true;
    }

    public String getOnAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    public boolean setOnAnimationFilePath(String onAnimationFilePath) {
        if (onAnimationFilePath == null) {
            return false;
        }
        selectedAnimationFilePath = onAnimationFilePath;
        return true;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if (!on && this.on) {
            this.on = false;
            sprite.stopAnimation();
            sprite.setAnimationFilePath(unselectedAnimationFilePath);
            sprite.playAnimation(1);
            parent.switchEvent(parent.getGroupID(), componentID, UIEvent.Switch_Off);
        } else if (on && !this.on) {
            this.on = true;
            sprite.stopAnimation();
            sprite.setAnimationFilePath(selectedAnimationFilePath);
            sprite.playAnimation(1);
            parent.switchEvent(parent.getGroupID(), componentID, UIEvent.Switch_On);
        }
    }

}

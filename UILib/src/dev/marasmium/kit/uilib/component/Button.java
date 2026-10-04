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

public class Button extends UIComponent {

    protected boolean pressed = false;
    protected String unselectedAnimationFilePath = null;
    protected String selectedAnimationFilePath = null;
    protected String pressedAnimationFilePath = null;
    protected final Label label = new Label();
    protected Alignment labelAlignment = null;

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
        if (!setPosition(position)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setLabelAlignment(labelAlignment)) {
            return false;
        }
        return true;
    }

    @Override
    public void processInput() {
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

    @Override
    public void draw() {
        App.Graphics.submit(parent.getCamera(), sprite);
        label.draw();
    }

    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
        label.update(deltaFrames);
    }

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

    @Override
    public boolean setPosition(Vector position) {
        boolean success = true;
        if (!super.setPosition(position)) {
            success = false;
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
        };
        if (!label.setPosition(labelPosition)) {
            success = false;
        }
        return success;
    }

    @Override
    public boolean setDimensions(Vector dimensions) {
        boolean success = true;
        if (!super.setDimensions(dimensions)) {
            success = false;
        }
        if (!label.setDimensions(dimensions)) {
            success = false;
        }
        return success;
    }

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

    @Override
    public boolean setSelected(boolean selected) {
        if (!super.setSelected(selected)) {
            return false;
        }
        sprite.stopAnimation();
        sprite.setAnimationFilePath(selected ? selectedAnimationFilePath : unselectedAnimationFilePath);
        sprite.playAnimation(1);
        if (!selected) {
            pressed = false;
        }
        return true;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean setPressed(boolean pressed) {
        if (this.pressed == pressed) {
            return false;
        }
        if (pressed) {
            parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Pressed);
        } else {
            if (App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite)) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Button_Released);
            }
        }
        this.pressed = pressed;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(pressed ? pressedAnimationFilePath : selectedAnimationFilePath);
        sprite.playAnimation(1);
        return true;
    }

    public String getUnselectedAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    public boolean setUnselectedAnimationFilePath(String unselectedAnimationFilePath) {
        if (unselectedAnimationFilePath == null) {
            return false;
        }
        this.unselectedAnimationFilePath = unselectedAnimationFilePath;
        return true;
    }

    public String getSelectedAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    public boolean setSelectedAnimationFilePath(String selectedAnimationFilePath) {
        if (selectedAnimationFilePath == null) {
            return false;
        }
        this.selectedAnimationFilePath = selectedAnimationFilePath;
        return true;
    }

    public String getPressedAnimationFilePath() {
        return pressedAnimationFilePath;
    }

    public boolean setPressedAnimationFilePath(String pressedAnimationFilePath) {
        if (pressedAnimationFilePath == null) {
            return false;
        }
        this.pressedAnimationFilePath = pressedAnimationFilePath;
        return true;
    }

    public Label getLabel() {
        return label;
    }

    public Alignment getLabelAlignment() {
        return labelAlignment;
    }

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

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

public class Slider extends UIComponent {

    protected String unselectedAnimationFilePath = null;
    protected String selectedAnimationFilePath = null;
    protected final Label label = new Label();
    protected Alignment labelAlignment = null;
    protected final Button cursor = new Button();
    protected Alignment verticalCursorAlignment = null;
    protected float minimumValue = 0.0f;
    protected float maximumValue = 0.0f;
    protected int valueCount = 0;
    protected float value = 0.0f;

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

    @Override
    public void processInput() {
        if (!enabled) {
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
            }
        }
        if (selected && App.Input.mouse.isButtonDown(MouseButton.Left)) {
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

    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        App.Graphics.submit(parent.getCamera(), sprite);
        label.draw();
        cursor.draw();
    }

    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
        label.update(deltaFrames);
        cursor.update(deltaFrames);
    }

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

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {}

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
        };
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
        return setPosition(getPosition().clone());
    }

    @Override
    public boolean setMinimumDimensions(Vector minimumDimensions) {
        if (!super.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        if (!label.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    @Override
    public boolean setMaximumDimensions(Vector maximumDimensions) {
        if (!super.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        if (!label.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    @Override
    public boolean setSelected(boolean selected) {
        if (!super.setSelected(selected)) {
            return false;
        }
        sprite.stopAnimation();
        sprite.setAnimationFilePath(selected ? selectedAnimationFilePath : unselectedAnimationFilePath);
        sprite.playAnimation(1);
        cursor.setSelected(selected);
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

    public Button getCursor() {
        return cursor;
    }

    public Vector getCursorDimensions() {
        return cursor.getDimensions().elementDivide(getDimensions());
    }

    public boolean setCursorDimensions(Vector cursorDimensions) {
        return cursor.setDimensions(cursorDimensions.elementMultiply(getDimensions()));
    }

    public Vector getMinimumCursorDimensions() {
        return cursor.getMinimumDimensions();
    }

    public boolean setMinimumCursorDimensions(Vector minimumCursorDimensions) {
        return cursor.setMinimumDimensions(minimumCursorDimensions);
    }

    public Vector getMaximumCursorDimensions() {
        return cursor.getMaximumDimensions();
    }

    public boolean setMaximumCursorDimensions(Vector maximumCursorDimensions) {
        return cursor.setMaximumDimensions(maximumCursorDimensions);
    }

    public Alignment getVerticalCursorAlignment() {
        return verticalCursorAlignment;
    }

    public boolean setVerticalCursorAlignment(Alignment verticalCursorAlignment) {
        if (verticalCursorAlignment == null) {
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

    public float getMinimumValue() {
        return minimumValue;
    }

    public boolean setMinimumValue(float minimumValue) {
        this.minimumValue = minimumValue;
        return setValue(value);
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public boolean setMaximumValue(float maximumValue) {
        this.maximumValue = maximumValue;
        return setValue(value);
    }

    public int getValueCount() {
        return valueCount;
    }

    public boolean setValueCount(int valueCount) {
        if (valueCount < 0 || valueCount == 1) {
            return false;
        }
        this.valueCount = valueCount;
        return true;
    }

    public float getValue() {
        return value;
    }

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
        Vector cursorPixelPosition = cursor.getPosition().elementMultiply(App.Window.getDimensions());
        cursorPixelPosition.setX(sprite.getPosition().getX()
                + ((sprite.getDimensions().getX() - cursor.getSprite().getDimensions().getX()) * percent));
        if (!cursor.setPosition(cursorPixelPosition.elementDivide(App.Window.getDimensions()))) {
            return false;
        }
        return true;
    }

}

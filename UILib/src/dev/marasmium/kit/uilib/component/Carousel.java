/**
 * File:        Carousel.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.04
 * Purpose:     Implements a carousel/spinner user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.uilib.UIEvent;

import java.util.ArrayList;

public class Carousel extends Label {

    protected final Label label = new Label();
    protected Alignment labelAlignment = null;
    protected final Button backButton = new Button();
    protected final Button nextButton = new Button();
    protected float buttonWidth = 0.0f;
    protected float minimumButtonWidth = 0.0f;
    protected float maximumButtonWidth = 0.0f;
    protected Alignment horizontalButtonAlignment = null;
    protected final ArrayList<String> values = new ArrayList<>();
    protected int valueIndex = 0;

    public boolean initialize(Vector position, Vector dimensions, String animationFilePath,
                              Alignment horizontalTextAlignment, Alignment verticalTextAlignment, String labelText,
                              Alignment labelAlignment, String[] buttonAnimationFilePaths, float buttonWidth,
                              Alignment horizontalButtonAlignment, String[] values, int valueIndex) {
        if (buttonAnimationFilePaths == null) {
            return false;
        }
        if (buttonAnimationFilePaths.length != 3) {
            return false;
        }
        if (!label.initialize(Vector.Zero(), Vector.Zero(), "", labelText, Vector.Zero(), Alignment.Center,
                Alignment.Center)) {
            return false;
        }
        if (!addComponent(label)) {
            return false;
        }
        if (!backButton.initialize(Vector.Zero(), Vector.Zero(), buttonAnimationFilePaths, "", Alignment.Center)) {
            return false;
        }
        if (!addComponent(backButton)) {
            return false;
        }
        if (!nextButton.initialize(Vector.Zero(), Vector.Zero(), buttonAnimationFilePaths, "", Alignment.Center)) {
            return false;
        }
        if (!addComponent(nextButton)) {
            return false;
        }
        this.horizontalButtonAlignment = horizontalButtonAlignment;
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setPosition(position)) {
            return false;
        }
        if (!super.initialize(position, dimensions, animationFilePath, "", Vector.Zero(), horizontalTextAlignment,
                verticalTextAlignment)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        if (!setPosition(position)) {
            return false;
        }
        if (!setButtonWidth(buttonWidth)) {
            return false;
        }
        if (!setLabelAlignment(labelAlignment)) {
            return false;
        }
        if (!setHorizontalButtonAlignment(horizontalButtonAlignment)) {
            return false;
        }
        for (String value : values) {
            if (!addValue(value)) {
                return false;
            }
        }
        if (!setValueIndex(valueIndex)) {
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
        backButton.processInput();
        nextButton.processInput();
    }

    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        label.draw();
        backButton.draw();
        nextButton.draw();
        super.draw();
    }

    @Override
    public void update(float deltaFrames) {
        label.update(deltaFrames);
        backButton.update(deltaFrames);
        nextButton.update(deltaFrames);
        sprite.update(deltaFrames);
    }

    @Override
    public void destroy() {
        super.destroy();
        label.destroy();
        labelAlignment = null;
        backButton.destroy();
        nextButton.destroy();
        buttonWidth = 0.0f;
        horizontalButtonAlignment = null;
        values.clear();
        valueIndex = 0;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (componentID == backButton.getComponentID()) {
            if (event == UIEvent.Button_Released) {
                if (valueIndex > 0) {
                    setValueIndex(valueIndex - 1);
                } else {
                    setValueIndex(values.size() - 1);
                }
                parent.componentEvent(parent.getGroupID(), this.componentID, UIEvent.Carousel_Value_Set);
            }
        } else if (componentID == nextButton.getComponentID()) {
            if (event == UIEvent.Button_Released) {
                if (valueIndex < values.size() - 1) {
                    setValueIndex(valueIndex + 1);
                } else {
                    setValueIndex(0);
                }
                parent.componentEvent(parent.getGroupID(), this.componentID, UIEvent.Carousel_Value_Set);
            }
        }
    }

    @Override
    public Vector getPosition() {
        if (horizontalButtonAlignment == Alignment.Left) {
            return nextButton.getPosition().clone();
        } else if (horizontalButtonAlignment == Alignment.Center) {
            return backButton.getPosition().clone();
        } else if (horizontalButtonAlignment == Alignment.Right) {
            return super.getPosition();
        }
        return null;
    }

    @Override
    public boolean setPosition(Vector position) {
        if (horizontalButtonAlignment == Alignment.Left) {
            if (!backButton.setPosition(Vector.Cartesian(position.getX(),
                    position.getY() + nextButton.getDimensions().getY()))) {
                return false;
            }
            if (!nextButton.setPosition(position.clone())) {
                return false;
            }
            if (!super.setPosition(Vector.Cartesian(position.getX() + nextButton.getDimensions().getX(),
                    position.getY()))) {
                return false;
            }
        } else if (horizontalButtonAlignment == Alignment.Center) {
            if (!backButton.setPosition(position.clone())) {
                return false;
            }
            if (!nextButton.setPosition(Vector.Cartesian(position.getX() + backButton.getDimensions().getX()
                            + super.getDimensions().getX(), position.getY()))) {
                return false;
            }
            if (!super.setPosition(Vector.Cartesian(position.getX() + backButton.getDimensions().getX(),
                    position.getY()))) {
                return false;
            }
        } else if (horizontalButtonAlignment == Alignment.Right) {
            if (!backButton.setPosition(Vector.Cartesian(position.getX() + super.getDimensions().getX(),
                    position.getY() + nextButton.getDimensions().getY()))) {
                return false;
            }
            if (!nextButton.setPosition(Vector.Cartesian(position.getX() + super.getDimensions().getX(),
                    position.getY()))) {
                return false;
            }
            if (!super.setPosition(position.clone())) {
                return false;
            }
        } else {
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

    @Override
    public Vector getDimensions() {
        if (horizontalButtonAlignment == Alignment.Center) {
            return Vector.Cartesian(backButton.getDimensions().getX() + super.getDimensions().getX()
                    + nextButton.getDimensions().getX(), super.getDimensions().getY());
        } else if (horizontalButtonAlignment == Alignment.Left || horizontalButtonAlignment == Alignment.Right) {
            return Vector.Cartesian(backButton.getDimensions().getX() + super.getDimensions().getX(),
                    super.getDimensions().getY());
        }
        return null;
    }

    @Override
    public boolean setDimensions(Vector dimensions) {
        if (horizontalButtonAlignment == Alignment.Center) {
            if (!super.setDimensions(Vector.Cartesian(dimensions.getX() - (dimensions.getX() * buttonWidth * 2.0f),
                    dimensions.getY()))) {
                return false;
            }
            if (!backButton.setDimensions(Vector.Cartesian(dimensions.getX() * buttonWidth, dimensions.getY()))) {
                return false;
            }
            if (!nextButton.setDimensions(Vector.Cartesian(dimensions.getX() * buttonWidth, dimensions.getY()))) {
                return false;
            }
        } else if (horizontalButtonAlignment == Alignment.Left || horizontalButtonAlignment == Alignment.Right) {
            if (!super.setDimensions(Vector.Cartesian(dimensions.getX() - (dimensions.getX() * buttonWidth),
                    dimensions.getY()))) {
                return false;
            }
            if (!backButton.setDimensions(Vector.Cartesian(dimensions.getX() * buttonWidth,
                    dimensions.getY() * 0.5f))) {
                return false;
            }
            if (!nextButton.setDimensions(Vector.Cartesian(dimensions.getX() * buttonWidth,
                    dimensions.getY() * 0.5f))) {
                return false;
            }
        }
        if (!label.setDimensions(dimensions)) {
            return false;
        }
        if (getPosition() != null) {
            setPosition(getPosition().clone());
        }
        return true;
    }

    @Override
    public Vector getMinimumDimensions() {
        if (labelAlignment == Alignment.Center) {
            return Vector.Cartesian(backButton.getMinimumDimensions().getX() + super.getMinimumDimensions().getX()
                    + nextButton.getMinimumDimensions().getX(), super.getMinimumDimensions().getY());
        } else if (labelAlignment == Alignment.Left || labelAlignment == Alignment.Right) {
            return Vector.Cartesian(backButton.getMinimumDimensions().getX() + super.getMinimumDimensions().getX(),
                    super.getMinimumDimensions().getY());
        }
        return null;
    }

    @Override
    public boolean setMinimumDimensions(Vector minimumDimensions) {
        if (minimumDimensions == null) {
            if (!super.setMinimumDimensions(null)) {
                return false;
            }
            if (!label.setMinimumDimensions(null)) {
                return false;
            }
            if (!backButton.setMinimumDimensions(null)) {
                return false;
            }
            if (!nextButton.setMinimumDimensions(null)) {
                return false;
            }
            return true;
        }
        if (horizontalButtonAlignment == Alignment.Center) {
            if (!super.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX()
                    - (minimumDimensions.getX() * buttonWidth * 2.0f), minimumDimensions.getY()))) {
                return false;
            }
            if (!backButton.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX() * buttonWidth,
                    minimumDimensions.getY()))) {
                return false;
            }
            if (!nextButton.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX() * buttonWidth,
                    minimumDimensions.getY()))) {
                return false;
            }
        } else if (horizontalButtonAlignment == Alignment.Left || horizontalButtonAlignment == Alignment.Right) {
            if (!super.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX()
                    - (minimumDimensions.getX() * buttonWidth), minimumDimensions.getY()))) {
                return false;
            }
            if (!backButton.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX() * buttonWidth,
                    minimumDimensions.getY() * 0.5f))) {
                return false;
            }
            if (!nextButton.setMinimumDimensions(Vector.Cartesian(minimumDimensions.getX() * buttonWidth,
                    minimumDimensions.getY() * 0.5f))) {
                return false;
            }
        }
        if (!label.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    @Override
    public Vector getMaximumDimensions() {
        if (labelAlignment == Alignment.Center) {
            return Vector.Cartesian(backButton.getMaximumDimensions().getX() + super.getMaximumDimensions().getX()
                    + nextButton.getMaximumDimensions().getX(), super.getMaximumDimensions().getY());
        } else if (labelAlignment == Alignment.Left || labelAlignment == Alignment.Right) {
            return Vector.Cartesian(backButton.getMaximumDimensions().getX() + super.getMaximumDimensions().getX(),
                    super.getMaximumDimensions().getY());
        }
        return null;
    }

    @Override
    public boolean setMaximumDimensions(Vector maximumDimensions) {
        if (maximumDimensions == null) {
            if (!super.setMaximumDimensions(null)) {
                return false;
            }
            if (!label.setMaximumDimensions(null)) {
                return false;
            }
            if (!backButton.setMaximumDimensions(null)) {
                return false;
            }
            if (!nextButton.setMaximumDimensions(null)) {
                return false;
            }
            return true;
        }
        if (horizontalButtonAlignment == Alignment.Center) {
            if (!super.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX()
                    - (maximumDimensions.getX() * buttonWidth * 2.0f), maximumDimensions.getY()))) {
                return false;
            }
            if (!backButton.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX() * buttonWidth,
                    maximumDimensions.getY()))) {
                return false;
            }
            if (!nextButton.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX() * buttonWidth,
                    maximumDimensions.getY()))) {
                return false;
            }
        } else if (horizontalButtonAlignment == Alignment.Left || horizontalButtonAlignment == Alignment.Right) {
            if (!super.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX()
                    - (maximumDimensions.getX() * buttonWidth), maximumDimensions.getY()))) {
                return false;
            }
            if (!backButton.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX() * buttonWidth,
                    maximumDimensions.getY() * 0.5f))) {
                return false;
            }
            if (!nextButton.setMaximumDimensions(Vector.Cartesian(maximumDimensions.getX() * buttonWidth,
                    maximumDimensions.getY() * 0.5f))) {
                return false;
            }
        }
        if (!label.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
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
        if (getPosition() == null) {
            return true;
        }
        if (!setPosition(getPosition().clone())) {
            return false;
        }
        return true;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public float getButtonWidth() {
        return buttonWidth;
    }

    public boolean setButtonWidth(float buttonWidth) {
        this.buttonWidth = buttonWidth;
        if (getDimensions() == null) {
            return true;
        }
        return setDimensions(getDimensions().clone());
    }

    public float getMinimumButtonWidth() {
        return minimumButtonWidth;
    }

    public boolean setMinimumButtonWidth(float minimumButtonWidth) {
        if (!backButton.setMinimumDimensions(Vector.Cartesian(minimumButtonWidth,
                backButton.getMinimumDimensions().getY()))) {
            return false;
        }
        if (!nextButton.setMinimumDimensions(Vector.Cartesian(minimumButtonWidth,
                nextButton.getMinimumDimensions().getY()))) {
            return false;
        }
        return true;
    }

    public float getMaximumButtonWidth() {
        return maximumButtonWidth;
    }

    public boolean setMaximumButtonWidth(float maximumButtonWidth) {
        if (!backButton.setMaximumDimensions(Vector.Cartesian(maximumButtonWidth,
                backButton.getMaximumDimensions().getY()))) {
            return false;
        }
        if (!nextButton.setMaximumDimensions(Vector.Cartesian(maximumButtonWidth,
                nextButton.getMaximumDimensions().getY()))) {
            return false;
        }
        return true;
    }

    public Alignment getHorizontalButtonAlignment() {
        return horizontalButtonAlignment;
    }

    public boolean setHorizontalButtonAlignment(Alignment horizontalButtonAlignment) {
        if (horizontalButtonAlignment == null) {
            return false;
        }
        if (!(horizontalButtonAlignment == Alignment.Left || horizontalButtonAlignment == Alignment.Center
                || horizontalButtonAlignment == Alignment.Right)) {
            return false;
        }
        if (horizontalButtonAlignment == Alignment.Center) {
            backButton.getSprite().setFlippedHorizontally(false);
            backButton.getSprite().setFlippedVertically(false);
            nextButton.getSprite().setFlippedHorizontally(true);
            nextButton.getSprite().setFlippedVertically(false);
        } else {
            backButton.getSprite().setFlippedHorizontally(false);
            backButton.getSprite().setFlippedVertically(false);
            nextButton.getSprite().setFlippedHorizontally(false);
            nextButton.getSprite().setFlippedVertically(true);
        }
        this.horizontalButtonAlignment = horizontalButtonAlignment;
        if (getPosition() == null) {
            return true;
        }
        return setPosition(getPosition().clone());
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getValue(int valueIndex) {
        if (valueIndex < 0 || valueIndex >= values.size()) {
            return null;
        }
        return values.get(valueIndex);
    }

    public String getValue() {
        return getValue(valueIndex);
    }

    public boolean addValue(String value, int valueIndex) {
        if (valueIndex < 0) {
            return false;
        }
        if (valueIndex >= values.size()) {
            values.addLast(value);
        } else {
            values.add(valueIndex, value);
        }
        return true;
    }

    public boolean addValue(String value) {
        return addValue(value, values.size());
    }

    public boolean removeValue(String value) {
        if (!values.contains(value)) {
            return false;
        }
        return values.remove(value);
    }

    public int getValueIndex() {
        return valueIndex;
    }

    public boolean setValueIndex(int valueIndex) {
        if (values.isEmpty()) {
            this.valueIndex = 0;
            text = "";
            return true;
        }
        if (valueIndex < 0 || valueIndex >= values.size()) {
            return false;
        }
        this.valueIndex = valueIndex;
        text = values.get(valueIndex);
        return true;
    }

}

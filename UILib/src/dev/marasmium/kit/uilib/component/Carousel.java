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

/**
 * A carousel/spinner user-interface component
 */
public class Carousel extends Label {

    /**
     * The text label to appear on this carousel
     */
    protected final Label label = new Label();
    /**
     * The alignment of this carousel's text label about its background
     */
    protected Alignment labelAlignment = null;
    /**
     * This carousel's back/previous value button
     */
    protected final Button backButton = new Button();
    /**
     * This carousel's next/forward value button
     */
    protected final Button nextButton = new Button();
    /**
     * The width of this carousel's back and next buttons in percent of its background's width
     */
    protected float buttonWidth = 0.0f;
    /**
     * The minimum width of this carousel's back and next buttons in pixels
     */
    protected float minimumButtonWidth = 0.0f;
    /**
     * The maximum width of this carousel's back and next buttons in pixels
     */
    protected float maximumButtonWidth = 0.0f;
    /**
     * The horizontal alignment of this carousel's back and next buttons about its background
     */
    protected Alignment horizontalButtonAlignment = null;
    /**
     * The set of values this carousel can be set to
     */
    protected final ArrayList<String> values = new ArrayList<>();
    /**
     * The index of the current value represented by this carousel
     */
    protected int valueIndex = 0;

    /**
     * Initialize this carousel's memory
     * @param position The initial position of this carousel in percent of the application's window dimensions
     * @param dimensions The initial dimensions of this carousel in percent of the application's window dimensions
     * @param animationFilePath The animation for the background of this carousel
     * @param horizontalTextAlignment The horizontal alignment of the value text in this carousel
     * @param verticalTextAlignment The vertical alignment of the value text in this carousel
     * @param labelText The text to appear on this carousel's label
     * @param labelAlignment The alignment of this carousel's text label about its background
     * @param buttonAnimationFilePaths The unselected, selected, and pressed, animations of this carousel's back and
     *                                 next buttons
     * @param buttonWidth The width of this carousel's back and next buttons in percent of its background's width
     * @param horizontalButtonAlignment The horizontal alignment of this carousel's back and next buttons about its
     *                                  background
     * @param values The initial set of values to be represented by this carousel
     * @param valueIndex The initial index of the value to be represented by this carousel
     * @return Whether all parameters were valid and this carousel was initialized successfully
     */
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

    /**
     * Process user-input to this carousel's back and next buttons
     */
    @Override
    public void processInput() {
        if (!enabled) {
            return;
        }
        label.processInput();
        backButton.processInput();
        nextButton.processInput();
    }

    /**
     * Draw this carousel's back and next buttons, text label, and current value
     */
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

    /**
     * Update this carousel's animation and its back and next buttons' logic
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        label.update(deltaFrames);
        backButton.update(deltaFrames);
        nextButton.update(deltaFrames);
        sprite.update(deltaFrames);
    }

    /**
     * Free this carousel's memory
     */
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

    /**
     * Process user-interface events on this carousel's back and next buttons
     * @param groupID Any integer value
     * @param componentID The ID of the relevant button
     * @param event The event identifier
     */
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

    /**
     * Get the position of this carousel in percent of the application's window dimensions
     * @return This carousel's position in percent of the application's window dimensions
     */
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

    /**
     * Set the position of this carousel in percent of the application's window dimensions
     * @param position This carousel's new position in percent of the application's window dimensions
     * @return Whether the given position was valid and was set successfully
     */
    @Override
    public boolean setPosition(Vector position) {
        // Update position depending on button alignment
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
        // Update label position
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
     * Get this carousel's dimensions in percent of the application's window dimensions
     * @return This carousel's dimensions in percent of the application's window dimensions
     */
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

    /**
     * Set this carousel's dimensions in percent of the application's window dimensions
     * @param dimensions This carousel's new dimensions in percent of the application's window dimensions
     * @return Wheher the given dimensions were valid and were set successfully
     */
    @Override
    public boolean setDimensions(Vector dimensions) {
        // Set background dimensions depending on button alignment
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
        // Set label dimensions and update positioning
        if (!label.setDimensions(dimensions)) {
            return false;
        }
        if (getPosition() != null) {
            setPosition(getPosition().clone());
        }
        return true;
    }

    /**
     * Get the minimum dimensions of this carousel in pixels
     * @return This carousel's minimum dimensions in pixels
     */
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

    /**
     * Set the minimum dimensions of this carousel in pixels
     * @param minimumDimensions This carousel's new minimum dimensions in pixels
     * @return Whether the given dimensions were valid and were set successfully
     */
    @Override
    public boolean setMinimumDimensions(Vector minimumDimensions) {
        // Clear minimum dimensions if none are given
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
        // Set minimum dimensions by button alignment
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
        // Set label minimum dimensions
        if (!label.setMinimumDimensions(minimumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Get the maximum dimensions of this carousel in pixels
     * @return This carousel's maximum dimensions in pixels
     */
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

    /**
     * Set the maximum dimensions of this carousel in pixels
     * @param maximumDimensions This carousel's new maximum dimensions in pixels
     * @return Whether the given dimensions were valid and were set successfully
     */
    @Override
    public boolean setMaximumDimensions(Vector maximumDimensions) {
        // Clear maximum dimensions if none are given
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
        // Set maximum dimensions by button alignment
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
        // Set minimum label dimensions
        if (!label.setMaximumDimensions(maximumDimensions)) {
            return false;
        }
        return setPosition(getPosition().clone());
    }

    /**
     * Get this carousel's text label
     * @return This carousel's text label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Set the alignment of this carousel's text label about its background
     * @return This carousel's label alignment
     */
    public Alignment getLabelAlignment() {
        return labelAlignment;
    }

    /**
     * Set the alignment of this carousel's text label about its background
     * @param labelAlignment This carousel's new label alignment
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
        if (getPosition() == null) {
            return true;
        }
        if (!setPosition(getPosition().clone())) {
            return false;
        }
        return true;
    }

    /**
     * Get this carousel's back/previous value button
     * @return This carousel's back button
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Get this carousel's next/forward value button
     * @return This carousel's next button
     */
    public Button getNextButton() {
        return nextButton;
    }

    /**
     * Get the width of this carousel's back and next buttons in percent of its background's width
     * @return This carousel's button width in percent of its background's width
     */
    public float getButtonWidth() {
        return buttonWidth;
    }

    /**
     * Set the width of this carousel's back and next buttons in percent of its background's width
     * @param buttonWidth This carousel's new button width in percent of its background's width
     * @return Whether the given width was valid
     */
    public boolean setButtonWidth(float buttonWidth) {
        this.buttonWidth = buttonWidth;
        if (getDimensions() == null) {
            return true;
        }
        return setDimensions(getDimensions().clone());
    }

    /**
     * Get the minimum width of this carousel's back and next buttons in pixels
     * @return This carousel's minimum button width in pixels
     */
    public float getMinimumButtonWidth() {
        return minimumButtonWidth;
    }

    /**
     * Set the minimum width of this carousel's back and next buttons in pixels
     * @param minimumButtonWidth This carousel's new minimum button width in pixels
     * @return Whether the given width was valid
     */
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

    /**
     * Get the maximum width of this carousel's back and next buttons in pixels
     * @return This carousel's maximum button width in pixels
     */
    public float getMaximumButtonWidth() {
        return maximumButtonWidth;
    }

    /**
     * Set the maximum width of this carousel's back and next buttons in pixels
     * @param maximumButtonWidth This carousel's new maximum button width in pixels
     * @return Whether the given width was valid
     */
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

    /**
     * Get the horizontal alignment of this carousel's back and next buttons about its background
     * @return This carousel's horizontal button alignment
     */
    public Alignment getHorizontalButtonAlignment() {
        return horizontalButtonAlignment;
    }

    /**
     * Set the horizontal alignment of this carousel's back and next buttons about its background
     * @param horizontalButtonAlignment This carousel's new horizontal button alignment
     * @return Whether the given alignment was valid and was successfully set
     */
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

    /**
     * Get the current set of values which can be represented by this carousel
     * @return This carousel's values
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * Get a value which can be represented by this carousel by its index
     * @param valueIndex The index in this carousel's set of values to retrieve
     * @return The value at the given index or null if none was found
     */
    public String getValue(int valueIndex) {
        if (valueIndex < 0 || valueIndex >= values.size()) {
            return null;
        }
        return values.get(valueIndex);
    }

    /**
     * Get the current value represented by this carousel
     * @return This carousel's value
     */
    public String getValue() {
        return getValue(valueIndex);
    }

    /**
     * Add a value to be represented by this carousel at a given index in the pre-existing set of values
     * @param value The value to add
     * @param valueIndex The index to add the value at in the pre-existing set of values
     * @return Whether the value was added successfully
     */
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

    /**
     * Add a value to be represented by this carousel at the end of the current set of values
     * @param value The value to add
     * @return Whether the value was added successfully
     */
    public boolean addValue(String value) {
        return addValue(value, values.size());
    }

    /**
     * Remove a value from this carousel
     * @param value The value to remove
     * @return Whether the value was removed successfully
     */
    public boolean removeValue(String value) {
        if (!values.contains(value)) {
            return false;
        }
        return values.remove(value);
    }

    /**
     * Get the index of the value currently represented by this carousel
     * @return This carousel's value index
     */
    public int getValueIndex() {
        return valueIndex;
    }

    /**
     * Set the index of the value currently represented by this carousel
     * @param valueIndex This carousel's new value index
     * @return Whether the given index was value and was set successfully
     */
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

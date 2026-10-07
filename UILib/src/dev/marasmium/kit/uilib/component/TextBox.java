/**
 * File:        TextBox.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.02
 * Purpose:     Defines a text box user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.assets.Glyph;
import dev.marasmium.kit.applib.assets.Typeface;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.uilib.UIEvent;

/**
 * A text box user-interface component
 */
public class TextBox extends Label {

    /**
     * Whether this text box is currently selected to be typed in
     */
    protected boolean selected = false;
    /**
     * The animation to play when this text box is no longer selected
     */
    protected String unselectedAnimationFilePath = null;
    /**
     * The animation to play when this text box is selected
     */
    protected String selectedAnimationFilePath = null;
    /**
     * The text label to appear on this text box
     */
    protected final Label label = new Label();
    /**
     * The alignment of this text box's label about its background
     */
    protected Alignment labelAlignment = null;
    /**
     * The sprite representing this text box's cursor position
     */
    protected final Sprite cursor = new Sprite();
    /**
     * The vertical alignment of this text box's cursor within its background
     */
    protected Alignment verticalCursorAlignment = null;
    /**
     * The set of characters allowed to be typed in this text box
     */
    protected String allowedCharacters = null;
    /**
     * The maximum number of characters allowed to be typed in this text box
     */
    protected int maximumCharacters = 0;
    /**
     * The current index of this text box's cursor in its body text
     */
    protected int cursorIndex = 0;
    /**
     * The target number of repeated inputs per second when controls are held down
     */
    protected int targetRPS = 0;
    /**
     * The number of seconds to wait before starting to repeat inputs when controls are held down
     */
    protected float repeatStartTime = 0.0f;

    /**
     * The x-coordinate of the starting position of this text box's body text
     */
    private float textX = 0.0f;
    /**
     * Timer used for repeating inputs when controls are held down
     */
    private float repeatTimer = 0.0f;
    /**
     * Whether a control is currently held down and this text box is waiting to start repeating the input
     */
    private boolean repeatStarting = false;
    /**
     * Timer used for starting to repeat inputs when controls are held down
     */
    private float repeatStartTimer = 0.0f;

    /**
     * Initialize this text box's memory
     * @param position The initial position for this text box in percent of the application's window dimensions
     * @param dimensions The initial dimensions for this text box in percent of the application's window dimensions
     * @param animationFilePaths The unselected and selected animations for this text box
     * @param labelText The text to appear in this text box's label
     * @param labelAlignment The alignment of this text box's text label abouts its background
     * @param cursorDimensions The dimensions of this text box's cursor in percent of its background's dimensions
     * @param cursorAnimationFilePath The animation for this text box's cursor
     * @param verticalCursorAlignment The vertical alignment of this text box's cursor within its background
     * @param allowedCharacters The set of characters allowed to be typed in this text box
     * @param maximumCharacters The maximum number of characters allowed to be typed in this text box
     * @param targetRPS The target number of repeated inputs per second when this text box's controls are held down
     * @param repeatStartTime The time to wait before starting to repeat inputs when controls are held down in seconds
     * @return Whether all given parameters were valid and this text box was initialized successfully
     */
    public boolean initialize(Vector position, Vector dimensions, String[] animationFilePaths, String labelText,
                              Alignment labelAlignment, Vector cursorDimensions, String cursorAnimationFilePath,
                              Alignment verticalCursorAlignment, String allowedCharacters, int maximumCharacters,
                              int targetRPS, float repeatStartTime) {
        if (animationFilePaths == null) {
            return false;
        }
        if (animationFilePaths.length != 2) {
            return false;
        }
        this.verticalCursorAlignment = verticalCursorAlignment;
        if (!cursor.initialize(Vector.Zero(), 0.0f, Vector.Zero(), Angle.Zero(), cursorAnimationFilePath)) {
            return false;
        }
        cursor.playAnimation();
        if (!super.initialize(position, dimensions, "", "", Vector.Zero(), null, verticalCursorAlignment)) {
            return false;
        }
        sprite.setAnimationFilePath(animationFilePaths[0]);
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
        if (!setAllowedCharacters(allowedCharacters)) {
            return false;
        }
        if (!setMaximumCharacters(maximumCharacters)) {
            return false;
        }
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
        textX = sprite.getPosition().getX();
        if (!setCursorIndex(0)) {
            return false;
        }
        if (!setTargetRPS(targetRPS)) {
            return false;
        }
        if (!setRepeatStartTime(repeatStartTime)) {
            return false;
        }
        return true;
    }

    /**
     * Process user-input to this text box
     */
    @Override
    public void processInput() {
        if (!enabled) {
            return;
        }
        label.processInput();
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
            setSelected(App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite));
        }
        float repeatTime = (float)App.Graphics.getTargetFPS() / (float)targetRPS;
        float repeatStartTime = (float)App.Graphics.getTargetFPS() * this.repeatStartTime;
        if (selected) {
            // Text controls
            boolean textUpdated = false;
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Left)) {
                if (!repeatStarting) {
                    repeatStartTimer = 0.0f;
                    repeatStarting = true;
                }
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Left) || (repeatStartTimer > repeatStartTime
                        && repeatTimer > repeatTime)) {
                    repeatTimer = 0.0f;
                    if (cursorIndex > 0) {
                        setCursorIndex(cursorIndex - 1);
                        textUpdated = true;
                    }
                }
            }
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Right)) {
                if (!repeatStarting) {
                    repeatStartTimer = 0.0f;
                    repeatStarting = true;
                }
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Right) || (repeatStartTimer > repeatStartTime
                        && repeatTimer > repeatTime)) {
                    repeatTimer = 0.0f;
                    if (cursorIndex < text.length()) {
                        setCursorIndex(cursorIndex + 1);
                        textUpdated = true;
                    }
                }
            }
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Backspace)) {
                if (!repeatStarting) {
                    repeatStartTimer = 0.0f;
                    repeatStarting = true;
                }
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Backspace) || (repeatStartTimer > repeatStartTime
                        && repeatTimer > repeatTime)) {
                    repeatTimer = 0.0f;
                    if (cursorIndex > 0) {
                        setCursorIndex(cursorIndex - 1);
                        setText(text.substring(0, cursorIndex) + text.substring(cursorIndex + 1));
                        textUpdated = true;
                    }
                }
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.Enter)) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Text_Box_Value_Set);
                textUpdated = true;
            }
            if (App.Input.keyboard.isKeyReleased(KeyboardKey.Left)
                    || App.Input.keyboard.isKeyReleased(KeyboardKey.Right)
                    || App.Input.keyboard.isKeyReleased(KeyboardKey.Backspace)
                    || App.Input.keyboard.isKeyReleased(KeyboardKey.Enter)) {
                repeatStarting = false;
                repeatStartTimer = 0.0f;
            }
            if (!textUpdated) {
                String typedChars = App.Input.keyboard.getTypedChars();
                if (!typedChars.isEmpty() && !(typedChars.contains("\n") || typedChars.contains("\r")
                        || typedChars.contains("\t") || typedChars.contains("\b"))) {
                    for (char character : typedChars.toCharArray()) {
                        if (allowedCharacters != null) {
                            if (!allowedCharacters.contains(Character.toString(character))) {
                                continue;
                            }
                        }
                        if (text.length() >= maximumCharacters && maximumCharacters > 0) {
                            continue;
                        }
                        setText(text.substring(0, cursorIndex) + character + text.substring(cursorIndex));
                        setCursorIndex(cursorIndex + 1);
                    }
                    textUpdated = true;
                }
            }
            if (textUpdated) {
                while (true) {
                    float bufferSpace = sprite.getDimensions().getX() * 0.125f;
                    if (cursor.getPosition().getX() < sprite.getPosition().getX() + bufferSpace
                            && textX < sprite.getPosition().getX() + parent.getTextPadding()) {
                        textX += bufferSpace;
                        if (textX > sprite.getPosition().getX() + parent.getTextPadding()) {
                            textX = sprite.getPosition().getX() + parent.getTextPadding();
                        }
                        setCursorIndex(cursorIndex);
                    } else if (cursor.getPosition().getX() + cursor.getDimensions().getX() > sprite.getPosition().getX()
                            + sprite.getDimensions().getX() - bufferSpace) {
                        textX -= bufferSpace;
                        setCursorIndex(cursorIndex);
                    } else {
                        break;
                    }
                }
            }
            // Cursor position control
            if (App.Input.mouse.isButtonDown(MouseButton.Left) && !text.isEmpty()) {
                Typeface typeface = App.Assets.getTypeface(parent.getTypefaceFilePath());
                float cursorX = App.Input.mouse.getCursorPosition(parent.getCamera()).getX();
                float x = textX;
                for (int i = 0; i < text.length() + 1; i++) {
                    Glyph glyph;
                    float step = 0.0f;
                    if (i < text.length()) {
                        glyph = typeface.getGlyph(text.charAt(i));
                    } else {
                        glyph = typeface.getGlyph(text.charAt(text.length() - 1));
                    }
                    if (glyph == null) {
                        continue;
                    }
                    step = (glyph.getAdvances().getX() - glyph.getOffsets().getX()) * parent.getTextSize();
                    setCursorIndex(i);
                    if (x + (step / 2.0f) > cursorX) {
                        break;
                    }
                    x += step;
                }
            }
        }
    }

    /**
     * Draw graphics for this text box's cursor, label, background, and body text
     */
    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        label.draw();
        App.Graphics.submit(parent.getCamera(), sprite);
        App.Graphics.submit(parent.getCamera(), text, parent.getTypefaceFilePath(), sprite, parent.getTextPadding(),
                parent.getBaseDepth() + 0.1f, parent.getTextSize(), Vector.Cartesian(textX, 0.0f), null,
                verticalCursorAlignment);
        if (selected && sprite.contains(cursor)) {
            App.Graphics.submit(parent.getCamera(), cursor);
        }
    }

    /**
     * Update this text box's logic and animations
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
    @Override
    public void update(float deltaFrames) {
        // Update animations
        super.update(deltaFrames);
        label.update(deltaFrames);
        cursor.update(deltaFrames);
        repeatTimer += deltaFrames;
        // Update input repeat timers
        if (repeatTimer > ((float)App.Graphics.getTargetFPS() * (float)targetRPS)) {
            repeatTimer = 0.0f;
        }
        if (repeatStarting) {
            repeatStartTimer += deltaFrames;
            if (repeatStartTimer > ((float)App.Graphics.getTargetFPS() * repeatStartTimer * repeatStartTimer)) {
                repeatStartTimer = 0.0f;
            }
        }
        // Update cursor positioning
        float bufferSpace = sprite.getDimensions().getX() * 0.125f;
        if (cursor.getPosition().getX() < sprite.getPosition().getX() + bufferSpace
                && textX < sprite.getPosition().getX() + parent.getTextPadding()) {
            textX += (bufferSpace / 8.0f) * deltaFrames;
            if (textX > sprite.getPosition().getX() + parent.getTextPadding()) {
                textX = sprite.getPosition().getX() + parent.getTextPadding();
            }
            setCursorIndex(cursorIndex);
        } else if (cursor.getPosition().getX() + cursor.getDimensions().getX() > sprite.getPosition().getX()
                + sprite.getDimensions().getX() - bufferSpace) {
            textX -= (bufferSpace / 8.0f) * deltaFrames;
            setCursorIndex(cursorIndex);
        }
        setCursorIndex(cursorIndex);
    }

    /**
     * Free this text box's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        selected = false;
        unselectedAnimationFilePath = null;
        selectedAnimationFilePath = null;
        label.destroy();
        labelAlignment = null;
        cursor.destroy();
        verticalCursorAlignment = null;
        allowedCharacters = null;
        maximumCharacters = 0;
        cursorIndex = 0;
    }

    /**
     * Set this text box's position in percent of the application's window dimensions
     * @param position This text box's new position in percent of the application's window dimensions
     * @return Whether the given position was valid
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
        textX = sprite.getPosition().getX();
        if (!setCursorIndex(cursorIndex)) {
            return false;
        }
        if (!setVerticalCursorAlignment(verticalCursorAlignment)) {
            return false;
        }
        return true;
    }

    /**
     * Set this text box's dimensions in percent of the application's window dimensions
     * @param dimensions This text box's new dimensions in percent of the application's window dimensions
     * @return Whether the given dimensions were valid
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
        return setPosition(getPosition().clone());
    }

    /**
     * Set this text box's minimum dimensions in pixels
     * @param minimumDimensions This text box's new minimum dimensions in pixels
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
        return setPosition(getPosition().clone());
    }

    /**
     * Set this text box's maximum dimensions in pixels
     * @param maximumDimensions This text box's new maximum dimensions in pixels
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
        return setPosition(getPosition().clone());
    }

    /**
     * Set this text box's body text
     * @param text This text box's new body text
     * @return Whether the given test was valid
     */
    @Override
    public boolean setText(String text) {
        if (allowedCharacters != null) {
            for (char character : text.toCharArray()) {
                if (!allowedCharacters.contains(Character.toString(character))) {
                    return false;
                }
            }
        }
        if (maximumCharacters > -1) {
            if (text.length() > maximumCharacters) {
                return false;
            }
        }
        this.text = text;
        setCursorIndex(cursorIndex);
        return true;
    }

    /**
     * Test whether this text box is currently selected
     * @return Whether this text box is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set whether this text box is selected and play the corresponding animation
     * @param selected Whether this text box should be selected
     * @return Whether this text box's selected state was changed successfully
     */
    public boolean setSelected(boolean selected) {
        if (this.selected == selected) {
            return false;
        }
        if (selected) {
            parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Text_Box_Selected);
        } else {
            parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Text_Box_Unselected);
        }
        this.selected = selected;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(selected ? selectedAnimationFilePath : unselectedAnimationFilePath);
        sprite.playAnimation(1);
        return true;
    }

    /**
     * Get the animation to play when this text box is no longer selected
     * @return This text box's unselected animation
     */
    public String getUnselectedAnimationFilePath() {
        return unselectedAnimationFilePath;
    }

    /**
     * Set the animation to play when this text box is no longer selected
     * @param unselectedAnimationFilePath This text box's new unselected animation
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
     * Get the animation to play when this text box is selected
     * @return This text box's selected animation
     */
    public String getSelectedAnimationFilePath() {
        return selectedAnimationFilePath;
    }

    /**
     * Set the animation to play when this text box is selected
     * @param selectedAnimationFilePath This text box's new selected animation
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
     * Get this text box's label
     * @return This text box's label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Get the alignment of this text box's label about its background
     * @return This text box's label alignment
     */
    public Alignment getLabelAlignment() {
        return labelAlignment;
    }

    /**
     * Set the alignment of this text box's label about its background
     * @param labelAlignment This text box's new label alignment
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
     * Get the dimensions of this text box's cursor in percent of its background's dimensions
     * @return This text box's cursor dimensions in percent of its background's dimensions
     */
    public Vector getCursorDimensions() {
        return cursor.getDimensions().elementDivide(sprite.getDimensions());
    }

    /**
     * Set the dimensions of this text box's cursor in percent of its background's dimensions
     * @param cursorDimensions This text box's new cursor dimensions in percent of its background's dimensions
     * @return Whether the given dimensions were valid
     */
    public boolean setCursorDimensions(Vector cursorDimensions) {
        if (!cursor.setDimensions(cursorDimensions.elementMultiply(sprite.getDimensions()))) {
            return false;
        }
        if (!setCursorIndex(cursorIndex)) {
            return false;
        }
        return true;
    }

    /**
     * Get the vertical alignment of this text box's cursor within its background
     * @return This text box's vertical cursor alignment
     */
    public Alignment getVerticalCursorAlignment() {
        return verticalCursorAlignment;
    }

    /**
     * Set the vertical alignment of this text box's cursor within its background
     * @param verticalCursorAlignment This text box's new vertical cursor alignment
     * @return Whether the given alignment was valid
     */
    public boolean setVerticalCursorAlignment(Alignment verticalCursorAlignment) {
        if (verticalCursorAlignment == null) {
            return false;
        }
        this.verticalCursorAlignment = verticalCursorAlignment;
        if (verticalCursorAlignment == Alignment.Bottom) {
            cursor.getPosition().setY(sprite.getPosition().getY());
        } else if (verticalCursorAlignment == Alignment.Top) {
            cursor.getPosition().setY(sprite.getPosition().getY() + sprite.getDimensions().getY()
                    - cursor.getDimensions().getY());
        } else if (verticalCursorAlignment == Alignment.Center) {
            cursor.getPosition().setY(sprite.getPosition().getY() + (sprite.getDimensions().getY() * 0.5f)
                    - (cursor.getDimensions().getY() * 0.5f));
        } else {
            return false;
        }
        return true;
    }

    /**
     * Get the set of characters allowed to be typed into this text box
     * @return This text box's allowed characters
     */
    public String getAllowedCharacters() {
        return allowedCharacters;
    }

    /**
     * Set the set of characters allowed to be typed into this text box
     * @param allowedCharacters This text box's new allowed characters
     * @return Whether this text box's allowed characters were set successfully
     */
    public boolean setAllowedCharacters(String allowedCharacters) {
        this.allowedCharacters = allowedCharacters;
        if (text == null) {
            return true;
        }
        StringBuilder updatedText = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (allowedCharacters.contains(Character.toString(character))) {
                updatedText.append(character);
            }
        }
        return setText(updatedText.toString());
    }

    /**
     * Get the maximum number of characters allowed to be typed into this text box
     * @return This text box's maximum character count
     */
    public int getMaximumCharacters() {
        return maximumCharacters;
    }

    /**
     * Set the maximum number of characters allowed to be typed into this text box
     * @param maximumCharacters This text box's new maximum character count
     * @return Whether this text box's maximum character count was set successfully
     */
    public boolean setMaximumCharacters(int maximumCharacters) {
        if (maximumCharacters < -1) {
            return false;
        }
        this.maximumCharacters = maximumCharacters;
        if (text == null) {
            return true;
        }
        if (maximumCharacters > -1 && text.length() > maximumCharacters) {
            setText(text.substring(0, maximumCharacters));
        }
        return true;
    }

    /**
     * Get the current index of this text box's cursor in its body text
     * @return This text box's current cursor index
     */
    public int getCursorIndex() {
        return cursorIndex;
    }

    /**
     * Set the index of this text box's cursor in its body text and set the cursor's position
     * @param cursorIndex This text box's new cursor index
     * @return Whether the given cursor index was valid and was set successfully
     */
    public boolean setCursorIndex(int cursorIndex) {
        if (cursorIndex < 0 || cursorIndex > text.length()) {
            cursorIndex = 0;
        }
        this.cursorIndex = cursorIndex;
        float cursorX = textX;
        if (parent == null) {
            cursor.getPosition().setX(cursorX);
            return true;
        }
        float textWidth = 0.0f;
        Typeface typeface = App.Assets.getTypeface(parent.getTypefaceFilePath());
        for (int i = 0; i < text.length(); i++) {
            Glyph glyph = typeface.getGlyph(text.charAt(i));
            if (glyph == null) {
                continue;
            }
            float step = (glyph.getAdvances().getX() - glyph.getOffsets().getX()) * parent.getTextSize();
            if (i < cursorIndex) {
                cursorX += step;
            }
            textWidth += step;
        }
        cursor.getPosition().setX(cursorX);
        cursor.setDepth(parent.getBaseDepth() + 0.2f);
        return true;
    }

    /**
     * Get the target number of repeated inputs per second when this text box's controls are held down
     * @return This text box's target repeats per second
     */
    public int getTargetRPS() {
        return targetRPS;
    }

    /**
     * Set the target number of repeated inputs per second when this text box's controls are held down
     * @param targetRPS This text box's new target repeats per second
     * @return Whether the given target repeats per second was valid
     */
    public boolean setTargetRPS(int targetRPS) {
        if (targetRPS < 0) {
            return false;
        }
        this.targetRPS = targetRPS;
        return true;
    }

    /**
     * Get the time to wait to start repeating inputs when this text box's controls are held down in seconds
     * @return This text box's repeat start time in seconds
     */
    public float getRepeatStartTime() {
        return repeatStartTime;
    }

    /**
     * Set the time to wait to start repeating inputs when this text box's controls are held down in seconds
     * @param repeatStartTime This text box's repeat start time in seconds
     * @return Whether the given repeat start time was valid
     */
    public boolean setRepeatStartTime(float repeatStartTime) {
        if (repeatStartTime < 0.0f) {
            return false;
        }
        this.repeatStartTime = repeatStartTime;
        return true;
    }

}

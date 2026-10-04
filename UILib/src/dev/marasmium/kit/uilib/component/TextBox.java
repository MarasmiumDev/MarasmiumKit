/**
 * File:        TextBox.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.02
 * Purpose:     Defines a text-box user-interface component
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

public class TextBox extends Label {

    protected boolean typing = false;
    protected String unselectedAnimationFilePath = null;
    protected String selectedAnimationFilePath = null;
    protected final Label label = new Label();
    protected Alignment labelAlignment = null;
    protected final Sprite cursor = new Sprite();
    protected Alignment verticalCursorAlignment = null;
    protected String allowedCharacters = null;
    protected int maximumCharacters = 0;
    protected int cursorIndex = 0;

    private float textX = 0.0f;
    private float repeatTimer = 0.0f;

    public boolean initialize(Vector position, Vector dimensions, String[] animationFilePaths, String labelText,
                              Alignment labelAlignment, Vector cursorDimensions, String cursorAnimationFilePath,
                              Alignment verticalCursorAlignment, String allowedCharacters, int maximumCharacters) {
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
        return true;
    }

    @Override
    public void processInput() {
        label.processInput();
        if (App.Input.mouse.isButtonPressed(MouseButton.Left)) {
            setTyping(App.Input.mouse.getCursorPosition(parent.getCamera()).inside(sprite));
        }
        float repeatTime = App.Graphics.getTargetFPS() / 5.0f;
        if (typing) {
            // Text controls
            boolean textUpdated = false;
            if (App.Input.keyboard.isKeyDown(KeyboardKey.Left)) {
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Left) || repeatTimer > repeatTime) {
                    repeatTimer = 0.0f;
                    if (cursorIndex > 0) {
                        setCursorIndex(cursorIndex - 1);
                        textUpdated = true;
                    }
                }
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.Right)) {
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Right) || repeatTimer > repeatTime) {
                    repeatTimer = 0.0f;
                    if (cursorIndex < text.length()) {
                        setCursorIndex(cursorIndex + 1);
                        textUpdated = true;
                    }
                }
            } else if (App.Input.keyboard.isKeyDown(KeyboardKey.Backspace)) {
                if (App.Input.keyboard.isKeyPressed(KeyboardKey.Backspace) || repeatTimer > repeatTime) {
                    repeatTimer = 0.0f;
                    if (cursorIndex > 0) {
                        setCursorIndex(cursorIndex - 1);
                        setText(text.substring(0, cursorIndex) + text.substring(cursorIndex + 1));
                        textUpdated = true;
                    }
                }
            } else if (App.Input.keyboard.isKeyPressed(KeyboardKey.Enter)) {
                parent.componentEvent(parent.getGroupID(), componentID, UIEvent.Text_Box_Value_Set);
            } else {
                String typedChars = App.Input.keyboard.getTypedChars();
                if (!typedChars.isEmpty() && !(typedChars.contains("\n") || typedChars.contains("\r")
                        || typedChars.contains("\t") || typedChars.contains("\b"))) {
                    setText(text.substring(0, cursorIndex) + typedChars + text.substring(cursorIndex));
                    setCursorIndex(cursorIndex + typedChars.length());
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

    @Override
    public void draw() {
        label.draw();
        App.Graphics.submit(parent.getCamera(), sprite);
        App.Graphics.submit(parent.getCamera(), text, parent.getTypefaceFilePath(), sprite, parent.getTextPadding(),
                parent.getBaseDepth() + 0.1f, parent.getTextSize(), Vector.Cartesian(textX, 0.0f), null,
                verticalCursorAlignment);
        if (typing && sprite.contains(cursor)) {
            App.Graphics.submit(parent.getCamera(), cursor);
        }
    }

    @Override
    public void update(float deltaFrames) {
        super.update(deltaFrames);
        label.update(deltaFrames);
        cursor.update(deltaFrames);
        repeatTimer += deltaFrames;
        if (repeatTimer > App.Graphics.getTargetFPS()) {
            repeatTimer = 0.0f;
        }
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

    @Override
    public void destroy() {
        super.destroy();
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
        textX = sprite.getPosition().getX();
        if (!setCursorIndex(cursorIndex)) {
            success = false;
        }
        if (!setVerticalCursorAlignment(verticalCursorAlignment)) {
            success = false;
        }
        return success;
    }

    @Override
    public boolean setDimensions(Vector dimensions) {
        boolean success = true;
        Vector cursorDimensions = getCursorDimensions();
        if (!super.setDimensions(dimensions)) {
            success = false;
        }
        if (!label.setDimensions(dimensions)) {
            success = false;
        }
        if (!setCursorDimensions(cursorDimensions)) {
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

    public boolean isTyping() {
        return typing;
    }

    public boolean setTyping(boolean typing) {
        if (this.typing == typing) {
            return false;
        }
        this.typing = typing;
        sprite.stopAnimation();
        sprite.setAnimationFilePath(typing ? selectedAnimationFilePath : unselectedAnimationFilePath);
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

    public Vector getCursorDimensions() {
        return cursor.getDimensions().elementDivide(sprite.getDimensions());
    }

    public boolean setCursorDimensions(Vector cursorDimensions) {
        boolean success = cursor.setDimensions(cursorDimensions.elementMultiply(sprite.getDimensions()));
        setCursorIndex(cursorIndex);
        return success;
    }

    public Alignment getVerticalCursorAlignment() {
        return verticalCursorAlignment;
    }

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

    public String getAllowedCharacters() {
        return allowedCharacters;
    }

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

    public int getMaximumCharacters() {
        return maximumCharacters;
    }

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

    public int getCursorIndex() {
        return cursorIndex;
    }

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

}

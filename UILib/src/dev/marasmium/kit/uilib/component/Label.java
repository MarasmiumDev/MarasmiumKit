/**
 * File:        Label.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.28
 * Purpose:     Defines a label user-interface component
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;

public class Label extends UIComponent {

    private String text = null;
    private Alignment horizontalTextAlignment = null;
    private Alignment verticalTextAlignment = null;

    public boolean initialize(Vector position, Vector dimensions, String animationFilePath, String text,
                              Alignment horizontalAlignment, Alignment verticalAlignment) {
        setEnabled(true);
        setVisible(true);
        if (!sprite.initialize(Vector.Zero(), 0.0f, Vector.Zero(), Angle.Zero(), animationFilePath)) {
            return false;
        }
        sprite.stopAnimation();
        if (!setText(text)) {
            return false;
        }
        if (!setHorizontalTextAlignment(horizontalAlignment)) {
            return false;
        }
        if (!setVerticalTextAlignment(verticalAlignment)) {
            return false;
        }
        if (!setPosition(position)) {
            return false;
        }
        if (!setDimensions(dimensions)) {
            return false;
        }
        return true;
    }

    @Override
    public void processInput() {}

    @Override
    public void draw() {
        if (!visible) {
            return;
        }
        App.Graphics.submit(parent.getCamera(), sprite);
        App.Graphics.submit(parent.getCamera(), text, parent.getTypefaceFilePath(), sprite, parent.getTextPadding(),
                parent.getBaseDepth() + 0.1f, parent.getTextSize(), horizontalTextAlignment, verticalTextAlignment);
    }

    @Override
    public void update(float deltaFrames) {
        sprite.update(deltaFrames);
    }

    @Override
    public void destroy() {
        super.destroy();
        text = null;
        horizontalTextAlignment = null;
        verticalTextAlignment = null;
    }

    public String getText() {
        return text;
    }

    public boolean setText(String text) {
        if (text == null) {
            return false;
        }
        this.text = text;
        return true;
    }

    public Alignment getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }

    public boolean setHorizontalTextAlignment(Alignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            return false;
        }
        this.horizontalTextAlignment = horizontalAlignment;
        return true;
    }

    public Alignment getVerticalTextAlignment() {
        return verticalTextAlignment;
    }

    public boolean setVerticalTextAlignment(Alignment verticalAlignment) {
        if (verticalAlignment == null) {
            return false;
        }
        this.verticalTextAlignment = verticalAlignment;
        return true;
    }

}

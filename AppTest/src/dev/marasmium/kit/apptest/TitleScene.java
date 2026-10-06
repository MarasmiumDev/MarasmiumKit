/**
 * File:        TitleScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     The initial/opening scene of the MarasmiumKit application framework's AppTest program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Carousel;
import dev.marasmium.kit.uilib.component.Label;
import dev.marasmium.kit.uilib.component.Slider;
import dev.marasmium.kit.uilib.component.Switch;
import dev.marasmium.kit.uilib.component.TextBox;

public class TitleScene extends Scene implements UIListener {

    private final LogSource logSource = new LogSource("Title Scene");
    private UIGroup UI = null;
    private Label label1 = null;
    private Button button1 = null;
    private Switch switch1 = null;
    private Slider slider1 = null;
    private TextBox textBox1 = null;
    private Carousel carousel1 = null;

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        UI = new UIGroup();
        UI.initialize(this, 0, 0.0f, "Typefaces/Fira_Sans.typeface", 4.0f, 0.25f);
        label1 = new Label();
        label1.initialize(Vector.Cartesian(0.01f, 0.82f), Vector.Cartesian(0.48f, 0.125f),
                "Animations/UI/Label/Label.animation", "Label", Vector.Zero(), Alignment.Center, Alignment.Center);
        UI.addComponent(label1);
        button1 = new Button();
        button1.initialize(Vector.Cartesian(0.01f, 0.56f), Vector.Cartesian(0.23f, 0.125f),
                new String[] { "Animations/UI/Button/Button_Unselected.animation",
                        "Animations/UI/Button/Button_Selected.animation",
                        "Animations/UI/Button/Button_Pressed.animation" }, "Button", Alignment.Center);
        UI.addComponent(button1);
        switch1 = new Switch();
        switch1.initialize(Vector.Cartesian(0.26f, 0.56f), Vector.Cartesian(0.23f, 0.125f),
                new String[] { "Animations/UI/Switch/Switch_Off.animation",
                        "Animations/UI/Switch/Switch_On.animation" }, "Switch", Alignment.Top);
        UI.addComponent(switch1);
        slider1 = new Slider();
        if (!slider1.initialize(Vector.Cartesian(0.01f, 0.3f), Vector.Cartesian(0.48f, 0.125f),
                new String[] { "Animations/UI/Slider/Slider_Unselected.animation",
                        "Animations/UI/Slider/Slider_Selected.animation" }, "Slider", Alignment.Top,
                Vector.Cartesian(0.01f, 0.9f), new String[] { "Animations/UI/Slider/Slider_Cursor_Unselected.animation",
                        "Animations/UI/Slider/Slider_Cursor_Selected.animation",
                        "Animations/UI/Slider/Slider_Cursor_Pressed.animation" }, Alignment.Center, -10.0f, 10.0f,
                20)) {
            return false;
        }
        UI.addComponent(slider1);
        textBox1 = new TextBox();
        if (!textBox1.initialize(Vector.Cartesian(0.01f, 0.05f), Vector.Cartesian(0.48f, 0.125f),
                new String[] { "Animations/UI/Text_Box/Text_Box_Unselected.animation",
                        "Animations/UI/Text_Box/Text_Box_Selected.animation" }, "Text Box", Alignment.Top,
                Vector.Cartesian(0.01f, 0.7f), "Animations/UI/Text_Box/Text_Box_Cursor.animation", Alignment.Center,
                null, -1, 12, 0.5f)) {
            return false;
        }
        UI.addComponent(textBox1);
        carousel1 = new Carousel();
        if (!carousel1.initialize(Vector.Cartesian(0.51f, 0.82f), Vector.Cartesian(0.48f, 0.125f),
                "Animations/UI/Carousel/Carousel.animation", Alignment.Center, Alignment.Center, "Carousel",
                Alignment.Top, new String[] { "Animations/UI/Carousel/Carousel_Button_Unselected.animation",
                        "Animations/UI/Carousel/Carousel_Button_Selected.animation",
                        "Animations/UI/Carousel/Carousel_Button_Pressed.animation" }, 0.15f, Alignment.Right,
                new String[] { "Value 1", "Value 2", "Value 3" }, 0)) {
            return false;
        }
        UI.addComponent(carousel1);
        return true;
    }

    @Override
    public boolean processInput() {
        UI.processInput();
        if (App.Input.keyboard.isKeyDown(KeyboardKey.Tab)) {
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.F)) {
                App.Window.setFullscreen(!App.Window.isFullscreen());
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.E)) {
                UI.setEnabled(!UI.isEnabled());
            }
            if (App.Input.keyboard.isKeyPressed(KeyboardKey.V)) {
                UI.setVisible(!UI.isVisible());
            }
        }
        return true;
    }

    @Override
    public void draw() {
        UI.draw();
    }

    @Override
    public void update(float deltaFrames) {
        UI.update(deltaFrames);
    }

    @Override
    public boolean leave(Scene nextScene) {
        UI.destroy();
        return true;
    }

    @Override
    public boolean destroy() {
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        App.Log.write(logSource, LogLevel.Info, "Component ", componentID, " in group ", groupID, " event ", event);
    }

}

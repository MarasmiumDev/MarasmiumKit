/**
 * File:        TitleScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     The initial/opening scene of the MarasmiumKit application framework's AppTest program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Label;
import dev.marasmium.kit.uilib.component.Slider;
import dev.marasmium.kit.uilib.component.Switch;

public class TitleScene extends Scene implements UIListener {

    private final LogSource logSource = new LogSource("Title Scene");
    private UIGroup UI = null;
    private Label label1 = null;
    private Button button1 = null;
    private Switch switch1 = null;
    private Slider slider1 = null;

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        UI = new UIGroup();
        UI.initialize(this, 0, 0.0f, "Typeface/Fira_Sans.typeface", 4.0f, 0.25f);
        label1 = new Label();
        label1.initialize(Vector.Cartesian(0.01f, 0.78f), Vector.Cartesian(0.48f, 0.125f),
                "Animation/UI/Label.animation", "Label", Alignment.Center, Alignment.Center);
        UI.addComponent(label1);
        button1 = new Button();
        button1.initialize(Vector.Cartesian(0.01f, 0.52f), Vector.Cartesian(0.23f, 0.125f),
                new String[] { "Animation/UI/Button_Unselected.animation", "Animation/UI/Button_Selected.animation",
                "Animation/UI/Button_Pressed.animation" }, "Button", Alignment.Center);
        UI.addComponent(button1);
        switch1 = new Switch();
        switch1.initialize(Vector.Cartesian(0.26f, 0.52f), Vector.Cartesian(0.23f, 0.125f),
                new String[] { "Animation/UI/Switch_Off.animation", "Animation/UI/Switch_On.animation" }, "Switch",
                Alignment.Top);
        UI.addComponent(switch1);
        slider1 = new Slider();
        if (!slider1.initialize(Vector.Cartesian(0.01f, 0.26f), Vector.Cartesian(0.48f, 0.125f),
                new String[] { "Animation/UI/Slider_Unselected.animation", "Animation/UI/Slider_Selected.animation" },
                "Slider", Alignment.Top, new String[] { "Animation/UI/Cursor_Unselected.animation",
                        "Animation/UI/Cursor_Selected.animation", "Animation/UI/Cursor_Pressed.animation" },
                Vector.Cartesian(0.5f, 0.9f), Alignment.Center, -10.0f, 10.0f, 5)) {
            return false;
        }
        UI.addComponent(slider1);
        return true;
    }

    @Override
    public boolean processInput() {
        UI.processInput();
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.F)) {
            App.Window.setFullscreen(!App.Window.isFullscreen());
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

/**
 * File:        ConnectScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.13
 * Purpose:     Defines the information scene for connecting to the MarasmiumKit application framework's test server
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.Scene;

public class ConnectScene extends Scene {

    @Override
    protected boolean initialize() {
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        return true;
    }

    @Override
    public boolean processInput() {
        return true;
    }

    @Override
    public void draw() {

    }

    @Override
    public void update(float deltaFrames) {

    }

    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    @Override
    protected boolean destroy() {
        return true;
    }
}

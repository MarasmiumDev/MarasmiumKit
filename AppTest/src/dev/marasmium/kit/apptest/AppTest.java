/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfig;
import dev.marasmium.kit.applib.data.Colour;

public class AppTest {

    public static final TestScene1 Test_Scene_1 = new TestScene1();
    public static final TestScene2 Test_Scene_2 = new TestScene2();

    static void main() {
        // Set up app
        AppConfig config = new AppConfig(Test_Scene_1);
        if (!config.applyDefaults()) {
            System.out.println("Failed to apply default settings");
            return;
        }
        if (App.Initialize(config)) {
            App.Run();
        } else {
            System.out.println("Failed to initialize app!");
        }
        if (!App.Destroy()) {
            System.out.println("Failed to destroy app!");
        }
    }

}

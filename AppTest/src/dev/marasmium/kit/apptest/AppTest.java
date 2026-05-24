/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfig;

public class AppTest {

    public static TestScene1 Test_Scene_1 = new TestScene1();

    static void main() {
        AppConfig config = new AppConfig(Test_Scene_1);
        if (!App.Initialize(config)) {
            System.out.println("Failed to initialize app!");
            return;
        }
        App.Run();
        if (!App.Destroy()) {
            System.out.println("Failed to destroy app!");
        }
    }

}

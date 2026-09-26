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

    public static final TitleScene Title_Scene = new TitleScene();

    static void main() {
        AppConfig config = new AppConfig(Title_Scene);
        if (!config.applyDefaults()) {
            System.out.println("Failed to configure application");
            return;
        }
        if (App.Initialize(config)) {
            System.out.println("Initialized application");
            App.Run();
        } else {
            System.out.println("Failed to initialize application");
        }
        if (App.Destroy()) {
            System.out.println("Destroyed application");
        } else {
            System.out.println("Failed to destroy application");
        }
    }

}

/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.apptest.scenes.ConnectScene;
import dev.marasmium.kit.apptest.scenes.OptionsScene;
import dev.marasmium.kit.apptest.scenes.TitleScene;

public class AppTest {

    public static final String Configuration_File_Path = "MarasmiumKit-App.configuration";
    public static final TitleScene Title_Scene = new TitleScene();
    public static final ConnectScene Connect_Scene = new ConnectScene();
    public static final OptionsScene Options_Scene = new OptionsScene();

    static void main() {
        if (App.Initialize(Configuration_File_Path, Title_Scene)) {
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

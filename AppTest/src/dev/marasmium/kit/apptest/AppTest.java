/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.apptest.scenes.ConnectScene;
import dev.marasmium.kit.apptest.scenes.GameScene;
import dev.marasmium.kit.apptest.scenes.OptionsScene;
import dev.marasmium.kit.apptest.scenes.TitleScene;

/**
 * The main class of the MarasmiumKit application's framework app test program
 */
public class AppTest {

    /**
     * The file path of the application framework's configuration file
     */
    public static final String Configuration_File_Path = "MarasmiumKit-App.configuration";
    /**
     * The app test's title scene
     */
    public static final TitleScene Title_Scene = new TitleScene();
    /**
     * The app test's connection scene
     */
    public static final ConnectScene Connect_Scene = new ConnectScene();
    /**
     * The app test's options scene
     */
    public static final OptionsScene Options_Scene = new OptionsScene();
    /**
     * The app test's game scene
     */
    public static final GameScene Game_Scene = new GameScene();

    /**
     * The entry point of the app test program
     */
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

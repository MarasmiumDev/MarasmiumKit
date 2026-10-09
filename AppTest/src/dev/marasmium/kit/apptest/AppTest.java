/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.audio.AudioDevice;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.windowing.Monitor;

public class AppTest {

    public static final TitleScene Title_Scene = new TitleScene();

    static void main() {
        if (App.Initialize("yyyy.MM.dd@HH:mm:ss.SSS", true, true, "MarasmiumKit-AppTest.log", "MarasmiumKit App",
                Vector.Cartesian(1280.0f, 720.0f), new Monitor(0), false, true, -1, new AudioDevice(0), 1.0f, 1.0f, 60,
                10, Colour.Black, Title_Scene)) {
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

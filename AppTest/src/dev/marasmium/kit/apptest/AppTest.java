/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfig;
import dev.marasmium.kit.applib.data.Vec2D;

import java.util.Scanner;

public class AppTest {

    public static final TestScene1 Test_Scene_1 = new TestScene1();
    public static final TestScene2 Test_Scene_2 = new TestScene2();

    static void main() {
        // Get log file index and host address for networking test
        Scanner scanner = new Scanner(System.in);
        System.out.print("Log index: ");
        String index = scanner.nextLine();
        System.out.print("Host: ");
        String address = scanner.nextLine();
        String[] split = address.split(":");
        Test_Scene_1.hostName = split[0];
        Test_Scene_1.port = Integer.parseInt(split[1]);
        // Set up app
        AppConfig config = new AppConfig(Test_Scene_1);
        config.window.dimensions = Vec2D.Cartesian(100.0d, 100.0d);
        config.log.fileOutputPath = "MarasmiumKit-Client-" + index + ".log";
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

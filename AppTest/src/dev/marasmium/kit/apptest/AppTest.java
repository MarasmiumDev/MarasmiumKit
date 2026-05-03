/**
 * File:        AppTest.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's application framework test program
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfig;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

public class AppTest {

    private static final LogSource LogSource = new LogSource("AppTest");

    static void main() {
        AppConfig config = new AppConfig();
        if (!App.Initialize(config)) {
            System.out.println("Failed to initialize app!");
            return;
        }
        App.Log.write(LogSource, LogLevel.Info, "Test writing message");
        App.Log.write(LogSource, "Test without level");
        App.Log.write(LogLevel.Warning, "Test without source");
        App.Log.write("Test without level or source");
        if (App.Log.setTimestampFormat("yyyy-MM-dd HH:mm:ss")) {
            App.Log.write(LogSource, LogLevel.Info, "Set valid timestamp format");
        } else {
            App.Log.write(LogSource, LogLevel.Error, "Failed to set valid timestamp format");
            return;
        }
        if (!App.Log.setTimestampFormat("x")) {
            App.Log.write(LogSource, LogLevel.Info, "Failed to set invalid timestamp format");
        } else {
            App.Log.write(LogSource, LogLevel.Error, "Set invalid timestamp format");
        }
        App.Log.write(LogSource, LogLevel.Info, "Disabling console output");
        App.Log.setConsoleOutputEnabled(false);
        App.Log.write(LogSource, LogLevel.Info, "Enabling console output");
        App.Log.setConsoleOutputEnabled(true);
        App.Log.write(LogSource, LogLevel.Info, "Disabling file output");
        App.Log.setFileOutputEnabled(false);
        App.Log.write(LogSource, LogLevel.Info, "Enabling file output");
        App.Log.setFileOutputEnabled(true);
        if (App.Log.setFileOutputPath("Test.log", true)) {
            App.Log.write(LogSource, LogLevel.Info, "Set valid output file path");
        } else {
            App.Log.write(LogSource, LogLevel.Error, "Failed to set valid output file path");
        }
        if (!App.Log.setFileOutputPath("/", false)) {
            App.Log.write(LogSource, LogLevel.Info, "Failed to set invalid output file path");
        } else {
            App.Log.write(LogSource, LogLevel.Error, "Set invalid output file path");
        }
        if (!App.Destroy()) {
            System.out.println("Failed to destroy app!");
        }
    }

}

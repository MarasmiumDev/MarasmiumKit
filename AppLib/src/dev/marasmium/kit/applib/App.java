/**
 * File:        App.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines the main class of the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogManager;
import dev.marasmium.kit.applib.logging.LogSource;

/**
 * The main, singleton class of the MarasmiumKit's application framework
 */
public class App {

    /**
     * The application framework's logging system
     */
    public static final LogManager Log = new LogManager();

    /**
     * Whether the application framework has been initialized
     */
    private static boolean Initialized = false;

    /**
     * Initialize the MarasmiumKit's application framework
     * @param config Configuration/settings structure for the application framework's systems
     * @return Whether the application framework was successfully initialized
     */
    public static boolean Initialize(AppConfig config) {
        if (Initialized) {
            Log.write(LogSource.App, LogLevel.Warning, "Application framework cannot be initialized twice");
            return false;
        }
        if (config == null) {
            return false;
        }
        // Initialize the logging system
        if (!Log.initialize(config.log)) {
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized logging system");
        Initialized = true;
        Log.write(LogSource.App, LogLevel.Info, "Initialized MarasmiumKit application framework");
        return true;
    }

    /**
     * Free/de-initialize the application framework's memory
     * @return Whether the application framework was successfully/cleanly destroyed
     */
    public static boolean Destroy() {
        if (!Initialized) {
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying MarasmiumKit application framework");
        boolean success = true;
        // Free the logging system
        Log.write(LogSource.App, LogLevel.Info, "Destroying logging system");
        if (!Log.destroy()) {
            success = false;
        }
        Initialized = false;
        return success;
    }

    /**
     * Test whether the MarasmiumKit's application framework has been initialized
     * @return Whether the application framework has been initialized
     */
    public static boolean IsInitialized() {
        return Initialized;
    }

    /**
     * Disabled constructor for singleton App class
     */
    private App() {}

}

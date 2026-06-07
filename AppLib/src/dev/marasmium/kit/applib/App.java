/**
 * File:        App.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines the main class of the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.graphics.GraphicsManager;
import dev.marasmium.kit.applib.input.InputManager;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogManager;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.windowing.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * The main, singleton class of the MarasmiumKit's application framework
 */
public class App {

    /**
     * The application framework's logging system
     */
    public static final LogManager Log = new LogManager();
    /**
     * The application framework's windowing system
     */
    public static final WindowManager Window = new WindowManager();
    /**
     * The application framework's user-input management system
     */
    public static final InputManager Input = new InputManager();
    /**
     * The application framework's graphics system
     */
    public static final GraphicsManager Graphics = new GraphicsManager();

    /**
     * Whether the application framework has been initialized
     */
    private static boolean Initialized = false;
    /**
     * The scenes managed by the application framework
     */
    private static List<Scene> Scenes = null;
    /**
     * The next ID to assign to a scene managed by the application framework
     */
    private static int NextSceneID = 0;
    /**
     * The application framework's current scene
     */
    private static Scene CurrentScene = null;

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
        // Initialize the windowing system
        if (!Window.initialize(config.window)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize windowing system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized windowing system");
        // Initialize the user-input management system
        if (!Input.initialize()) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize user-input management system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized user-input management system");
        // Initialize the graphics system
        if (!Graphics.initialize(config.graphics)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize graphics system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized graphics system");
        // Set initial scene
        Scenes = new ArrayList<>();
        if (!SetCurrentScene(config.initialScene)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to set current scene");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Set initial scene");
        Initialized = true;
        Log.write(LogSource.App, LogLevel.Info, "Initialized MarasmiumKit application framework");
        return true;
    }

    /**
     * Run the main loop of the MarasmiumKit application framework
     */
    public static void Run() {
        Log.write(LogSource.App, LogLevel.Info, "Starting main application loop");
        long deltaStartMS = System.currentTimeMillis();
        long deltaElapsedMS;
        double deltaFrames;
        long waitStartMS;
        long waitElapsedMS;
        long waitMS;
        int count = 0;
        while (!Window.isCloseRequested()) {
            waitStartMS = System.currentTimeMillis();
            if (!CurrentScene.processInput()) {
                break;
            }
            Input.update();
            deltaElapsedMS = System.currentTimeMillis() - deltaStartMS;
            deltaStartMS = System.currentTimeMillis();
            deltaFrames = deltaElapsedMS * Graphics.getTargetFPMS();
            while (count++ < Graphics.getMaxUPF() && deltaFrames > 1.0d) {
                CurrentScene.update(1.0d);
                deltaFrames -= 1.0d;
            }
            CurrentScene.update(deltaFrames);
            count = 0;
            waitElapsedMS = System.currentTimeMillis() - waitStartMS;
            waitMS = (long)Graphics.getTargetMSPF() - waitElapsedMS;
            try {
                Thread.sleep(waitMS);
            } catch (Exception _) {}
        }
        Log.write(LogSource.App, LogLevel.Info, "Finished main application loop");
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
        // Dispose of all scenes managed by the application
        Log.write(LogSource.App, LogLevel.Info, "Freeing all scenes");
        NextSceneID = 0;
        CurrentScene.leave(null);
        CurrentScene = null;
        for (Scene scene : Scenes) {
            scene.destroy();
        }
        Scenes.clear();
        Scenes = null;
        // Free the graphics system
        Log.write(LogSource.App, LogLevel.Info, "Destroying graphics system");
        if (!Graphics.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy graphics system");
            success = false;
        }
        // Free the user-input management system
        Log.write(LogSource.App, LogLevel.Info, "Destroying user-input management system");
        if (!Input.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy user-input management system");
            success = false;
        }
        // Free the windowing system
        Log.write(LogSource.App, LogLevel.Info, "Destroying windowing system");
        if (!Window.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy windowing system");
            success = false;
        }
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
     * Add a scene to the application framework to be managed by it - the framework will be responsible for initializing
     * and destroying the scene after it is added
     * @param scene The scene to add to the application framework - if not initialized, this will initialize it
     * @return Whether the scene was not already present and was initialized successfully
     */
    public static boolean AddScene(Scene scene) {
        Log.write(LogSource.App, LogLevel.Info, "Adding scene ", scene.getID());
        if (Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Warning, "Scene ", scene.getID(), " already present");
            return false;
        }
        if (!scene.isInitialized()) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getID(), " requires initialization");
            if (!scene.initializeScene(++NextSceneID)) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to initialize scene ", scene.getID());
                return false;
            }
        }
        Scenes.add(scene);
        return true;
    }

    /**
     * Remove a scene from the application framework no longer to be managed by it - the framework will no longer be
     * responsible for initializing and destroying the scene after it is removed
     * @param scene The scene to remove from the application framework - this will destroy it
     * @return Whether the scene was present and was destroyed successfully
     */
    public static boolean RemoveScene(Scene scene) {
        Log.write(LogSource.App, LogLevel.Info, "Removing scene ", scene.getID());
        if (!Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Warning, "Scene ", scene.getID(), " not present");
            return false;
        }
        boolean success = true;
        if (scene.isInitialized()) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getID(), " requires destruction");
            if (!scene.destroyScene()) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy scene ", scene.getID());
                success = false;
            }
        }
        Scenes.remove(scene);
        return success;
    }

    /**
     * Get the current scene displayed by the application framework
     * @return The application framework's current scene
     */
    public static Scene GetCurrentScene() {
        return CurrentScene;
    }

    /**
     * Change the current scene displayed by the application framework to a new one - leaves the old scene if one was
     * present, adds the new scene to be managed by the application framework if not already added, and enters the new
     * scene
     * @param scene The new scene for the application framework to display, must not be null
     * @return Whether leaving the old scene and entering the new scene was done successfully
     */
    public static boolean SetCurrentScene(Scene scene) {
        if (scene == null) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to set new scene - null");
            return false;
        }
        // Leave old scene
        if (CurrentScene != null) {
            Log.write(LogSource.App, LogLevel.Info, "Leaving current scene ", CurrentScene.getID());
            Input.removeListener(CurrentScene);
            if (!CurrentScene.leave(scene)) {
                Log.write(LogSource.App, LogLevel.Error, "Failed to leave current scene ", CurrentScene.getID());
                return false;
            }
        }
        // Enter new scene
        if (!Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getID(), " not added");
            if (!AddScene(scene)) {
                Log.write(LogSource.App, LogLevel.Error, "Failed to add scene ", scene.getID());
                return false;
            }
        }
        Log.write(LogSource.App, LogLevel.Info, "Entering new scene ", scene.getID());
        if (!scene.enter(CurrentScene)) {
            return false;
        }
        if (!Input.addListener(scene)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to add scene ", scene.getID(), " to input listeners");
            return false;
        }
        CurrentScene = scene;
        return true;
    }

    /**
     * Disabled constructor for singleton App class
     */
    private App() {}

}

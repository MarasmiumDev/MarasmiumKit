/**
 * File:        App.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines the main class of the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.assets.AssetManager;
import dev.marasmium.kit.applib.assets.Configuration;
import dev.marasmium.kit.applib.audio.AudioDevice;
import dev.marasmium.kit.applib.audio.AudioManager;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.GraphicsManager;
import dev.marasmium.kit.applib.input.InputManager;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogManager;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetClient;
import dev.marasmium.kit.applib.windowing.Monitor;
import dev.marasmium.kit.applib.windowing.WindowManager;

import java.util.ArrayList;

/**
 * The main, singleton class of the MarasmiumKit's application framework
 */
public class App {

    /**
     * The application framework's logging system
     */
    public static final LogManager Log = new LogManager();
    /**
     * The application framework's user-input management system
     */
    public static final InputManager Input = new InputManager();
    /**
     * The application framework's windowing system
     */
    public static final WindowManager Window = new WindowManager();
    /**
     * The application framework's network client
     */
    public static final NetClient Network = new NetClient();
    /**
     * This application framework's asset management system
     */
    public static final AssetManager Assets = new AssetManager();
    /**
     * The application framework's audio system
     */
    public static final AudioManager Audio = new AudioManager();
    /**
     * The application framework's graphics system
     */
    public static final GraphicsManager Graphics = new GraphicsManager();

    /**
     * The scenes managed by the application framework
     */
    private static final ArrayList<Scene> Scenes = new ArrayList<>();
    /**
     * The next ID to assign to a scene managed by the application framework
     */
    private static int Next_Scene_ID = 0;
    /**
     * The application framework's current scene
     */
    private static Scene Current_Scene = null;

    /**
     * Initialize all subsystems of the MarasmiumKit application framework
     * @param configurationFilePath The file path to read the application framework's configuration from
     * @param initialScene The initial scene to be managed by the application framework
     * @return Whether the application was initialized successfully
     */
    public static boolean Initialize(String configurationFilePath, Scene initialScene) {
        if (configurationFilePath == null) {
            return false;
        }
        if (initialScene == null) {
            return false;
        }
        try {
            System.setProperty("sun.java2d.uiScale.enabled", "false");
        } catch (IllegalArgumentException _) {
            return false;
        }
        Configuration configuration = ReadConfiguration(configurationFilePath);
        if (configuration == null) {
            return false;
        }
        String logTimestampFormat;
        boolean logConsoleOutputEnabled;
        boolean logOutputFileEnabled;
        String logOutputFilePath;
        String windowTitle;
        Vector windowDimensions;
        Monitor windowMonitor;
        boolean windowFullscreen;
        boolean windowMouseCursorVisible;
        int netClientMaximumMPU;
        AudioDevice audioSpeaker;
        float audioSoundEffectsDefaultVolume;
        float audioMusicVolume;
        int graphicsTargetFPS;
        int graphicsMaximumUPF;
        Colour graphicsClearColour;
        try {
            logTimestampFormat = configuration.getStringValue(AppConfiguration.LogTimestampFormat);
            logConsoleOutputEnabled = configuration.getBooleanValue(AppConfiguration.LogConsoleOutputEnabled);
            logOutputFileEnabled = configuration.getBooleanValue(AppConfiguration.LogOutputFileEnabled);
            logOutputFilePath = configuration.getStringValue(AppConfiguration.LogOutputFilePath);
            windowTitle = configuration.getStringValue(AppConfiguration.WindowTitle);
            windowDimensions = configuration.getVectorValue(AppConfiguration.WindowDimensions);
            windowMonitor = new Monitor(configuration.getIntegerValue(AppConfiguration.WindowMonitorIndex));
            windowFullscreen = configuration.getBooleanValue(AppConfiguration.WindowFullscreen);
            windowMouseCursorVisible = configuration.getBooleanValue(AppConfiguration.WindowMouseCursorVisible);
            netClientMaximumMPU = configuration.getIntegerValue(AppConfiguration.NetClientMaximumMPU);
            audioSpeaker = new AudioDevice(configuration.getIntegerValue(AppConfiguration.AudioSpeakerIndex));
            audioSoundEffectsDefaultVolume = configuration.getFloatValue(
                    AppConfiguration.AudioSoundEffectsDefaultVolume);
            audioMusicVolume = configuration.getFloatValue(AppConfiguration.AudioMusicVolume);
            graphicsTargetFPS = configuration.getIntegerValue(AppConfiguration.GraphicsTargetFPS);
            graphicsMaximumUPF = configuration.getIntegerValue(AppConfiguration.GraphicsMaximumUPF);
            graphicsClearColour = configuration.getColourValue(AppConfiguration.GraphicsClearColour);
        } catch (NullPointerException _) {
            return false;
        }
        if (!Log.initialize(logTimestampFormat, logConsoleOutputEnabled, logOutputFileEnabled, logOutputFilePath)) {
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized logging system");
        if (!Input.initialize()) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize user-input management system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized user-input management system");
        if (!Window.initialize(windowTitle, windowDimensions, windowMonitor, windowFullscreen,
                windowMouseCursorVisible)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize windowing system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized windowing system");
        if (!Network.initialize(netClientMaximumMPU)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize network client");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized network client");
        if (!Audio.initialize(audioSpeaker, audioSoundEffectsDefaultVolume, audioMusicVolume)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize audio system");
            return false;
        }
        if (!Graphics.initialize(graphicsTargetFPS, graphicsMaximumUPF, graphicsClearColour)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to initialize graphics system");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Initialized graphics system");
        if (!SetCurrentScene(initialScene)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to set current scene");
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Set initial scene");
        Log.write(LogSource.App, LogLevel.Info, "Initialized MarasmiumKit application framework");
        return true;
    }

    /**
     * Run the main loop of the MarasmiumKit application framework
     */
    public static void Run() {
        Log.write(LogSource.App, LogLevel.Info, "Starting main application loop");
        if (Current_Scene == null) {
            return;
        }
        long deltaStartMS = System.currentTimeMillis();
        long deltaElapsedMS;
        float deltaFrames;
        long waitStartMS;
        long waitElapsedMS;
        long waitMS;
        int count = 0;
        while (!Window.isCloseRequested()) {
            waitStartMS = System.currentTimeMillis();
            if (!Current_Scene.processInput()) {
                break;
            }
            Input.update();
            Network.update();
            Audio.update();
            Graphics.beginFrame();
            Current_Scene.draw();
            if (!Graphics.endFrame()) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to end graphics frame");
                break;
            }
            if (Window.getCanvas() != null) {
                Window.getCanvas().display();
            }
            deltaElapsedMS = System.currentTimeMillis() - deltaStartMS;
            deltaStartMS = System.currentTimeMillis();
            deltaFrames = deltaElapsedMS * Graphics.getTargetFPMS();
            while (count++ < Graphics.getMaximumUPF() && deltaFrames > 1.0f) {
                Current_Scene.update(1.0f);
                deltaFrames -= 1.0f;
            }
            Current_Scene.update(deltaFrames);
            count = 0;
            waitElapsedMS = System.currentTimeMillis() - waitStartMS;
            waitMS = (long)Graphics.getTargetMSPF() - waitElapsedMS;
            try {
                Thread.sleep(waitMS);
            } catch (InterruptedException _) {
                Log.write(LogSource.App, LogLevel.Error, "Interrupted while waiting in main application loop");
                return;
            } catch (IllegalArgumentException _) {}
        }
        Log.write(LogSource.App, LogLevel.Info, "Finished main application loop");
    }

    /**
     * Add a scene to the application framework to be managed by it
     * @param scene The scene to add to the application framework - if not initialized, this will initialize it
     * @return Whether the scene was not already present and was initialized successfully
     */
    public static boolean AddScene(Scene scene) {
        if (scene == null) {
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Adding scene ", scene.getSceneID());
        if (Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Warning, "Scene ", scene.getSceneID(), " already present");
            return false;
        }
        if (!scene.isInitialized()) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getSceneID(), " requires initialization");
            if (!scene.initializeScene(++Next_Scene_ID)) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to initialize scene ", scene.getSceneID());
                return false;
            }
        }
        Scenes.add(scene);
        return true;
    }

    /**
     * Remove a scene from the application framework no longer to be managed by it
     * @param scene The scene to remove from the application framework - this will destroy it
     * @return Whether the scene was present and was destroyed successfully
     */
    public static boolean RemoveScene(Scene scene) {
        if (scene == null) {
            return false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Removing scene ", scene.getSceneID());
        if (!Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Warning, "Scene ", scene.getSceneID(), " not present");
            return false;
        }
        boolean success = true;
        if (scene.isInitialized()) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getSceneID(), " requires destruction");
            if (!scene.destroyScene()) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy scene ", scene.getSceneID());
                success = false;
            }
        }
        if (!Scenes.remove(scene)) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to remove scene ", scene.getSceneID(), " from list");
            success = false;
        }
        return success;
    }

    /**
     * Free the application framework's memory
     * @return Whether the application framework was successfully/cleanly destroyed
     */
    public static boolean Destroy() {
        Log.write(LogSource.App, LogLevel.Info, "Destroying MarasmiumKit application framework");
        boolean success = true;
        Log.write(LogSource.App, LogLevel.Info, "Freeing all scenes");
        Next_Scene_ID = 0;
        if (Current_Scene != null) {
            Current_Scene.leave(null);
            Current_Scene = null;
        }
        for (Scene scene : Scenes) {
            scene.destroy();
        }
        Scenes.clear();
        Log.write(LogSource.App, LogLevel.Info, "Destroying graphics system");
        if (!Graphics.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy graphics system");
            success = false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying audio system");
        if (!Audio.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy audio system");
            success = false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying network client");
        if (!Network.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy network client");
            success = false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying windowing system");
        if (!Window.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy windowing system");
            success = false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying user-input management system");
        if (!Input.destroy()) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to destroy user-input management system");
            success = false;
        }
        Log.write(LogSource.App, LogLevel.Info, "Destroying asset management system");
        Assets.destroy();
        Log.write(LogSource.App, LogLevel.Info, "Destroying logging system");
        if (!Log.destroy()) {
            success = false;
        }
        return success;
    }

    /**
     * Read the MarasmiumKit application framework's configuration from the disk or generate a default configuration and
     * attempt to write it
     * @param configurationFilePath The file path to read the application framework's configuration from
     * @return The configuration read from disk or the default configuration generated
     */
    private static Configuration ReadConfiguration(String configurationFilePath) {
        if (configurationFilePath == null) {
            return null;
        }
        Configuration configuration = Assets.getConfiguration(configurationFilePath);
        if (configuration != null) {
            return configuration;
        }
        configuration = new Configuration();
        configuration.initialize();
        configuration.setStringValue(AppConfiguration.LogTimestampFormat, AppConfiguration.LogTimestampFormatDefault);
        configuration.setBooleanValue(AppConfiguration.LogConsoleOutputEnabled,
                AppConfiguration.LogConsoleOutputEnabledDefault);
        configuration.setBooleanValue(AppConfiguration.LogOutputFileEnabled,
                AppConfiguration.LogOutputFileEnabledDefault);
        configuration.setStringValue(AppConfiguration.LogOutputFilePath, AppConfiguration.LogOutputFilePathDefault);
        configuration.setStringValue(AppConfiguration.WindowTitle, AppConfiguration.WindowTitleDefault);
        configuration.setVectorValue(AppConfiguration.WindowDimensions, AppConfiguration.WindowDimensionsDefault);
        configuration.setIntegerValue(AppConfiguration.WindowMonitorIndex, AppConfiguration.WindowMonitorIndexDefault);
        configuration.setBooleanValue(AppConfiguration.WindowFullscreen, AppConfiguration.WindowFullscreenDefault);
        configuration.setBooleanValue(AppConfiguration.WindowMouseCursorVisible,
                AppConfiguration.WindowMouseCursorVisibleDefault);
        configuration.setIntegerValue(AppConfiguration.NetClientMaximumMPU,
                AppConfiguration.NetClientMaximumMPUDefault);
        configuration.setIntegerValue(AppConfiguration.AudioSpeakerIndex, AppConfiguration.AudioSpeakerIndexDefault);
        configuration.setFloatValue(AppConfiguration.AudioSoundEffectsDefaultVolume,
                AppConfiguration.AudioSoundEffectsDefaultVolumeDefault);
        configuration.setFloatValue(AppConfiguration.AudioMusicVolume, AppConfiguration.AudioMusicVolumeDefault);
        configuration.setIntegerValue(AppConfiguration.GraphicsTargetFPS, AppConfiguration.GraphicsTargetFPSDefault);
        configuration.setIntegerValue(AppConfiguration.GraphicsMaximumUPF, AppConfiguration.GraphicsMaximumUPFDefault);
        configuration.setColourValue(AppConfiguration.GraphicsClearColour, AppConfiguration.GraphicsClearColourDefault);
        Assets.writeConfiguration(configuration, configurationFilePath);
        return configuration;
    }

    /**
     * Get the current scene displayed by the application framework
     * @return The application framework's current scene
     */
    public static Scene GetCurrentScene() {
        return Current_Scene;
    }

    /**
     * Leave the current scene if one is present, add the given scene to be managed by the application framework if
     * not already added, and enter the new scene
     * @param scene The new scene for the application framework to display, must not be null
     * @return Whether leaving the old scene and entering the new scene was done successfully
     */
    public static boolean SetCurrentScene(Scene scene) {
        if (scene == null) {
            Log.write(LogSource.App, LogLevel.Warning, "Failed to set new scene - null");
            return false;
        }
        if (Current_Scene != null) {
            Log.write(LogSource.App, LogLevel.Info, "Leaving current scene ", Current_Scene.getSceneID());
            if (!Input.removeListener(Current_Scene)) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to remove scene ", Current_Scene.getSceneID(),
                        " from user-input listeners");
            }
            if (!Network.removeListener(Current_Scene)) {
                Log.write(LogSource.App, LogLevel.Warning, "Failed to remove scene ", Current_Scene.getSceneID(),
                        " from network listeners");
            }
            if (!Current_Scene.leave(scene)) {
                Log.write(LogSource.App, LogLevel.Error, "Failed to leave current scene ", Current_Scene.getSceneID());
                return false;
            }
        }
        if (!Scenes.contains(scene)) {
            Log.write(LogSource.App, LogLevel.Info, "Scene ", scene.getSceneID(), " not added");
            if (!AddScene(scene)) {
                Log.write(LogSource.App, LogLevel.Error, "Failed to add scene ", scene.getSceneID());
                return false;
            }
        }
        Log.write(LogSource.App, LogLevel.Info, "Entering new scene ", scene.getSceneID());
        if (!scene.enter(Current_Scene)) {
            return false;
        }
        if (!Input.addListener(scene)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to add scene ", scene.getSceneID(), " to input listeners");
            return false;
        }
        if (!Network.addListener(scene)) {
            Log.write(LogSource.App, LogLevel.Error, "Failed to add scene ", scene.getSceneID(), " to network ",
                    "listeners");
        }
        Current_Scene = scene;
        return true;
    }

}

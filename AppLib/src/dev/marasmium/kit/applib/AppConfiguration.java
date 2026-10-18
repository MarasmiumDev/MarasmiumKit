/**
 * File:        AppConfiguration.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.15
 * Purpose:     Defines the value names and data for the MarasmiumKit application framework's default configuration
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

/**
 * Constant value names and default data for the MarasmiumKit application framework's default configuration
 */
public class AppConfiguration {

    /**
     * The value name for the logging system's timestamp format
     */
    public static final String LogTimestampFormat = "LogTimestampFormat";
    /**
     * The value name for the logging system's console output enabledness
     */
    public static final String LogConsoleOutputEnabled = "LogConsoleOutputEnabled";
    /**
     * The value name for the logging system's output file enabledness
     */
    public static final String LogOutputFileEnabled = "LogOutputFileEnabled";
    /**
     * The value name for the logging system's output file path
     */
    public static final String LogOutputFilePath = "LogOutputFilePath";
    /**
     * The value name for the windowing system's title
     */
    public static final String WindowTitle = "WindowTitle";
    /**
     * The value name for the windowing system's dimensions
     */
    public static final String WindowDimensions = "WindowDimensions";
    /**
     * The value name for the windowing system's monitor index
     */
    public static final String WindowMonitorIndex = "WindowMonitorIndex";
    /**
     * The value name for the windowing system's fullscreen state
     */
    public static final String WindowFullscreen = "WindowFullscreen";
    /**
     * The value name for the windowing system's mouse cursor visibility
     */
    public static final String WindowMouseCursorVisible = "WindowMouseCursorVisible";
    /**
     * The value name for the network client's maximum messages to process per update
     */
    public static final String NetClientMaximumMPU = "NetClientMaximumMPU";
    /**
     * The value name for the audio system's speaker index
     */
    public static final String AudioSpeakerIndex = "AudioSpeakerIndex";
    /**
     * The value name for the audio system's sound effects default volume
     */
    public static final String AudioSoundEffectsDefaultVolume = "AudioSoundEffectsDefaultVolume";
    /**
     * The value name for the audio system's music volume
     */
    public static final String AudioMusicVolume = "AudioMusicVolume";
    /**
     * The value name for the graphics system's target frames per second
     */
    public static final String GraphicsTargetFPS = "GraphicsTargetFPS";
    /**
     * The value name for the graphics system's maximum updates per frame
     */
    public static final String GraphicsMaximumUPF = "GraphicsMaximumUPF";
    /**
     * The value name for the graphics system's clear colour
     */
    public static final String GraphicsClearColour = "GraphicsClearColour";
    /**
     * The default value for the logging system's timestamp format
     */
    public static final String LogTimestampFormatDefault = "yyyy.MM.dd@HH:mm:ss.SSS";
    /**
     * The default value for the logging system's console output enabledness
     */
    public static final boolean LogConsoleOutputEnabledDefault = true;
    /**
     * The default value for the logging system's output file enabledness
     */
    public static final boolean LogOutputFileEnabledDefault = true;
    /**
     * The default value for the logging system's output file path
     */
    public static final String LogOutputFilePathDefault = "MarasmiumKit-App.log";
    /**
     * The default value for the windowing system's title
     */
    public static final String WindowTitleDefault = "MarasmiumKit App";
    /**
     * The default value for the windowing system's dimensions
     */
    public static final Vector WindowDimensionsDefault = Vector.Cartesian(1280.0f, 720.0f);
    /**
     * The default value for the windowing system's monitor index
     */
    public static final int WindowMonitorIndexDefault = 0;
    /**
     * The default value for the windowing system's fullscreen state
     */
    public static final boolean WindowFullscreenDefault = false;
    /**
     * The default value for the windowing system's cursor visibility
     */
    public static final boolean WindowMouseCursorVisibleDefault = true;
    /**
     * The default value for the network client's maximum messages to process per update
     */
    public static final int NetClientMaximumMPUDefault = -1;
    /**
     * The default value for the audio system's speaker index
     */
    public static final int AudioSpeakerIndexDefault = 0;
    /**
     * The default value for the audio system's sound effects default volume
     */
    public static final float AudioSoundEffectsDefaultVolumeDefault = 1.0f;
    /**
     * The default value for the audio system's music volume
     */
    public static final float AudioMusicVolumeDefault = 1.0f;
    /**
     * The default value for the graphics system's target frames per second
     */
    public static final int GraphicsTargetFPSDefault = 60;
    /**
     * The default value for the graphics system's maximum updates per frame
     */
    public static final int GraphicsMaximumUPFDefault = 8;
    /**
     * The default value for the graphics system's clear colour
     */
    public static final Colour GraphicsClearColourDefault = Colour.Black();

}

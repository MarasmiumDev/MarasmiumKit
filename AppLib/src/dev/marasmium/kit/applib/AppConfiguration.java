/**
 * File:        AppConfiguration.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.15
 * Purpose:     Defines the value names and data for the MarasmiumKit application framework's default configuration
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

public class AppConfiguration {

    public static final String LogTimestampFormat = "LogTimestampFormat";
    public static final String LogConsoleOutputEnabled = "LogConsoleOutputEnabled";
    public static final String LogOutputFileEnabled = "LogOutputFileEnabled";
    public static final String LogOutputFilePath = "LogOutputFilePath";
    public static final String WindowTitle = "WindowTitle";
    public static final String WindowDimensions = "WindowDimensions";
    public static final String WindowMonitorIndex = "WindowMonitorIndex";
    public static final String WindowFullscreen = "WindowFullscreen";
    public static final String WindowMouseCursorVisible = "WindowMouseCursorVisible";
    public static final String NetClientMaximumMPU = "NetClientMaximumMPU";
    public static final String AudioSpeakerIndex = "AudioSpeakerIndex";
    public static final String AudioSoundEffectsDefaultVolume = "AudioSoundEffectsDefaultVolume";
    public static final String AudioMusicVolume = "AudioMusicVolume";
    public static final String GraphicsTargetFPS = "GraphicsTargetFPS";
    public static final String GraphicsMaximumUPF = "GraphicsMaximumUPF";
    public static final String GraphicsClearColour = "GraphicsClearColour";
    public static final String LogTimestampFormatDefault = "yyyy.MM.dd@HH:mm:ss.SSS";
    public static final boolean LogConsoleOutputEnabledDefault = true;
    public static final boolean LogOutputFileEnabledDefault = true;
    public static final String LogOutputFilePathDefault = "MarasmiumKit-App.log";
    public static final String WindowTitleDefault = "MarasmiumKit App";
    public static final Vector WindowDimensionsDefault = Vector.Cartesian(1280.0f, 720.0f);
    public static final int WindowMonitorIndexDefault = 0;
    public static final boolean WindowFullscreenDefault = false;
    public static final boolean WindowMouseCursorVisibleDefault = true;
    public static final int NetClientMaximumMPUDefault = -1;
    public static final int AudioSpeakerIndexDefault = 0;
    public static final float AudioSoundEffectsDefaultVolumeDefault = 1.0f;
    public static final float AudioMusicVolumeDefault = 1.0f;
    public static final int GraphicsTargetFPSDefault = 60;
    public static final int GraphicsMaximumUPFDefault = 8;
    public static final Colour GraphicsClearColourDefault = Colour.Black;

}

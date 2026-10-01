/**
 * File:        LogManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.22
 * Purpose:     Defines a configuration/settings structure for the application framework's logging system
 */

package dev.marasmium.kit.applib.logging;

/**
 * Configuration/settings structure for the application framework's logging system
 */
public class LogManagerConfig {

    /**
     * The initial format for the timestamps attached to log messages
     */
    public String timestampFormat;
    /**
     * Whether output of logging system messages to the console is initially enabled
     */
    public boolean consoleOutputEnabled;
    /**
     * Whether output of logging system messages to a log file is initially enabled
     */
    public boolean fileOutputEnabled;
    /**
     * The initial path to the logging system's output file
     */
    public String fileOutputPath;
    /**
     * Whether output of logging system messages should initially be appended to the output file
     */
    public boolean fileOutputAppended;

    /**
     * Construct a logging system configuration structure with default settings
     */
    public LogManagerConfig() {
        timestampFormat = "yyyy.MM.dd@HH:mm:ss.SSS";
        consoleOutputEnabled = true;
        fileOutputEnabled = true;
        fileOutputPath = "MarasmiumKit.log";
        fileOutputAppended = false;
    }

}

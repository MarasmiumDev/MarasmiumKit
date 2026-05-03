/**
 * File:        LogLevel.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.28
 * Purpose:     Defines constants for log message level/severity flags
 */

package dev.marasmium.kit.applib.logging;

/**
 * Record providing constants for logging system message level/severity flags
 * @param levelName The name of this message level flag
 */
public record LogLevel(String levelName) {

    /**
     * Logging system message source flag for informational messages
     */
    public static final LogLevel Info = new LogLevel("Information");
    /**
     * Logging system message source flag for warning messages
     */
    public static final LogLevel Warning = new LogLevel("Warning");
    /**
     * Logging system message source flag for error messages
     */
    public static final LogLevel Error = new LogLevel("Error");

    /**
     * Construct a blank logging system message level flag record with default name "-"
     */
    public LogLevel() {
        this("-");
    }

    /**
     * Get this log level flag's name
     * @return The name of this log level flag
     */
    @Override
    public String toString() {
        return levelName;
    }

}

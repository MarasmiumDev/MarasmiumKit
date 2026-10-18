/**
 * File:        LogSource.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.28
 * Purpose:     Defines constants for logging system message source flags
 */

package dev.marasmium.kit.applib.logging;

/**
 * Data structure providing constants for logging system message source flags
 */
public class LogSource {

    /**
     * Logging system message source flag for the main App class
     */
    public static final LogSource App = new LogSource("MarasmiumKit App");
    /**
     * Logging system message source flag for the logging system
     */
    public static final LogSource Log = new LogSource("Logging System");
    /**
     * Logging system message source flag for the windowing system
     */
    public static final LogSource Window = new LogSource("Windowing System");
    /**
     * Logging system message source flag for the user-input management system
     */
    public static final LogSource Input = new LogSource("User-Input Management System");
    /**
     * Logging system message source flag for the networking client
     */
    public static final LogSource Network = new LogSource("Network Client");
    /**
     * Logging system message source flag for the asset management system
     */
    public static final LogSource Assets = new LogSource("Asset Management System");
    /**
     * Logging system message source flag for the audio system
     */
    public static final LogSource Audio = new LogSource("Audio System");
    /**
     * Logging system message source flag for the graphics system
     */
    public static final LogSource Graphics = new LogSource("Graphics System");

    /**
     * The name of this log source
     */
    private String sourceName;

    /**
     * Construct a blank logging system message source flag with default name "-"
     */
    public LogSource() {
        this("-");
    }

    /**
     * Construct a logging system message source flag with a name
     * @param sourceName This log source's name
     */
    public LogSource(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * Get this log source flag's name
     * @return The name of this log source flag
     */
    @Override
    public String toString() {
        return sourceName;
    }

}

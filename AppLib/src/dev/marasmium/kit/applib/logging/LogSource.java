/**
 * File:        LogSource.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.28
 * Purpose:     Defines constants for logging system message source flags
 */

package dev.marasmium.kit.applib.logging;

/**
 * Record providing constants for logging system message source flags
 * @param sourceName The name of this message source flag
 */
public record LogSource(String sourceName) {

    /**
     * Logging system message source flag for the main App class
     */
    public static final LogSource App = new LogSource("MarasmiumKit App");
    /**
     * Logging system message source flag for the logging system
     */
    public static final LogSource Log = new LogSource("Logging System");

    /**
     * Construct a blank logging system message source flag record with default name "-"
     */
    public LogSource() {
        this("-");
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

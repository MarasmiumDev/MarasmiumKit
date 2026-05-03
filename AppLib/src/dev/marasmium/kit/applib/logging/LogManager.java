/**
 * File:        LogManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.22
 * Purpose:     Defines the main class of the application framework's logging system
 */

package dev.marasmium.kit.applib.logging;

import dev.marasmium.kit.applib.App;

import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The main class of the application framework's logging system
 */
public class LogManager {

    /**
     * Whether the logging system has been initialized
     */
    private boolean initialized = false;
    /**
     * The datetime format for timestamps at the beginning of log messages
     */
    private String timestampFormat = "";
    /**
     * Whether the logging system will write messages to the console
     */
    private boolean consoleOutputEnabled = false;
    /**
     * Whether the logging system will write messages to its current output file
     */
    private boolean fileOutputEnabled = false;
    /**
     * The path of the logging system's current output file
     */
    private String fileOutputPath = "";
    /**
     * Java file handle for the logging system's current output file
     */
    private FileWriter fileOutputWriter = null;

    /**
     * Initialize the application framework's logging system
     * @param config Configuration/settings structure for the application framework's logging system
     * @return Whether the logging system's was successfully initialized
     */
    public boolean initialize(LogManagerConfig config) {
        if (this != App.Log) {
            return false;
        }
        if (initialized) {
            App.Log.write(LogSource.Log, LogLevel.Warning, "Logging system cannot be initialized twice");
            return false;
        }
        // Set memory in order
        if (!setTimestampFormat(config.timestampFormat)) {
            return false;
        }
        setConsoleOutputEnabled(config.consoleOutputEnabled);
        setFileOutputEnabled(config.fileOutputEnabled);
        if (!setFileOutputPath(config.fileOutputPath, config.fileOutputAppended)) {
            return false;
        }
        App.Log.write(LogSource.Log, LogLevel.Info, "Initialized logging system");
        initialized = true;
        return true;
    }

    /**
     * Write a log message
     * @param source The source flag for this message
     * @param level The level/severity flag for this message
     * @param data The message data to write
     * @return Whether the message was valid and was successfully written to the console and/or output file
     */
    public boolean write(LogSource source, LogLevel level, Object... data) {
        if (source == null || level == null || data == null) {
            return false;
        }
        // Compile message data
        StringBuilder message = new StringBuilder(getTimestamp() + ": [" + source + "] [" + level + "] ");
        for (Object o : data) {
            if (o != null) {
                message.append(o);
            }
        }
        message.append("\n");
        // Print to the console and/or output file
        if (consoleOutputEnabled) {
            System.out.print(message);
        }
        if (fileOutputEnabled) {
            if (fileOutputWriter != null) {
                try {
                    fileOutputWriter.write(message.toString());
                    fileOutputWriter.flush();
                } catch (IOException _) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Write a log message with the default level/severity flag
     * @param source The source flag for this message
     * @param data The message data to write
     * @return Whether the message was valid and was successfully written to the console and/or output file
     */
    public boolean write(LogSource source, Object... data) {
        return write(source, new LogLevel(), data);
    }

    /**
     * Write a log message with the default source flag
     * @param level The level/severity flag for this message
     * @param data The message data to write
     * @return Whether the message was valid and was successfully written to the console and/or output file
     */
    public boolean write(LogLevel level, Object... data) {
        return write(new LogSource(), level, data);
    }

    /**
     * Write a log message with the default source and level/severity flag
     * @param data The message data to write
     * @return Whether the message was valid and was successfully written to the console and/or output file
     */
    public boolean write(Object... data) {
        return write(new LogSource(), new LogLevel(), data);
    }

    /**
     * Free/de-initialize the memory of the application framework's logging system
     * @return Whether the logging system was destroyed successfully and cleanly
     */
    public boolean destroy() {
        if (!initialized) {
            return false;
        }
        App.Log.write(LogSource.Log, LogLevel.Info, "Destroying logging system");
        boolean success = true;
        // Reset memory
        timestampFormat = "";
        consoleOutputEnabled = false;
        fileOutputEnabled = false;
        fileOutputPath = "";
        if (fileOutputWriter != null) {
            // Close the still-open output file
            try {
                fileOutputWriter.flush();
                fileOutputWriter.close();
            } catch (IOException _) {
                success = false;
            }
            fileOutputWriter = null;
        }
        initialized = false;
        return success;
    }

    /**
     * Get a current timestamp in the logging system's timestamp format
     * @return The current date and time as a string
     */
    private String getTimestamp() {
        DateTimeFormatter formatter;
        // Use the current timestamp format if valid
        try {
            formatter = DateTimeFormatter.ofPattern(timestampFormat);
        } catch (IllegalArgumentException _) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        // Test if the format can be used on this system
        String timestamp;
        try {
            timestamp = formatter.format(LocalDateTime.now());
        } catch (DateTimeException _) {
            return "";
        }
        return timestamp;
    }

    /**
     * Get the current format for timestamps attached to log messages
     * @return The current timestamp format
     */
    public String getTimestampFormat() {
        return timestampFormat;
    }

    /**
     * Set the format for timestamps attached to log messages
     * @param timestampFormat The new timestamp format
     * @return Whether the new timestamp format was valid and could be used
     */
    public boolean setTimestampFormat(String timestampFormat) {
        if (initialized) {
            App.Log.write(LogSource.Log, LogLevel.Info, "Setting timestamp format: \"", timestampFormat, "\"");
        }
        // Test whether the new format is valid
        try {
            DateTimeFormatter.ofPattern(timestampFormat);
        } catch (IllegalArgumentException _) {
            if (initialized) {
                App.Log.write(LogSource.Log, LogLevel.Warning, "Invalid timestamp format");
            }
            return false;
        }
        // Test whether the new format can be used on this system
        String current = this.timestampFormat;
        this.timestampFormat = timestampFormat;
        if (getTimestamp().isEmpty()) {
            this.timestampFormat = current;
            App.Log.write(LogSource.Log, LogLevel.Warning, "Unsupported timestamp format");
            return false;
        }
        return true;
    }

    /**
     * Test whether the logging system currently writes message to the console
     * @return Whether console output is currently enabled
     */
    public boolean isConsoleOutputEnabled() {
        return consoleOutputEnabled;
    }

    /**
     * Set whether the logging system will write messages to the console
     * @param consoleOutputEnabled Whether console output will be enabled
     */
    public void setConsoleOutputEnabled(boolean consoleOutputEnabled) {
        if (initialized) {
            if (consoleOutputEnabled) {
                App.Log.write(LogSource.Log, LogLevel.Info, "Enabling log console output");
            } else {
                App.Log.write(LogSource.Log, LogLevel.Info, "Disabling log console output");
            }
        }
        this.consoleOutputEnabled = consoleOutputEnabled;
    }

    /**
     * Test whether the logging system currently writes messages to its output file
     * @return Whether writing messages to the output file is enabled
     */
    public boolean isFileOutputEnabled() {
        return fileOutputEnabled;
    }

    /**
     * Set whether the logging system will write messages to its output file
     * @param fileOutputEnabled Whether writing messages to the output file will be enabled
     */
    public void setFileOutputEnabled(boolean fileOutputEnabled) {
        if (initialized) {
            if (fileOutputEnabled) {
                App.Log.write(LogSource.Log, LogLevel.Info, "Enabling log file output");
            } else {
                App.Log.write(LogSource.Log, LogLevel.Info, "Disabling log file output");
            }
        }
        this.fileOutputEnabled = fileOutputEnabled;
    }

    /**
     * Get the current path of the logging system's output file
     * @return The output file path
     */
    public String getFileOutputPath() {
        return fileOutputPath;
    }

    /**
     * Set the path of the logging system's output file and close the previous file
     * @param fileOutputPath The new output file path or empty string to indicate no output file
     * @param fileOutputAppended Whether logging system output should be appended to the end of the new output file
     * @return Whether the old file (if present) could be closed and the new file (if present) could be opened
     */
    public boolean setFileOutputPath(String fileOutputPath, boolean fileOutputAppended) {
        if (initialized) {
            App.Log.write(LogSource.Log, LogLevel.Info, "Opening log file path \"", fileOutputPath, "\"");
        }
        this.fileOutputPath = fileOutputPath;
        // Close the old output file
        if (fileOutputWriter != null) {
            try {
                fileOutputWriter.flush();
                fileOutputWriter.close();
            } catch (IOException _) {
                if (initialized) {
                    App.Log.write(LogSource.Log, LogLevel.Warning, "Failed to close current log file");
                }
            }
            fileOutputWriter = null;
        }
        // Open a new output file if one was given
        if (fileOutputPath.isEmpty()) {
            return true;
        }
        try {
            fileOutputWriter = new FileWriter(fileOutputPath, fileOutputAppended);
        } catch (IOException _) {
            if (initialized) {
                App.Log.write(LogSource.Log, LogLevel.Warning, "Failed to open log file path");
            }
            return false;
        }
        return true;
    }

}

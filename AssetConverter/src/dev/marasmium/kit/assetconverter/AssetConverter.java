/**
 * File:        AssetConverter.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's asset converter program
 */

package dev.marasmium.kit.assetconverter;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.assets.AssetManagerConfig;
import dev.marasmium.kit.applib.assets.AudioTrack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The main class of the MarasmiumKit's asset converter program
 */
public class AssetConverter {

    /**
     * Convert a common audio file readable by the base JVM to the MarasmiumKit's custom audio file format
     * @param commandLine Scanner for reading command line input
     * @return Whether a file was converted successfully
     */
    private static boolean convertAudio(Scanner commandLine) {
        // Read format and data from input file
        System.out.print("Input file path: ");
        String inputFilePath;
        try {
            inputFilePath = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return false;
        }
        File inputFile = new File(inputFilePath);
        if (!inputFile.canRead()) {
            System.out.println("Failed to open input file");
            return false;
        }
        AudioInputStream inputStream;
        try {
            inputStream = AudioSystem.getAudioInputStream(inputFile);
        } catch (IOException | UnsupportedAudioFileException _) {
            System.out.println("Failed to read input file format");
            return false;
        }
        AudioFormat inputFormat = inputStream.getFormat();
        if (inputFormat == null) {
            System.out.println("Failed to read input file format");
            return false;
        }
        int inputSize;
        try {
            inputSize = inputStream.available();
        } catch (IOException _) {
            System.out.println("Failed to read input file size");
            return false;
        }
        System.out.println("Read audio file (" + inputSize + "B), format:");
        System.out.println("Input sample rate: " + inputFormat.getSampleRate() + "Hz");
        System.out.println("Input sample size: " + (inputFormat.getSampleSizeInBits() / 8) + "B");
        System.out.println("Input channel count: " + inputFormat.getChannels());
        // Read desired output format
        System.out.print("Output sample rate (Hz): ");
        String sampleRateStr;
        try {
            sampleRateStr = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return false;
        }
        int sampleRate;
        try {
            sampleRate = Integer.parseInt(sampleRateStr);
        } catch (NumberFormatException _) {
            System.out.println("Failed to parse new sample rate");
            return false;
        }
        System.out.print("Output sample size (B): ");
        String sampleSizeStr;
        try {
            sampleSizeStr = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return false;
        }
        int sampleSize;
        try {
            sampleSize = Integer.parseInt(sampleSizeStr);
        } catch (NumberFormatException _) {
            System.out.println("Failed to parse new sample size");
            return false;
        }
        System.out.print("Output channel count: ");
        String channelCountStr;
        try {
            channelCountStr = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return false;
        }
        int channelCount;
        try {
            channelCount = Integer.parseInt(channelCountStr);
        } catch (NumberFormatException _) {
            System.out.println("Failed to parse new channel count");
            return false;
        }
        // Convert data to output format
        AudioFormat outputFormat = new AudioFormat(sampleRate, 8 * sampleSize, channelCount, true, false);
        if (!AudioSystem.isConversionSupported(inputFormat, outputFormat)) {
            System.out.println("Invalid format conversion");
            return false;
        }
        AudioInputStream outputStream;
        try {
            outputStream = AudioSystem.getAudioInputStream(outputFormat, inputStream);
        } catch (IllegalArgumentException _) {
            System.out.println("Failed to open conversion stream");
            return false;
        }
        byte[] data;
        try {
            data = outputStream.readAllBytes();
            outputStream.close();
            inputStream.close();
        } catch (IOException | OutOfMemoryError _) {
            System.out.println("Failed to perform conversion");
            return false;
        }
        // Write converted audio track
        AudioTrack track = new AudioTrack();
        if (!track.initialize(sampleRate, sampleSize, channelCount, data)) {
            System.out.println("Failed to initialize audio track");
            return false;
        }
        System.out.println("Generated audio track: " + track);
        System.out.print("Output file path: " + App.Assets.getBasePath());
        String outputFilePath;
        try {
            outputFilePath = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return false;
        }
        if (!App.Assets.writeAudioTrack(track, outputFilePath)) {
            System.out.println("Failed to write converted audio file");
            return false;
        }
        track.destroy();
        return true;
    }

    /**
     * Convert a common image file readable by the base JVM to the MarasmiumKit's custom animation file format
     * @param commandLine Scanner for reading command line input
     * @return Whether a file was converted successfully
     */
    private static boolean convertAnimation(Scanner commandLine) {
        return false;
    }

    /**
     * The main entry point of the AssetConverter program
     */
    static void main() {
        System.out.println("MarasmiumKit Asset Converter");
        // Get asset base path
        Scanner commandLine = new Scanner(System.in);
        System.out.print("Base file path: ");
        String basePath;
        try {
            basePath = commandLine.nextLine();
        } catch (NoSuchElementException | IllegalStateException _) {
            System.out.println("No user input available");
            return;
        }
        // Initialize application framework asset manager
        AssetManagerConfig config = new AssetManagerConfig();
        config.applyDefaults();
        config.basePath = basePath;
        if (!App.Assets.initialize(config)) {
            System.out.println("Failed to initialize asset management system");
            return;
        }
        boolean running = true;
        while (running) {
            // Switch between audio and animation file conversion
            System.out.println("1. Audio");
            System.out.println("2. Animation");
            System.out.println("3. Exit");
            System.out.print("Mode: ");
            String modeStr;
            try {
                modeStr = commandLine.nextLine();
            } catch (NoSuchElementException | IllegalStateException _) {
                System.out.println("No user input available");
                return;
            }
            int mode;
            try {
                mode = Integer.parseInt(modeStr);
            } catch (NumberFormatException _) {
                System.out.println("Invalid mode");
                return;
            }
            if (mode < 1 || mode > 3) {
                System.out.println("Invalid mode");
                return;
            }
            switch (mode) {
                case 1:
                    if (!convertAudio(commandLine)) {
                        System.out.println("Failed to convert audio file");
                    }
                    break;
                case 2:
                    if (!convertAnimation(commandLine)) {
                        System.out.println("Failed to convert animation file");
                    }
                    break;
                case 3:
                    running = false;
                    break;
            }
        }
        App.Assets.destroy();
    }

}

/**
 * File:        AssetConverter.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.21
 * Purpose:     Defines the main class and entry point of the MarasmiumKit's asset converter program
 */

package dev.marasmium.kit.assetconverter;

import dev.marasmium.kit.assetlib.audio.AudioLoader;
import dev.marasmium.kit.assetlib.audio.AudioTrack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.Scanner;

public class AssetConverter {

    private static boolean convertAudio(Scanner commandLine) {
        // Read format and data from input file
        System.out.print("Input file path: ");
        String inputFilePath = commandLine.nextLine();
        File inputFile = new File(inputFilePath);
        if (!inputFile.canRead()) {
            System.err.println("Failed to open input file");
            return false;
        }
        AudioInputStream inputStream;
        try {
            inputStream = AudioSystem.getAudioInputStream(inputFile);
        } catch (IOException | UnsupportedAudioFileException _) {
            System.err.println("Failed to read input file format");
            return false;
        }
        AudioFormat inputFormat = inputStream.getFormat();
        int inputSize;
        try {
            inputSize = inputStream.available();
        } catch (IOException _) {
            System.err.println("Failed to read input file size");
            return false;
        }
        System.out.println("Read audio file (" + inputSize + "B), format:");
        System.out.println("Input frame rate: " + inputFormat.getFrameRate() + "Hz");
        System.out.println("Input frame size: " + inputFormat.getFrameSize() + "B");
        System.out.println("Input channel count: " + inputFormat.getChannels());
        // Read desired output format
        System.out.print("Output frame rate (Hz): ");
        String frameRateStr = commandLine.nextLine();
        int frameRate;
        try {
            frameRate = Integer.parseInt(frameRateStr);
        } catch (NumberFormatException _) {
            System.err.println("Failed to parse new frame rate");
            return false;
        }
        System.out.print("Output frame size (B): ");
        String frameSizeStr = commandLine.nextLine();
        int frameSize;
        try {
            frameSize = Integer.parseInt(frameSizeStr);
        } catch (NumberFormatException _) {
            System.err.println("Failed to parse new frame size");
            return false;
        }
        System.out.print("Output channel count: ");
        String channelCountStr = commandLine.nextLine();
        int channelCount;
        try {
            channelCount = Integer.parseInt(channelCountStr);
        } catch (NumberFormatException _) {
            System.err.println("Failed to parse new channel count");
            return false;
        }
        // Convert data to output format
        AudioFormat outputFormat = new AudioFormat(frameRate * channelCount, 8 * frameSize / channelCount, channelCount,
                true, false);
        if (!AudioSystem.isConversionSupported(inputFormat, outputFormat)) {
            System.err.println("Invalid format conversion");
            return false;
        }
        AudioInputStream outputStream;
        try {
            outputStream = AudioSystem.getAudioInputStream(outputFormat, inputStream);
        } catch (IllegalArgumentException _) {
            System.err.println("Failed to open conversion stream");
            return false;
        }
        byte[] data;
        try {
            data = outputStream.readAllBytes();
            outputStream.close();
            inputStream.close();
        } catch (IOException | OutOfMemoryError _) {
            System.err.println("Failed to perform conversion");
            return false;
        }
        // Write converted audio track
        AudioTrack track = new AudioTrack(frameRate, frameSize, channelCount, data);
        System.out.println("Generated audio track: " + track);
        System.out.print("Output file path: ");
        String outputFilePath = commandLine.nextLine();
        if (!AudioLoader.WriteTrack(outputFilePath, track)) {
            System.err.println("Failed to write converted audio file");
            return false;
        }
        return true;
    }

    private static boolean convertAnimation(Scanner commandLine) {
        return true;
    }

    static void main() {
        // Switch between audio and animation file conversion
        System.out.println("MarasmiumKit Asset Converter");
        System.out.println("1. Audio");
        System.out.println("2. Animation");
        System.out.print("Mode: ");
        Scanner commandLine = new Scanner(System.in);
        String modeStr = commandLine.nextLine();
        int mode;
        try {
            mode = Integer.parseInt(modeStr);
        } catch (NumberFormatException _) {
            System.err.println("Invalid mode");
            return;
        }
        if (mode < 1 || mode > 2) {
            System.err.println("Invalid mode");
            return;
        }
        switch (mode) {
            case 1:
                if (!convertAudio(commandLine)) {
                    System.err.println("Failed to convert audio file");
                }
                break;
            case 2:
                if (!convertAnimation(commandLine)) {
                    System.err.println("Failed to convert animation file");
                }
                break;
        }
    }

}

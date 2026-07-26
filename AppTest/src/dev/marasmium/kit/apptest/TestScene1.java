/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.audio.AudioDevice;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;

import java.util.ArrayList;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private int speaker = 0;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        return true;
    }

    @Override
    public boolean processInput() {
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.P)) {
            for (AudioDevice speaker : App.Audio.getSpeakers()) {
                App.Log.write(logSource, LogLevel.Info, "Speaker: ", speaker);
            }
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.I)) {
            App.Log.write(logSource, LogLevel.Info, "Current speaker: ", App.Audio.getSpeaker());
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.O)) {
            ArrayList<AudioDevice> speakers = App.Audio.getSpeakers();
            speaker = (speaker + 1) % speakers.size();
            App.Audio.setSpeaker(speakers.get(speaker));
            App.Log.write(logSource, LogLevel.Info, "Set speaker: ", App.Audio.getSpeaker());
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Down)) {
            App.Audio.soundEffects.setDefaultVolume(App.Audio.soundEffects.getDefaultVolume() - 0.1d);
            App.Log.write(logSource, LogLevel.Info, "Sound effects volume: ",
                    (int)(App.Audio.soundEffects.getDefaultVolume() * 100.0d), "%");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Up)) {
            App.Audio.soundEffects.setDefaultVolume(App.Audio.soundEffects.getDefaultVolume() + 0.1d);
            App.Log.write(logSource, LogLevel.Info, "Sound effects volume: ",
                    (int)(App.Audio.soundEffects.getDefaultVolume() * 100.0d), "%");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Left)) {
            App.Audio.music.setVolume(App.Audio.music.getVolume() - 0.1d);
            App.Log.write(logSource, LogLevel.Info, "Music volume: ",
                    (int)(App.Audio.music.getVolume() * 100.0d), "%");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Right)) {
            App.Audio.music.setVolume(App.Audio.music.getVolume() + 0.1d);
            App.Log.write(logSource, LogLevel.Info, "Music volume: ",
                    (int)(App.Audio.music.getVolume() * 100.0d), "%");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.One)) {
            App.Log.write(logSource, LogLevel.Info, "Playing sound effect 1");
            App.Audio.soundEffects.play("Assets/Audio/Sound_Effect_1.audio");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Two)) {
            App.Log.write(logSource, LogLevel.Info, "Playing sound effect 2");
            App.Audio.soundEffects.play("Assets/Audio/Sound_Effect_2.audio", 0.1f);
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Three)) {
            App.Log.write(logSource, LogLevel.Info, "Playing sound effect 3");
            App.Audio.soundEffects.play("Assets/Audio/Sound_Effect_3.audio", 0.75f);
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.X)) {
            App.Log.write(logSource, LogLevel.Info, "Stopping all sound effects");
            App.Audio.soundEffects.stop();
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Q)) {
            App.Log.write(logSource, LogLevel.Info, "Playing music 1");
            App.Audio.music.play("Assets/Audio/Music_1.audio");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.W)) {
            App.Log.write(logSource, LogLevel.Info, "Playing music 2");
            App.Audio.music.play("Assets/Audio/Music_2.audio");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.S)) {
            App.Log.write(logSource, LogLevel.Info, "Stopping music");
            App.Audio.music.stop();
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Space)) {
            if (!App.Audio.music.isPaused()) {
                App.Log.write(logSource, LogLevel.Info, "Pausing music");
                App.Audio.music.pause();
            } else {
                App.Log.write(logSource, LogLevel.Info, "Playing music");
                App.Audio.music.play();
            }
        }
        return true;
    }

    @Override
    public void update(double deltaFrames) {

    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }
    
}

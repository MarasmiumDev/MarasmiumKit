/**
 * File:        OptionsScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.13
 * Purpose:     Defines the options/setting scene for the MarasmiumKit application framework's test program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.assets.Configuration;
import dev.marasmium.kit.applib.audio.AudioDevice;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.windowing.Monitor;
import dev.marasmium.kit.apptest.AppTest;
import dev.marasmium.kit.apptest.sprites.Spark;
import dev.marasmium.kit.uilib.UIEvent;
import dev.marasmium.kit.uilib.UIGroup;
import dev.marasmium.kit.uilib.UIListener;
import dev.marasmium.kit.uilib.component.Button;
import dev.marasmium.kit.uilib.component.Carousel;
import dev.marasmium.kit.uilib.component.Slider;
import dev.marasmium.kit.uilib.component.Switch;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class OptionsScene extends Scene implements UIListener {

    private final LogSource logSource = new LogSource("Options Scene");
    private final UIGroup optionsUI = new UIGroup();
    private final Carousel resolutionCarousel = new Carousel();
    private final Switch fullscreenSwitch = new Switch();
    private final Carousel monitorCarousel = new Carousel();
    private final Slider musicVolumeSlider = new Slider();
    private final Slider soundEffectsVolumeSlider = new Slider();
    private final Carousel speakerCarousel = new Carousel();
    private final UIGroup controlsUI = new UIGroup();
    private final Button backButton = new Button();
    private final Button defaultsButton = new Button();
    private final Button saveButton = new Button();
    private final Camera sparkCamera = new Camera();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    private float sparkTimer = 0.0f;

    @Override
    protected boolean initialize() {
        // Declare component animations
        final String carouselAnimationFilePath = "Assets/Animations/UI/Carousel/Carousel.animation";
        final String[] carouselButtonAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Carousel/Carousel_Button_Unselected.animation",
                "Assets/Animations/UI/Carousel/Carousel_Button_Selected.animation",
                "Assets/Animations/UI/Carousel/Carousel_Button_Pressed.animation",
        };
        final String[] switchAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Switch/Switch_Off.animation",
                "Assets/Animations/UI/Switch/Switch_On.animation",
        };
        final String[] sliderAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Slider/Slider_Unselected.animation",
                "Assets/Animations/UI/Slider/Slider_Selected.animation",
        };
        final String[] sliderCursorAnimationFilePaths = new String[] {
                "Assets/Animations/UI/Slider/Slider_Cursor_Unselected.animation",
                "Assets/Animations/UI/Slider/Slider_Cursor_Selected.animation",
                "Assets/Animations/UI/Slider/Slider_Cursor_Pressed.animation",
        };
        final String[] buttonAnimationFilePaths = {
                "Assets/Animations/UI/Button/Button_Unselected.animation",
                "Assets/Animations/UI/Button/Button_Selected.animation",
                "Assets/Animations/UI/Button/Button_Pressed.animation"
        };
        // Initialize options UI components
        optionsUI.initialize(this, 0, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.20f);
        optionsUI.addComponent(resolutionCarousel);
        optionsUI.addComponent(fullscreenSwitch);
        optionsUI.addComponent(monitorCarousel);
        optionsUI.addComponent(musicVolumeSlider);
        optionsUI.addComponent(soundEffectsVolumeSlider);
        optionsUI.addComponent(speakerCarousel);
        resolutionCarousel.initialize(Vector.Cartesian(0.05f, 0.80f), Vector.Cartesian(0.425f, 0.10f),
                carouselAnimationFilePath, Alignment.Center, Alignment.Center, "WINDOW RESOLUTION", Alignment.Top,
                carouselButtonAnimationFilePaths, 0.1f, Alignment.Center,
                new String[] { "1920x1080", "1280x720", "640x360" }, 0);
        fullscreenSwitch.initialize(Vector.Cartesian(0.05f, 0.60f), Vector.Cartesian(0.425f, 0.10f),
                switchAnimationFilePaths, "FULLSCREEN", Alignment.Center);
        monitorCarousel.initialize(Vector.Cartesian(0.05f, 0.40f), Vector.Cartesian(0.425f, 0.10f),
                carouselAnimationFilePath, Alignment.Center, Alignment.Center, "MONITOR", Alignment.Top,
                carouselButtonAnimationFilePaths, 0.1f, Alignment.Center, new String[] {}, 0);
        musicVolumeSlider.initialize(Vector.Cartesian(0.525f, 0.80f), Vector.Cartesian(0.425f, 0.10f),
                sliderAnimationFilePaths, "MUSIC VOLUME", Alignment.Top, Vector.Cartesian(0.02f, 0.9f),
                sliderCursorAnimationFilePaths, Alignment.Center, 0.0f, 1.0f, 0);
        soundEffectsVolumeSlider.initialize(Vector.Cartesian(0.525f, 0.60f), Vector.Cartesian(0.425f, 0.10f),
                sliderAnimationFilePaths, "SOUND EFFECTS VOLUME", Alignment.Top, Vector.Cartesian(0.02f, 0.9f),
                sliderCursorAnimationFilePaths, Alignment.Center, 0.0f, 1.0f, 0);
        speakerCarousel.initialize(Vector.Cartesian(0.525f, 0.40f), Vector.Cartesian(0.425f, 0.10f),
                carouselAnimationFilePath, Alignment.Center, Alignment.Center, "SPEAKER", Alignment.Top,
                carouselButtonAnimationFilePaths, 0.1f, Alignment.Center, new String[] {}, 0);
        // Initialize control UI components
        controlsUI.initialize(this, 1, 1.0f, "Assets/Typefaces/Silkscreen.typeface", 4.0f, 0.35f);
        controlsUI.addComponent(backButton);
        controlsUI.addComponent(defaultsButton);
        controlsUI.addComponent(saveButton);
        backButton.initialize(Vector.Cartesian(0.05f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "BACK", Alignment.Center);
        defaultsButton.initialize(Vector.Cartesian(0.375f, 0.05f), Vector.Cartesian(0.25f, 0.10f),
                buttonAnimationFilePaths, "DEFAULTS", Alignment.Center);
        saveButton.initialize(Vector.Cartesian(0.70f, 0.05f), Vector.Cartesian(0.25f, 0.10f), buttonAnimationFilePaths,
                "SAVE", Alignment.Center);
        // Initialize camera
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        sparks.clear();
        // Display current options
        int windowWidth = (int)App.Window.getDimensions().getX();
        int windowHeight = (int)App.Window.getDimensions().getY();
        resolutionCarousel.setValue(Integer.toString(windowWidth) + "x" + Integer.toString(windowHeight));
        fullscreenSwitch.setOn(App.Window.isFullscreen());
        monitorCarousel.clearValues();
        ArrayList<Monitor> monitors = App.Window.getMonitors();
        for (int i = 0; i < monitors.size(); i++) {
            monitorCarousel.addValue(monitors.get(i).getIndex() + ": " + monitors.get(i).getDescription());
        }
        Monitor monitor = App.Window.getMonitor();
        monitorCarousel.setValue(monitor.getIndex() + ": " + monitor.getDescription());
        musicVolumeSlider.setValue(App.Audio.music.getVolume());
        soundEffectsVolumeSlider.setValue(App.Audio.soundEffects.getDefaultVolume());
        speakerCarousel.clearValues();
        ArrayList<AudioDevice> speakers = App.Audio.getSpeakers();
        for (int i = 0; i < speakers.size(); i++) {
            speakerCarousel.addValue(speakers.get(i).getIndex() + ": " + speakers.get(i).getName());
        }
        AudioDevice speaker = App.Audio.getSpeaker();
        speakerCarousel.setValue(speaker.getIndex() + ": " + speaker.getName());
        return true;
    }

    @Override
    public boolean processInput() {
        optionsUI.processInput();
        controlsUI.processInput();
        return true;
    }

    @Override
    public void draw() {
        optionsUI.draw();
        controlsUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    @Override
    public void update(float deltaFrames) {
        optionsUI.update(deltaFrames);
        controlsUI.update(deltaFrames);
        sparkTimer += deltaFrames;
        if (sparkTimer > (float)App.Graphics.getTargetFPS() / 20.0f) {
            sparkTimer = 0.0f;
            Spark spark = new Spark();
            spark.initialize();
            sparks.add(spark);
        }
        sparkCamera.setPosition(App.Window.getDimensions().scalarMultiply(0.5f));
        sparkCamera.update(deltaFrames);
        for (Spark spark : sparks) {
            spark.update(deltaFrames);
        }
        sparks.removeIf(spark -> !spark.isAlive());
        musicVolumeSlider.getLabel().setText("MUSIC VOLUME: " + (int)(musicVolumeSlider.getValue() * 100.0f) + "%");
        soundEffectsVolumeSlider.getLabel().setText("SOUND EFFECTS VOLUME: "
                + (int)(soundEffectsVolumeSlider.getValue() * 100.0f) + "%");
    }

    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    @Override
    protected boolean destroy() {
        optionsUI.destroy();
        controlsUI.destroy();
        for (Spark spark : sparks) {
            spark.destroy();
        }
        sparks.clear();
        return true;
    }

    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (groupID == optionsUI.getGroupID()) {

        } else if (groupID == controlsUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                if (componentID == backButton.getComponentID()) {
                    App.SetCurrentScene(AppTest.Title_Scene);
                } else if (componentID == defaultsButton.getComponentID()) {
                    resolutionCarousel.setValue("1280x720");
                    fullscreenSwitch.setOn(false);
                    try {
                        Monitor defaultMonitor = App.Window.getMonitors().getFirst();
                        monitorCarousel.setValue(defaultMonitor.getIndex() + ": " + defaultMonitor.getDescription());
                    } catch (NoSuchElementException _) {
                        App.Log.write(logSource, LogLevel.Error, "No monitors available");
                    }
                    musicVolumeSlider.setValue(1.0f);
                    soundEffectsVolumeSlider.setValue(1.0f);
                    try {
                        AudioDevice defaultSpeaker = App.Audio.getSpeakers().getFirst();
                        speakerCarousel.setValue(defaultSpeaker.getIndex() + ": " + defaultSpeaker.getName());
                    } catch (NoSuchElementException _) {
                        App.Log.write(logSource, LogLevel.Error, "No speakers available");
                    }
                } else if (componentID == saveButton.getComponentID()) {
                    // Update and write configuration
                    Configuration configuration = App.Assets.getConfiguration(AppTest.Configuration_File_Path);
                    String[] resolutionStrs = resolutionCarousel.getValue().split("x");
                    if (resolutionStrs.length != 2) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid resolution");
                        return;
                    }
                    int windowWidth;
                    int windowHeight;
                    try {
                        windowWidth = Integer.parseInt(resolutionStrs[0]);
                        windowHeight = Integer.parseInt(resolutionStrs[1]);
                    } catch (NumberFormatException _) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid resolution dimensions");
                        return;
                    }
                    configuration.setVectorValue("windowDimensions", Vector.Cartesian(windowWidth, windowHeight));
                    configuration.setBooleanValue("windowFullscreen", fullscreenSwitch.isOn());
                    String[] monitorStrs = monitorCarousel.getValue().split(":");
                    if (monitorStrs.length != 2) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid monitor");
                    }
                    int monitorIndex;
                    try {
                        monitorIndex = Integer.parseInt(monitorStrs[0]);
                    } catch (NumberFormatException _) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid monitor index");
                        return;
                    }
                    configuration.setIntegerValue("windowMonitorIndex", monitorIndex);
                    configuration.setFloatValue("audioMusicVolume", musicVolumeSlider.getValue());
                    configuration.setFloatValue("audioSoundEffectsDefaultVolume", soundEffectsVolumeSlider.getValue());
                    String speakerStrs[] = speakerCarousel.getValue().split(":");
                    if (speakerStrs.length != 2) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid speaker");
                        return;
                    }
                    int speakerIndex;
                    try {
                        speakerIndex = Integer.parseInt(speakerStrs[0]);
                    } catch (NumberFormatException _) {
                        App.Log.write(logSource, LogLevel.Error, "Invalid speaker index");
                        return;
                    }
                    configuration.setIntegerValue("audioSpeakerIndex", speakerIndex);
                    App.Assets.writeConfiguration(configuration, AppTest.Configuration_File_Path);
                    // Apply configuration
                    App.Window.setDimensions(configuration.getVectorValue("windowDimensions"));
                    App.Window.setMonitor(new Monitor(configuration.getIntegerValue("windowMonitorIndex")));
                    App.Window.setFullscreen(configuration.getBooleanValue("windowFullscreen"));
                    App.Audio.music.setVolume(configuration.getFloatValue("audioMusicVolume"));
                    App.Audio.soundEffects.setDefaultVolume(configuration.getFloatValue(
                            "audioSoundEffectsDefaultVolume"));
                    App.Audio.setSpeaker(new AudioDevice(configuration.getIntegerValue("audioSpeakerIndex")));
                }
            }
        }
    }
}

/**
 * File:        OptionsScene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.13
 * Purpose:     Defines the options/setting scene for the MarasmiumKit application framework's test program
 */

package dev.marasmium.kit.apptest.scenes;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.AppConfiguration;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.assets.Configuration;
import dev.marasmium.kit.applib.audio.AudioDevice;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Alignment;
import dev.marasmium.kit.applib.graphics.Camera;
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

/**
 * The options scene of the MarasmiumKit application framework's app test program
 */
public class OptionsScene extends Scene implements UIListener {

    /**
     * UI group for application options components
     */
    private final UIGroup optionsUI = new UIGroup();
    /**
     * Carousel for switching window resolutions
     */
    private final Carousel resolutionCarousel = new Carousel();
    /**
     * Switch for toggling fullscreen mode
     */
    private final Switch fullscreenSwitch = new Switch();
    /**
     * Carousel for switching the fullscreen monitor
     */
    private final Carousel monitorCarousel = new Carousel();
    /**
     * Slider for controlling music volume
     */
    private final Slider musicVolumeSlider = new Slider();
    /**
     * Slider for controlling sound effects volume
     */
    private final Slider soundEffectsVolumeSlider = new Slider();
    /**
     * Carousel for switching the audio speaker
     */
    private final Carousel speakerCarousel = new Carousel();
    /**
     * UI group for scene options
     */
    private final UIGroup controlsUI = new UIGroup();
    /**
     * Back button to go back to the title scene
     */
    private final Button backButton = new Button();
    /**
     * Button for setting all options to their defaults
     */
    private final Button defaultsButton = new Button();
    /**
     * Button for saving options to the configuration file and the currently running application
     */
    private final Button saveButton = new Button();
    /**
     * Camera for the background visual artifacts in this scene
     */
    private final Camera sparkCamera = new Camera();
    /**
     * The set of background sparks in this scene
     */
    private final ArrayList<Spark> sparks = new ArrayList<>();
    /**
     * Timer for creating new sparks in the background of this scene
     */
    private float sparkTimer = 0.0f;

    /**
     * Initialize the options scene's memory and UI components
     * @return Whether the options scene was initialized successfully
     */
    @Override
    protected boolean initialize() {
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
        sparkCamera.initialize(App.Window.getDimensions().scalarMultiply(0.5f), 1.0f, Angle.Zero());
        return true;
    }

    /**
     * Enter the options scene from another scene
     * @param lastScene Reference to the previous scene displayed by the application framework
     * @return Whether the options scene was initialized successfully
     */
    @Override
    public boolean enter(Scene lastScene) {
        int windowWidth = (int)App.Window.getDimensions().getX();
        int windowHeight = (int)App.Window.getDimensions().getY();
        resolutionCarousel.setValue(windowWidth + "x" + windowHeight);
        fullscreenSwitch.setOn(App.Window.isFullscreen());
        monitorCarousel.clearValues();
        ArrayList<Monitor> monitors = App.Window.getMonitors();
        for (Monitor value : monitors) {
            monitorCarousel.addValue(value.getIndex() + ": " + value.getDescription());
        }
        Monitor monitor = App.Window.getMonitor();
        monitorCarousel.setValue(monitor.getIndex() + ": " + monitor.getDescription());
        musicVolumeSlider.setValue(App.Audio.music.getVolume());
        soundEffectsVolumeSlider.setValue(App.Audio.soundEffects.getDefaultVolume());
        speakerCarousel.clearValues();
        ArrayList<AudioDevice> speakers = App.Audio.getSpeakers();
        for (AudioDevice audioDevice : speakers) {
            speakerCarousel.addValue(audioDevice.getIndex() + ": " + audioDevice.getName());
        }
        AudioDevice speaker = App.Audio.getSpeaker();
        speakerCarousel.setValue(speaker.getIndex() + ": " + speaker.getName());
        return true;
    }

    /**
     * Process user-input to the options scene
     * @return Whether the application should continue running
     */
    @Override
    public boolean processInput() {
        optionsUI.processInput();
        controlsUI.processInput();
        return true;
    }

    /**
     * Draw the options scene's graphics
     */
    @Override
    public void draw() {
        optionsUI.draw();
        controlsUI.draw();
        for (Spark spark : sparks) {
            App.Graphics.submit(sparkCamera, spark);
        }
    }

    /**
     * Update the options scene's logic and UI components
     * @param deltaFrames The target number of frames to have elapsed since the last update
     */
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

    /**
     * Leave the options scene for another scene
     * @param nextScene Reference to the next scene to be displayed by the application framework
     * @return Whether the options scene was left successfully
     */
    @Override
    public boolean leave(Scene nextScene) {
        return true;
    }

    /**
     * Free the options scene's memory
     * @return Whether the options scene was destroyed successfully
     */
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

    /**
     * Callback for user-interface component events on the options scene
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        if (event == null) {
            return;
        }
        if (groupID == controlsUI.getGroupID()) {
            if (event == UIEvent.Button_Released) {
                App.Audio.soundEffects.play("Assets/Audio/Click.audio");
                if (componentID == backButton.getComponentID()) {
                    App.SetCurrentScene(AppTest.Title_Scene);
                } else if (componentID == defaultsButton.getComponentID()) {
                    resolutionCarousel.setValue((int)AppConfiguration.WindowDimensionsDefault.getX() + "x"
                            + (int)AppConfiguration.WindowDimensionsDefault.getY());
                    fullscreenSwitch.setOn(AppConfiguration.WindowFullscreenDefault);
                    try {
                        Monitor defaultMonitor = App.Window.getMonitors().getFirst();
                        monitorCarousel.setValue(defaultMonitor.getIndex() + ": " + defaultMonitor.getDescription());
                    } catch (NoSuchElementException _) {}
                    musicVolumeSlider.setValue(AppConfiguration.AudioMusicVolumeDefault);
                    soundEffectsVolumeSlider.setValue(AppConfiguration.AudioSoundEffectsDefaultVolumeDefault);
                    try {
                        AudioDevice defaultSpeaker = App.Audio.getSpeakers().getFirst();
                        speakerCarousel.setValue(defaultSpeaker.getIndex() + ": " + defaultSpeaker.getName());
                    } catch (NoSuchElementException _) {}
                } else if (componentID == saveButton.getComponentID()) {
                    Configuration configuration = App.Assets.getConfiguration(AppTest.Configuration_File_Path);
                    String[] resolutionStrs = resolutionCarousel.getValue().split("x");
                    if (resolutionStrs.length != 2) {
                        return;
                    }
                    int windowWidth;
                    int windowHeight;
                    try {
                        windowWidth = Integer.parseInt(resolutionStrs[0]);
                        windowHeight = Integer.parseInt(resolutionStrs[1]);
                    } catch (NumberFormatException _) {
                        return;
                    }
                    configuration.setVectorValue(AppConfiguration.WindowDimensions,
                            Vector.Cartesian(windowWidth, windowHeight));
                    configuration.setBooleanValue(AppConfiguration.WindowFullscreen, fullscreenSwitch.isOn());
                    String[] monitorStrs = monitorCarousel.getValue().split(":");
                    if (monitorStrs.length != 2) {
                        return;
                    }
                    int monitorIndex;
                    try {
                        monitorIndex = Integer.parseInt(monitorStrs[0]);
                    } catch (NumberFormatException _) {
                        return;
                    }
                    configuration.setIntegerValue(AppConfiguration.WindowMonitorIndex, monitorIndex);
                    configuration.setFloatValue(AppConfiguration.AudioMusicVolume, musicVolumeSlider.getValue());
                    configuration.setFloatValue(AppConfiguration.AudioSoundEffectsDefaultVolume,
                            soundEffectsVolumeSlider.getValue());
                    String[] speakerStrs = speakerCarousel.getValue().split(":");
                    if (speakerStrs.length != 2) {
                        return;
                    }
                    int speakerIndex;
                    try {
                        speakerIndex = Integer.parseInt(speakerStrs[0]);
                    } catch (NumberFormatException _) {
                        return;
                    }
                    configuration.setIntegerValue(AppConfiguration.AudioSpeakerIndex, speakerIndex);
                    App.Assets.writeConfiguration(configuration, AppTest.Configuration_File_Path);
                    App.Window.setDimensions(configuration.getVectorValue(AppConfiguration.WindowDimensions));
                    App.Window.setMonitor(new Monitor(configuration.getIntegerValue(
                            AppConfiguration.WindowMonitorIndex)));
                    App.Window.setFullscreen(configuration.getBooleanValue(AppConfiguration.WindowFullscreen));
                    App.Audio.music.setVolume(configuration.getFloatValue(AppConfiguration.AudioMusicVolume));
                    App.Audio.soundEffects.setDefaultVolume(configuration.getFloatValue(
                            AppConfiguration.AudioSoundEffectsDefaultVolume));
                    App.Audio.setSpeaker(new AudioDevice(configuration.getIntegerValue(
                            AppConfiguration.AudioSpeakerIndex)));
                    enter(null);
                }
            }
        }
    }
}

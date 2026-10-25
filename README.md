# MarasmiumKit

## Overview

A collection of bespoke libraries and tools for simplified 2D Java video game
development targeting OpenJDK 26 on Windows, macOS, and x64 Linux systems.

The core design philosophy for the MarasmiumKit is to cater to beginner game
developers by abstracting common game development activities and
simplifying/consolidating diverse programming paradigms.

The initial release version of the MarasmiumKit comprises three main components:
- AppLib & UILib: A generic application layer API and a user-interface library
built on top of it
- AssetConverter: A converter program from common image, audio, and font file
types to Marasmium custom file types
- Test programs: A pair of demo programs utilizing all functions of the AppLib
and UILib

A full Javadoc for each of these components can be in the source code of this
GitHub repository and snapshots of it will be packaged alongside releases.

## AppLib

The AppLib is a general application-development API targeted at video game
development which graphics natives for Windows, macOS, and x64 Linux systems.

### Application API Functions

The MarasmiumKit's AppLib contains an API for handling the following functions:
- Logging: Writing data to the console and/or a log file with timestamps and
optional source and level flags
- Window management: Creating and assigning the title, dimensions, fullscreen
mode, and monitor of an OS-native window frame for drawing graphics in and
handling user-input on
- Keyboard and mouse user-input management: Providing callback functions and
synchronous tests for keyboard key and mouse button presses/releases and mouse
movements and scrolling
- Network communications: Providing a client and server side TCP networking
functions and callbacks for connections, incoming messages, and disconnections
- Audio: Playing sound effects and a single background music track
- 2D graphics: Drawing 2D animations at arbitrary position, dimensions, and
angle of rotation within the window
- Asset management: Reading and writing assets including animations, audio
tracks, fonts/typefaces, and configuration/general keyed-data files

### Core Structure

The AppLib is organized around a central App class which manages a collection of
Scenes. The App contains static instances of a Manager class for each function
of the library (LogManager, WindowManager, etc.). The Scenes managed by the App
provide a collection of callback functions for:
- Initializing memory
- Entering the scene
- Drawing graphics
- Processing user input
- Processing network communications
- Processing user-interface events
- Updating timed logic
- Leaving the scene
- Destroying/freeing memory

Each Manager class uses a collection of data and/or interface classes to provide
its function to the library. The AppLib also contains several general data
classes to support these functions (Vector, Colour, etc.).

The App, all Manager, and most other classes in the AppLib conform to the
following interface structure:
- Boolean initialize(Parameters...): Initializes memory and sets parameters,
returns whether initialization succeeded
- Core methods: All core functions of the class
- Boolean destroy(): Calls destroy() on member variables where possible and
frees memory, returns whether destruction succeeded
Note that constructors are not used in this interface and that the JVM garbage
collector should only be allowed to take memory after the destroy() method has
been called on an object.

## UILib

A user-interface library is built on top of the AppLib which uses its
graphics/animation and user-input processing capabilities to provide several
UI components and a grouping system to track and process them.

The UI components currently available in the UILib include:
- Text labels
- Buttons
- Switches/Toggle-Buttons
- Sliders
- Text boxes
- Carousels/Spinners

See the Getting Started section below for a guide on proper use/initialization
of user-interface components within a group.

## AssetConverter

The MarasmiumKit AppLib processes only custom file formats for audio tracks,
imagery/animations, and fonts/typefaces. The AssetConverter program released
alongside the AppLib and UILib reads audio, image, and TTF font files readable
by the OpenJDK 26 JVM and processes them, with user-selected options, into these
custom file formats.

### Images/Animations

The AssetConverter reads common image file types and processes them into grids
of equally-sized animation frames in 8bpc RGBA colour. A variable number of
frames will be saved as an animation file by the AssetConverter up to the
maximum number of frames read in from the original image.

Animation frames are indexed from the bottom-left corner of the image by rows as
depicted here:
```
-------
|3|4|5|
-------
|0|1|2|
-------
```

### Audio Tracks

Common audio file types are read and are re-written in a user-defined format,
allowing the sample rate, sample size (in bytes), and number of audio channels
to be modified from the original audio data.

Audio data is written in the default linear PCM format used by the JVM.

### Fonts/Typefaces

The AssetConverter reads TTF font files, renders them at a user-defined
point-size and colour, and stores their glyph metrics and bitmap renders in
typeface files. Glyph metrics are stored in binary format and are mapped to
frame indices in a Marasmium animation packed at the end of the typeface file in
the format described above.

## Getting Started

Building an application with the AppLib and optionally the UILib requires the
build environment to include several libraries and to enable several JVM
options. Using the Build and JVM Options below, the application should run
uniformly on x64 Windows, ARM/x64 macOS, and x64 Linux systems.

All assets (images, audio, fonts) used in the project should be generated by the
MarasmiumKit AssetConverter. The configuration file should only be edited by the
MarasmiumKit AppLib's asset management methods for configuration files.

### Build and JVM Options

The application's the be built with OpenJDK 26 or later and should use the
following JVM options when building (optional in OpenJDK 26, but may cause
problems in future releases):
- `--enable-native-access=ALL-UNNAMED`
- `--add-opens=java.desktop/sun.awt=ALL-UNNAMED`

When building, the project must include the compiled AppLib and gluegen and JOGL
natives for the target system (redistributed under the JOGL license in the
vendor directory of this repository). To create a runnable JAR, all of these
must be packaged alongside the application's compile output.

The compile output of the UILib is optional.

### Build Environment Test

It is recommended to use the code below to test the build environment described
above.

```
import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

public class TestScene extends Scene {

    private LogSource logSource = new LogSource("Test Scene");

    static void main() {
        TestScene testScene = new TestScene();
        App.Initialize("MarasmiumKit-App.configuration", testScene);
        App.Run();
        App.Destroy();
    }

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initialized test scene");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entered test scene");
        return true;
    }

    @Override
    public void draw() {}

    @Override
    public void update(float deltaFrames) {}

    @Override
    public boolean processInput() {
        return true;
    }

    @Override
    public boolean leave(Scene nextScene) {
        App.Log.write(logSource, LogLevel.Info, "Left test scene");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroyed test scene");
        return true;
    }

}
```

If everything works correctly, when built into a runnable JAR, this code
will generate a log file with no errors which contains the following lines:

```
{Timestamp} [Test Scene] [Information] Initialized test scene
{Timestamp} [Test Scene] [Information] Entered test scene
{Timestamp} [Test Scene] [Information] Left test scene
{Timestamp} [Test Scene] [Information] Destroyed test scene
```

### Suggested Project Structure

Once it is established that the build environment is correctly configured for
your IDE, it is suggested that applications using the MarasmiumKit API conform
to the following structure, having:
- A main class which contains public static instances of each scene to be used
in the application
- A "scenes" package containing all scenes to be used in the application
- An "entities" package containing all objects extending from the Sprite class

This structure is reflected in the AppTest program packaged with the
MarasmiumKit v1.0 Release.
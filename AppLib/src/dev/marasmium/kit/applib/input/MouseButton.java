/**
 * File:        MouseButton.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.06
 * Purpose:     Defines constants wrapping Java AWT virtual mouse button codes
 */

package dev.marasmium.kit.applib.input;

/**
 * Constants representing buttons on the mouse wrapping Java AWT virtual mouse button codes
 */
public enum MouseButton {

    // Positioned buttons
    Left("BUTTON1"),
    Middle("BUTTON2"),
    Right("BUTTON3");

    /**
     * The field name of the Java AWT virtual mouse button code for this button
     */
    private final String AWTName;

    /**
     * Constant a button with its Java AWT virtual mouse button code name
     * @param AWTName The Java AWT virtual mouse button code name
     */
    MouseButton(String AWTName) {
        if (AWTName == null) {
            this.AWTName = "";
            return;
        }
        this.AWTName = AWTName;
    }

    /**
     * Get the Java AWT virtual mouse button code name for this button
     * @return This button's string representation
     */
    @Override
    public String toString() {
        return AWTName;
    }

}

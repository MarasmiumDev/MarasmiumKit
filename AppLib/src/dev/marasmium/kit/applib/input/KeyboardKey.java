/**
 * File:        KeyboardKey.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines constants wrapping Java AWT virtual key codes
 */

package dev.marasmium.kit.applib.input;

/**
 * Constants representing keys on the keyboard wrapping Java AWT virtual key codes
 */
public enum KeyboardKey {

    // Function keys
    F1("VK_F1"),
    F2("VK_F2"),
    F3("VK_F3"),
    F4("VK_F4"),
    F5("VK_F5"),
    F6("VK_F6"),
    F7("VK_F7"),
    F8("VK_F8"),
    F9("VK_F9"),
    F10("VK_F10"),
    F11("VK_F11"),
    F12("VK_F12"),
    // Number keys
    ONE("VK_1"),
    TWO("VK_2"),
    THREE("VK_3"),
    FOUR("VK_4"),
    FIVE("VK_5"),
    SIX("VK_6"),
    SEVEN("VK_7"),
    EIGHT("VK_8"),
    NINE("VK_9"),
    ZERO("VK_0"),
    // Alphabet keys
    A("VK_A"),
    B("VK_B"),
    C("VK_C"),
    D("VK_D"),
    E("VK_E"),
    F("VK_F"),
    G("VK_G"),
    H("VK_H"),
    I("VK_I"),
    J("VK_J"),
    K("VK_K"),
    L("VK_L"),
    M("VK_M"),
    N("VK_N"),
    O("VK_O"),
    P("VK_P"),
    Q("VK_Q"),
    R("VK_R"),
    S("VK_S"),
    T("VK_T"),
    U("VK_U"),
    V("VK_V"),
    W("VK_W"),
    X("VK_X"),
    Y("VK_Y"),
    Z("VK_Z"),
    // Symbol keys
    GRAVE("VK_BACK_QUOTE"),
    MINUS("VK_MINUS"),
    EQUALS("VK_EQUALS"),
    BACKSLASH("VK_BACK_SLASH"),
    OPEN_BRACKET("VK_OPEN_BRACKET"),
    CLOSE_BRACKET("VK_CLOSE_BRACKET"),
    SEMICOLON("VK_SEMICOLON"),
    APOSTROPHE("VK_QUOTE"),
    PERIOD("VK_PERIOD"),
    COMMA("VK_COMMA"),
    SLASH("VK_SLASH"),
    // Text control keys
    TAB("VK_TAB"),
    BACKSPACE("VK_BACK_SPACE"),
    ENTER("VK_ENTER"),
    CAPS_LOCK("VK_CAPS_LOCK"),
    SHIFT("VK_SHIFT"),
    SPACE("VK_SPACE"),
    // System control keys
    ESCAPE("VK_ESCAPE"),
    CONTROL("VK_CONTROL"),
    ALT("VK_ALT"),
    PRINT_SCREEN("VK_PRINTSCREEN"),
    SCROLL_LOCK("VK_SCROLL_LOCK"),
    PAUSE("VK_PAUSE"),
    INSERT("VK_INSERT"),
    HOME("VK_HOME"),
    DELETE("VK_DELETE"),
    END("VK_END"),
    PAGE_UP("VK_PAGE_UP"),
    PAGE_DOWN("VK_PAGE_DOWN"),
    UP("VK_UP"),
    DOWN("VK_DOWN"),
    LEFT("VK_LEFT"),
    RIGHT("VK_RIGHT"),
    // Keypad keys
    NUM_LOCK("VK_NUM_LOCK"),
    DIVIDE("VK_DIVIDE"),
    MULTIPLY("VK_MULTIPLY"),
    SUBTRACT("VK_SUBTRACT"),
    ADD("VK_ADD"),
    DECIMAL("VK_DECIMAL"),
    KEYPAD_UP("VK_KP_UP"),
    KEYPAD_DOWN("VK_KP_DOWN"),
    KEYPAD_LEFT("VK_KP_LEFT"),
    KEYPAD_RIGHT("VK_KP_RIGHT");

    /**
     * The field name of the Java AWT virtual key code for this key
     */
    private final String AWTName;

    /**
     * Construct a key with its Java AWT virtual key code name
     * @param AWTName The Java AWT virtual key code name
     */
    KeyboardKey(String AWTName) {
        this.AWTName = AWTName;
    }

    /**
     * Get the Java AWT virtual key code name for this key
     * @return This key's string representation
     */
    @Override
    public String toString() {
        return AWTName;
    }

}

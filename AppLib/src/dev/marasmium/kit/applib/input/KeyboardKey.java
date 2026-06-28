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
    One("VK_1"),
    Two("VK_2"),
    Three("VK_3"),
    Four("VK_4"),
    Five("VK_5"),
    Six("VK_6"),
    Seven("VK_7"),
    Eight("VK_8"),
    Nine("VK_9"),
    Zero("VK_0"),
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
    Grave("VK_BACK_QUOTE"),
    Minus("VK_MINUS"),
    Equals("VK_EQUALS"),
    Backslash("VK_BACK_SLASH"),
    Open_Bracket("VK_OPEN_BRACKET"),
    Close_Bracket("VK_CLOSE_BRACKET"),
    Semicolon("VK_SEMICOLON"),
    Apostrophe("VK_QUOTE"),
    Period("VK_PERIOD"),
    Comma("VK_COMMA"),
    Slash("VK_SLASH"),
    // Text control keys
    Tab("VK_TAB"),
    Backspace("VK_BACK_SPACE"),
    Enter("VK_ENTER"),
    Caps_Lock("VK_CAPS_LOCK"),
    Shift("VK_SHIFT"),
    Space("VK_SPACE"),
    // System control keys
    Escape("VK_ESCAPE"),
    Control("VK_CONTROL"),
    Alt("VK_ALT"),
    Print_Screen("VK_PRINTSCREEN"),
    Scroll_Lock("VK_SCROLL_LOCK"),
    Pause("VK_PAUSE"),
    Insert("VK_INSERT"),
    Home("VK_HOME"),
    Delete("VK_DELETE"),
    End("VK_END"),
    Page_Up("VK_PAGE_UP"),
    Page_Down("VK_PAGE_DOWN"),
    Up("VK_UP"),
    Down("VK_DOWN"),
    Left("VK_LEFT"),
    Right("VK_RIGHT"),
    // Keypad keys
    Num_Lock("VK_NUM_LOCK"),
    Divide("VK_DIVIDE"),
    Multiply("VK_MULTIPLY"),
    Subtract("VK_SUBTRACT"),
    Add("VK_ADD"),
    Decimal("VK_DECIMAL"),
    Keypad_Up("VK_KP_UP"),
    Keypad_Down("VK_KP_DOWN"),
    Keypad_Left("VK_KP_LEFT"),
    Keypad_Right("VK_KP_RIGHT");

    /**
     * The field name of the Java AWT virtual key code for this key
     */
    private final String AWTName;

    /**
     * Construct a key with its Java AWT virtual key code name
     * @param AWTName The Java AWT virtual key code name
     */
    KeyboardKey(String AWTName) {
        if (AWTName == null) {
            this.AWTName = "";
            return;
        }
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

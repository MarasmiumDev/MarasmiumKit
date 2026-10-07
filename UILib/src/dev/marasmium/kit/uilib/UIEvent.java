/**
 * File:        UIEvent.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.29
 * Purpose:     Defines constant codes for user-interface events
 */

package dev.marasmium.kit.uilib;

/**
 * Enumeration of constants representing user-interface event types
 */
public enum UIEvent {

    /**
     * Button events
     */
    Button_Unselected,
    Button_Selected,
    Button_Pressed,
    Button_Released,
    /**
     * Switch events
     */
    Switch_Off,
    Switch_On,
    /**
     * Slider events
     */
    Slider_Unselected,
    Slider_Selected,
    Slider_Value_Set,
    /**
     * Text box events
     */
    Text_Box_Unselected,
    Text_Box_Selected,
    Text_Box_Value_Set,
    /**
     * Carousel events
     */
    Carousel_Value_Set,

}

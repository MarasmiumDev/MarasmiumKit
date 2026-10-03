/**
 * File:        UIListener.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.28
 * Purpose:     Defines callback functions for user-interface component events
 */

package dev.marasmium.kit.uilib;

import dev.marasmium.kit.applib.graphics.Camera;

public interface UIListener {

    default UIListener getParent() {
        return null;
    }

    default float getBaseDepth() {
        return 0.0f;
    }

    default String getTypefaceFilePath() {
        return null;
    }

    default float getTextSize() {
        return 0.0f;
    }

    default float getTextPadding() {
        return 0.0f;
    }

    default int getGroupID() {
        return 0;
    }

    default Camera getCamera() {
        return null;
    }

    default void componentEvent(int groupID, int componentID, UIEvent event) {}

}

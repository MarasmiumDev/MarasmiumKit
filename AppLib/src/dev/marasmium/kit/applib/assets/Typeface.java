/**
 * File:        Typeface.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.16
 * Purpose:     Defines a data structure containing metrics and animation data for a typeface to be drawn by the
 *              graphics system
 */

package dev.marasmium.kit.applib.assets;

import java.util.HashMap;

/**
 * Data structure containing metrics and animation data for a typeface to be drawn by the graphics system
 */
public class Typeface {

    /**
     * Mapping of characters in this typeface to glyph identifiers and metrics
     */
    private final HashMap<Character, Glyph> glyphs = new HashMap<>();
    /**
     * The serialized animation containing the glyph images of this typeface
     */
    private byte[] animationData = null;
    /**
     * The constructed file path of the animation containing the glyph images of this typeface
     */
    private String animationFilePath = null;

    /**
     * Initialize this typeface with a set of characters mapped to glyph metrics and an animation file path
     * @param characters The set of characters in this typeface
     * @param glyphMetrics The set of glyph metrics corresponding to the characters of this typeface
     * @param animationData The serialized animation containing the glyph images of this typeface
     * @return Whether all parameters were valid and this typeface was initialized successfully
     */
    public boolean initialize(String characters, Glyph[] glyphMetrics, byte[] animationData) {
        if (characters == null || glyphMetrics == null || animationData == null) {
            return false;
        }
        if (characters.length() != glyphMetrics.length) {
            return false;
        }
        for (int i = 0; i < characters.length(); i++) {
            if (!addGlyph(characters.charAt(i), glyphMetrics[i])) {
                return false;
            }
        }
        if (!setAnimationData(animationData)) {
            return false;
        }
        return true;
    }

    /**
     * Free this typeface's memory
     * @return Whether this typeface's memory could be freed safely
     */
    public boolean destroy() {
        boolean success = true;
        for (HashMap.Entry<Character, Glyph> entry : glyphs.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    entry.getValue().destroy();
                }
            } catch (IllegalStateException _) {
                success = false;
            }
        }
        glyphs.clear();
        animationFilePath = null;
        return success;
    }

    /**
     * Get the set of characters in this typeface mapped to their glyph metrics
     * @return This typeface's glyph metrics
     */
    public HashMap<Character, Glyph> getGlyphs() {
        return glyphs;
    }

    /**
     * Get the set of characters in this typeface
     * @return The set of characters in this typeface
     */
    public String getCharacters() {
        StringBuilder characters = new StringBuilder();
        for (HashMap.Entry<Character, Glyph> entry : glyphs.entrySet()) {
            try {
                characters.append(entry.getKey());
            } catch (IllegalStateException _) {
                return null;
            }
        }
        return characters.toString();
    }

    /**
     * Get the set of glyph metrics corresponding to a given character in this typeface
     * @param character The character to retrieve the glyph metrics for
     * @return The glyph metrics of the given character or null if the character was not found in this typeface
     */
    public Glyph getGlyph(char character) {
        if (!glyphs.containsKey(character)) {
            return null;
        }
        return glyphs.get(character);
    }

    /**
     * Add a character and its corresponding glyph metrics to this typeface
     * @param character The character to add
     * @param glyph The corresponding glyph metrics to add
     * @return Whether the character and glyph metrics were valid and added successfully
     */
    public boolean addGlyph(char character, Glyph glyph) {
        if (glyph == null) {
            return false;
        }
        if (glyphs.containsKey(character)) {
            return false;
        }
        glyphs.put(character, glyph);
        return true;
    }

    /**
     * Remove a set of glyph metrics corresponding to a character in this typeface
     * @param character The character to remove
     * @return Whether the given character was in memory and was removed successfully
     */
    public boolean removeGlyph(char character) {
        if (!glyphs.containsKey(character)) {
            return false;
        }
        return glyphs.remove(character) != null;
    }

    /**
     * Get the serialized animation containing this typeface's glyph images
     * @return This typeface's animation data
     */
    public byte[] getAnimationData() {
        return animationData;
    }

    /**
     * Set the serialized animation containing this typeface's glyph images
     * @param animationData This typeface's animation data
     * @return Whether the given animation data was valid and was set successfully
     */
    public boolean setAnimationData(byte[] animationData) {
        if (animationData == null) {
            return false;
        }
        this.animationData = new byte[animationData.length];
        try {
            System.arraycopy(animationData, 0, this.animationData, 0, animationData.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException _) {
            return false;
        }
        return true;
    }

    /**
     * Get the constructed file path of the animation containing this typeface's glyph images
     * @return This typeface's animation file path
     */
    public String getAnimationFilePath() {
        return animationFilePath;
    }

    /**
     * Set the constructed file path of the animation containing this typeface's glyph images
     * @param animationFilePath This typeface's new animation file path
     * @return Whether the given file path was valid and was set successfully
     */
    public boolean setAnimationFilePath(String animationFilePath) {
        if (animationFilePath == null) {
            return false;
        }
        if (animationFilePath.isEmpty()) {
            return false;
        }
        this.animationFilePath = animationFilePath;
        return true;
    }

    /**
     * Get a string representing this typeface
     * @return This typeface's string representation
     */
    @Override
    public String toString() {
        if (animationData == null) {
            return "typeface(null)";
        }
        if (animationFilePath == null) {
            return "typeface(" + glyphs.size() + " glyphs, " + animationData.length + "B)";
        }
        return "typeface(" + glyphs.size() + " glyphs, " + animationData.length + "B at \"" + animationFilePath + "\")";
    }

}

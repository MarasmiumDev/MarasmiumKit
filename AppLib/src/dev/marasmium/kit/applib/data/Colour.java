/**
 * File:        Colour.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Defines an RGBA colour data structure and related conversion operations
 */

package dev.marasmium.kit.applib.data;

import java.io.Serializable;

/**
 * RGBA colour data structure with related constants mathematical operations
 */
public class Colour implements Serializable {

    /**
     * Black colour constant
     */
    public static final Colour Black = Colour.Bytes(0x000000FF);
    /**
     * Red colour constant
     */
    public static final Colour Red = Colour.Bytes(0xFF0000FF);
    /**
     * Green colour constant
     */
    public static final Colour Green = Colour.Bytes(0x00FF00FF);
    /**
     * Blue colour constant
     */
    public static final Colour Blue = Colour.Bytes(0x0000FFFF);
    /**
     * White colour constant
     */
    public static final Colour White = Colour.Bytes(0xFFFFFFFF);

    /**
     * Byte representation of RGBA channels
     */
    private int RGBA;

    /**
     * Create a new colour from RGBA channel integer values
     * @param red Red channel (0-255)
     * @param green Green channel (0-255)
     * @param blue Blue channel (0-255)
     * @param alpha Alpha channel (0-255)
     * @return A colour with the given RGBA channel values
     */
    public static Colour Channels(int red, int green, int blue, int alpha) {
        Colour c = new Colour();
        c.setRed(red);
        c.setGreen(green);
        c.setBlue(blue);
        c.setAlpha(alpha);
        return c;
    }

    /**
     * Create a new colour from the bytes of an RGBA integer value
     * @param RGBA Integer with byte values of RGBA channels
     * @return A colour with the given RGBA value
     */
    public static Colour Bytes(int RGBA) {
        Colour c = new Colour();
        c.setRGBA(RGBA);
        return c;
    }

    /**
     * Construct a blank colour
     */
    private Colour() {
        this.RGBA = 0x00000000;
    }

    /**
     * Compute the colour given by alpha-blending another colour on top of it
     * @param c A colour to blend over this one
     * @return This colour with c alpha-blended on top of it
     */
    public Colour blend(Colour c) {
        if (c == null) {
            return null;
        }
        int aUnder = getAlpha();
        int aOver = c.getAlpha();
        int aOut255 = ((255 * aOver) + ((255 - aOver) * aUnder));
        return Colour.Channels(
                ((255 * c.getRed() * aOver) + ((255 - aOver) * getRed() * aUnder)) / (aOut255),
                ((255 * c.getGreen() * aOver) + ((255 - aOver) * getGreen() * aUnder)) / (aOut255),
                ((255 * c.getBlue() * aOver) + ((255 - aOver) * getBlue() * aUnder)) / (aOut255),
                aOut255 / 255);
    }

    /**
     * Get the integer whose bytes represent this colour's RGBA channel values
     * @return This colour's RGBA integer representation
     */
    public int getRGBA() {
        return RGBA;
    }

    /**
     * Set this colour's RGBA channels by the bytes of a given integer
     * @param RGBA This colour's new RGBA integer representation
     */
    public void setRGBA(int RGBA) {
        this.RGBA = RGBA;
    }

    /**
     * Get the red channel value of this colour
     * @return The red channel of this colour
     */
    public int getRed() {
        return (RGBA >> 24) & 0xFF;
    }

    /**
     * Set the red channel value of this colour
     * @param red The new red channel value for this colour (0-255)
     */
    public void setRed(int red) {
        RGBA &= 0x00FFFFFF;
        RGBA |= ((red << 24) & 0xFF000000);
    }

    /**
     * Get the green channel value of this colour
     * @return The green channel of this colour
     */
    public int getGreen() {
        return (RGBA >> 16) & 0xFF;
    }

    /**
     * Set the green channel value of this colour
     * @param green The new green channel value for this colour (0-255)
     */
    public void setGreen(int green) {
        RGBA &= 0xFF00FFFF;
        RGBA |= ((green << 16) & 0x00FF0000);
    }

    /**
     * Get the blue channel value of this colour
     * @return The blue channel value of this colour
     */
    public int getBlue() {
        return (RGBA >> 8) & 0xFF;
    }

    /**
     * Set the blue channel value of this colour
     * @param blue The new blue channel value of this colour (0-255)
     */
    public void setBlue(int blue) {
        RGBA &= 0xFFFF00FF;
        RGBA |= ((blue << 8) & 0x0000FF00);
    }

    /**
     * Get the alpha channel value of this colour
     * @return The alpha channel value of this colour
     */
    public int getAlpha() {
        return RGBA & 0xFF;
    }

    /**
     * Set the alpha channel value of this colour
     * @param alpha The new alpha channel value of this colour (0-255)
     */
    public void setAlpha(int alpha) {
        RGBA &= 0xFFFFFF00;
        RGBA |= (alpha & 0x000000FF);
    }

    /**
     * Test whether this colour is equal to another
     * @param o The other object to test (must be a Colour)
     * @return Whether this colour is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Colour c)) {
            return false;
        }
        return RGBA == c.RGBA;
    }

    /**
     * Convert this colour to a string
     * @return The string representation of this colour
     */
    @Override
    public String toString() {
        return "colour(" + getRed() + ", " + getGreen() + ", " + getBlue() + ", " + getAlpha() + ")";
    }

}

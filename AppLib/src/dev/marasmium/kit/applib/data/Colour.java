/**
 * File:        Colour.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.03
 * Purpose:     Defines an ARGB colour data structure and related conversion operations
 */

package dev.marasmium.kit.applib.data;

import java.io.Serializable;

/**
 * ARGB colour data structure with related constants mathematical operations
 */
public class Colour implements Serializable {

    /**
     * Blank colour constant
     */
    public static final Colour Blank = Colour.Bytes(0x00000000);
    /**
     * Black colour constant
     */
    public static final Colour Black = Colour.Bytes(0xFF000000);
    /**
     * Red colour constant
     */
    public static final Colour Red = Colour.Bytes(0xFFFF0000);
    /**
     * Green colour constant
     */
    public static final Colour Green = Colour.Bytes(0xFF00FF00);
    /**
     * Yellow colour constant
     */
    public static final Colour Yellow = Colour.Bytes(0xFFFFFF00);
    /**
     * Blue colour constant
     */
    public static final Colour Blue = Colour.Bytes(0xFF0000FF);
    /**
     * Magenta colour constant
     */
    public static final Colour Magenta = Colour.Bytes(0xFFFF00FF);
    /**
     * Cyan colour constant
     */
    public static final Colour Cyan = Colour.Bytes(0xFF00FFFF);
    /**
     * White colour constant
     */
    public static final Colour White = Colour.Bytes(0xFFFFFFFF);

    /**
     * Byte representation of ARGB channels
     */
    private int ARGB;

    /**
     * Create a new colour from ARGB channel integer values
     * @param alpha Alpha channel (0-255)
     * @param red Red channel (0-255)
     * @param green Green channel (0-255)
     * @param blue Blue channel (0-255)
     * @return A colour with the given ARGB channel values
     */
    public static Colour Channels(int alpha, int red, int green, int blue) {
        Colour c = new Colour();
        c.setAlpha(alpha);
        c.setRed(red);
        c.setGreen(green);
        c.setBlue(blue);
        return c;
    }

    /**
     * Create a new colour from the bytes of an ARGB integer value
     * @param ARGB Integer with byte values of ARGB channels
     * @return A colour with the given ARGB value
     */
    public static Colour Bytes(int ARGB) {
        Colour c = new Colour();
        c.setARGB(ARGB);
        return c;
    }

    /**
     * Construct a blank colour
     */
    private Colour() {
        this.ARGB = 0x00000000;
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
     * Get the integer whose bytes represent this colour's ARGB channel values
     * @return This colour's ARGB integer representation
     */
    public int getARGB() {
        return ARGB;
    }

    /**
     * Set this colour's ARGB channels by the bytes of a given integer
     * @param ARGB This colour's new ARGB integer representation
     */
    public void setARGB(int ARGB) {
        this.ARGB = ARGB;
    }

    /**
     * Get the alpha channel value of this colour
     * @return The red channel of this colour
     */
    public int getAlpha() {
        return (ARGB >> 24) & 0xFF;
    }

    /**
     * Set the alpha channel value of this colour
     * @param alpha The new red channel value for this colour (0-255)
     */
    public void setAlpha(int alpha) {
        ARGB &= 0x00FFFFFF;
        ARGB |= ((alpha << 24) & 0xFF000000);
    }

    /**
     * Get the red channel value of this colour
     * @return The red channel of this colour
     */
    public int getRed() {
        return (ARGB >> 16) & 0xFF;
    }

    /**
     * Set the red channel value of this colour
     * @param red The new red channel value for this colour (0-255)
     */
    public void setRed(int red) {
        ARGB &= 0xFF00FFFF;
        ARGB |= ((red << 16) & 0x00FF0000);
    }

    /**
     * Get the green channel value of this colour
     * @return The green channel value of this colour
     */
    public int getGreen() {
        return (ARGB >> 8) & 0xFF;
    }

    /**
     * Set the green channel value of this colour
     * @param green The new green channel value of this colour (0-255)
     */
    public void setGreen(int green) {
        ARGB &= 0xFFFF00FF;
        ARGB |= ((green << 8) & 0x0000FF00);
    }

    /**
     * Get the blue channel value of this colour
     * @return The blue channel value of this colour
     */
    public int getBlue() {
        return ARGB & 0xFF;
    }

    /**
     * Set the blue channel value of this colour
     * @param blue The new blue channel value of this colour (0-255)
     */
    public void setBlue(int blue) {
        ARGB &= 0xFFFFFF00;
        ARGB |= (blue & 0x000000FF);
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
        return ARGB == c.ARGB;
    }

    /**
     * Convert this colour to a string
     * @return The string representation of this colour
     */
    @Override
    public String toString() {
        return "colour(" + getAlpha() + ", " + getRed() + ", " + getGreen() + ", " + getBlue() + ")";
    }

}

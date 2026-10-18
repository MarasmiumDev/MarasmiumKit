/**
 * File:        PlayerMetrics.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.16
 * Purpose:     Defines a serializable data structure for wrapping a player's information in the
 *              MarasmiumKit application framework's test server
 */

package dev.marasmium.kit.servertest;

import dev.marasmium.kit.applib.data.Vector;

/**
 * Wrapper for a player's position and velocity
 */
public class PlayerMetrics {

    /**
     * The player's position
     */
    private Vector position = null;
    /**
     * The player's velocity
     */
    private Vector velocity = null;

    /**
     * Initialize this set of player metrics
     * @param position The player's position
     * @param velocity The player's velocity
     * @return Whether the player metrics were initialized successfully
     */
    public boolean initialize(Vector position, Vector velocity) {
        if (!setPosition(position)) {
            return false;
        }
        if (!setVelocity(velocity)) {
            return false;
        }
        return true;
    }

    /**
     * Free this set of player metrics' memory
     */
    public void destroy() {
        position = null;
        velocity = null;
    }

    /**
     * Get the player's position
     * @return The player's position
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Set the player's position
     * @param position The player's new position
     * @return Whether the given position was valid
     */
    public boolean setPosition(Vector position) {
        if (position == null) {
            return false;
        }
        this.position = position;
        return true;
    }

    /**
     * Get the player's velocity
     * @return The player's velocity
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Set the player's velocity
     * @param velocity The player's new velocity
     * @return Whether the given velocity was valid
     */
    public boolean setVelocity(Vector velocity) {
        if (velocity == null) {
            return false;
        }
        this.velocity = velocity;
        return true;
    }

}

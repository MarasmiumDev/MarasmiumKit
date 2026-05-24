/**
 * File:        Scene.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.21
 * Purpose:     Defines an interface for scenes within the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

/**
 * Abstract interface for a scene within the MarasmiumKit's application framework with callbacks for application
 * processes. This class is intended to be managed (initialized and destroyed) by the framework's App class
 */
public abstract class Scene {

    /**
     * Whether this scene has been initialized
     */
    private boolean initialized = false;
    /**
     * The ID number of this scene set by the application framework
     */
    private int ID = 0;

    /**
     * External initialization function for this scene intended to only be called by the application framework when
     * adding it to the app, assigns the scene's ID and calls its internal initialization function
     * @param ID The ID number assigned by the application framework
     * @return Whether this scene was initialized successfully
     */
    public boolean initializeScene(int ID) {
        if (initialized) {
            return false;
        }
        this.ID = ID;
        initialized = initialize();
        return initialized;
    }

    /**
     * Initialize this scene's memory
     * @return Whether this scene was initialized successfully
     */
    protected abstract boolean initialize();

    /**
     * Enter this scene from another scene in the application framework
     * @param lastScene Reference to the previous scene displayed by the application framework
     * @return Whether this scene was entered successfully
     */
    public abstract boolean enter(Scene lastScene);

    /**
     * Process user input to this scene and decide whether to continue running the application
     * @return Whether the application should continue running
     */
    public abstract boolean processInput();

    /**
     * Leave this scene for another scene in the application framework
     * @param nextScene Reference to the next scene to be displayed by the application framework
     * @return Whether this scene was left successfully
     */
    public abstract boolean leave(Scene nextScene);

    /**
     * External destruction function for this scene intended to be called only by the application framework when
     * removing it from the app, frees the scene's ID and calls its internal destroy function
     * @return Whether this scene was destroyed safely
     */
    public boolean destroyScene() {
        if (!initialized) {
            return false;
        }
        boolean success = destroy();
        ID = 0;
        initialized = false;
        return success;
    }

    /**
     * Free this scene's memory
     * @return Whether this scene was destroyed successfully
     */
    protected abstract boolean destroy();

    /**
     * Test whether this scene has been initialized by its external initialization function
     * @return Whether this scene has been initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the ID number assigned by the application framework for this scene
     * @return This scene's ID number
     */
    public int getID() {
        return ID;
    }

    /**
     * Test whether this scene has the same ID as another instance
     * @param o The object to compare this scene to (must be an instance of Scene)
     * @return Whether o has the same scene ID as this one
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Scene s)) {
            return false;
        }
        return ID == s.ID;
    }

}

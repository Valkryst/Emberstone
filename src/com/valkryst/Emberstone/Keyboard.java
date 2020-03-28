package com.valkryst.Emberstone;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Keyboard implements KeyListener {
    private final static Keyboard INSTANCE = new Keyboard();

    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    /** Constructs a new Keyboard. */
    private Keyboard() {}

    @Override
    public void keyTyped(final KeyEvent e) {}

    @Override
    public void keyPressed(final KeyEvent e) {
        keyStates.put(e.getExtendedKeyCode(), true);
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        keyStates.put(e.getExtendedKeyCode(), false);
    }

    /** Sets all keys to the released state. */
    public void resetStates() {
        keyStates.clear();
    }

    /**
     * Determines whether a specific key is currently pressed.
     *
     * @param extendedKeyCode
     *          The extended key code of the key to check.
     *
     * @return
     *          Whether the key is currently pressed.
     */
    public boolean isPressed(final int extendedKeyCode) {
        if (!keyStates.containsKey(extendedKeyCode)) {
            return false;
        }

        return keyStates.get(extendedKeyCode);
    }

    /**
     * Determines whether a specific key is currently released.
     *
     * @param extendedKeyCode
     *          The extended key code of the key to check.
     *
     * @return
     *          Whether the key is currently released.
     */
    public boolean isReleased(final int extendedKeyCode) {
        return isPressed(extendedKeyCode) == false;
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Keyboard getInstance() {
        return INSTANCE;
    }
}

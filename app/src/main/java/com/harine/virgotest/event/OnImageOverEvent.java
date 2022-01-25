package com.harine.virgotest.event;

/**
 * @author nepalese on 2021/4/1 16:26
 * @usage
 */
public class OnImageOverEvent {
    private final int screen;

    public OnImageOverEvent(int screen) {
        this.screen = screen;
    }

    public int getScreen() {
        return screen;
    }
}

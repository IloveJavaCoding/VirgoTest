package com.harine.virgotest.event;

/**
 * @author nepalese on 2021/4/1 16:27
 * @usage
 */
public class OnVideoOverEvent {
    private final int screen;

    public OnVideoOverEvent(int screen) {
        this.screen = screen;
    }

    public int getScreen() {
        return screen;
    }
}

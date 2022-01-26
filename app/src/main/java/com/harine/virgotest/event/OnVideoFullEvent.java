package com.harine.virgotest.event;

/**
 * @author nepalese on 2021/3/29 16:54
 * @usage
 */
public class OnVideoFullEvent {
    private final boolean isFull;
    private final String index;//控件索引
    private final int screen;

    public OnVideoFullEvent(boolean isFull, String index, int screen) {
        this.isFull = isFull;
        this.index = index;
        this.screen = screen;
    }

    public boolean isFull() {
        return isFull;
    }

    public String getIndex() {
        return index;
    }

    public int getScreen() {
        return screen;
    }
}

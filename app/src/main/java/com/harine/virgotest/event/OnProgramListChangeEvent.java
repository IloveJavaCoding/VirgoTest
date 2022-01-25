package com.harine.virgotest.event;

/**
 * @author nepalese on 2021/3/31 13:42
 * @usage
 */
public class OnProgramListChangeEvent {
    private final String curPid;
    private final int screen;

    public OnProgramListChangeEvent(String curPid, int screen) {
        this.curPid = curPid;
        this.screen = screen;
    }

    public String getCurPid() {
        return curPid;
    }

    public int getScreen() {
        return screen;
    }
}

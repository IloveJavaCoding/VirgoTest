package com.harine.virgotest.bean;

import com.harine.virgotest.util.TempUtil;

/**
 * @author nepalese on 2021/2/26 17:44
 * @usage
 */
public class VirgoPoint2 {
    private float x;
    private float y;
    private final int offset;//分36等分，每份10度

    public VirgoPoint2(int x, int y) {
        this.x = x;
        this.y = y;
        this.offset = TempUtil.getRandomInt(1,36);
    }

    public int getOffset() {
        return offset;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

package com.harine.virgotest.bean;

import com.google.android.material.internal.FlowLayout;

/**
 * @author nepalese on 2021/5/11 14:51
 * @usage
 */
public class VirgoPointf {
    private float x,y;

    public VirgoPointf() {
    }

    public VirgoPointf(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public VirgoPointf(VirgoPoint point) {
        this.x = point.getX();
        this.y = point.getY();
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

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }
}

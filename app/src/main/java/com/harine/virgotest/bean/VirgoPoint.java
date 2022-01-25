package com.harine.virgotest.bean;

/**
 * @author nepalese on 2021/2/24 15:47
 * @usage
 */
public class VirgoPoint {
    private int x,y;

    public VirgoPoint() {
    }

    public VirgoPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public VirgoPoint(VirgoPoint point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
}

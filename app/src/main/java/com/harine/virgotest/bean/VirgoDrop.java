package com.harine.virgotest.bean;

import com.nepalese.virgosdk.Util.MathUtil;

/**
 * @author nepalese on 2021/5/24 09:53
 * @usage
 */
public class VirgoDrop {
    private final char[] CHARACTERS = {'a','c','e','m','n','o','r','s','u','v','w','x','z'};
    private final char[] chars = new char[12];
    private float startX;
    private float startY;
    private float moveTime;//运动时间

    public VirgoDrop(float startX, float startY) {
        generateDop();
        this.startX = startX;
        this.startY = startY;
        this.moveTime = 0f;
    }

    public void generateDop(){
        for(int i=0; i<chars.length; i++){
            chars[i] = CHARACTERS[MathUtil.getRandomInt(0,12)];
        }
    }

    public String getContent(){
        return new String(chars);//Arrays.toString(chars);
    }

    public void setXY(float x, float y){
        this.startX = x;
        this.startY = y;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(float moveTime) {
        this.moveTime = moveTime;
    }
}

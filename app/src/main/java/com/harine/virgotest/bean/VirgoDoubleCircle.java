package com.harine.virgotest.bean;

import com.nepalese.virgosdk.Util.MathUtil;

/**
 * @author nepalese on 2021/3/8 17:03
 * @usage
 */
public class VirgoDoubleCircle {
    private float rBig;//大圆半径 逐渐变大
    private final int rSmall;//小圆半径, 随机生成大小，不改变
    private int degree;//旋转角度

    public VirgoDoubleCircle(float rBig, int rSmall, int degree) {
        this.rBig = rBig;
        this.degree = degree;
        if(rSmall<=3){
            this.rSmall = MathUtil.getRandomInt(rSmall,rSmall+5);
        }else{
            this.rSmall = MathUtil.getRandomInt(rSmall-2,rSmall+3);
        }
    }

    public float getrBig() {
        return rBig;
    }

    public void setrBig(float rBig) {
        this.rBig = rBig;
    }

    public float getrSmall() {
        return rSmall;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree%360;
    }
}
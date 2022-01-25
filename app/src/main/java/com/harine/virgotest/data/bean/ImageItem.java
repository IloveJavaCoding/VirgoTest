package com.harine.virgotest.data.bean;

import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author nepalese on 2021/3/26 15:42
 * @usage 图片素材排期表
 */
@Entity
public class ImageItem extends BaseBean {
    @Id(autoincrement = true)
    private Long id;

    private String pId;//节目id
    private String resId;//素材id
    private String areaId;//模板id: 排期使用

    private String path;//本地路径
    private String startTime;//开始时间
    private String endTime;//结束时间

    private int duration;//播放时长：循环,垫片切换
    private int times;//循环节目播放 次数：-1为无限
    private boolean isFull;//全屏播放？


    @Generated(hash = 223449191)
    public ImageItem(Long id, String pId, String resId, String areaId, String path,
            String startTime, String endTime, int duration, int times,
            boolean isFull) {
        this.id = id;
        this.pId = pId;
        this.resId = resId;
        this.areaId = areaId;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.times = times;
        this.isFull = isFull;
    }
    @Generated(hash = 1053804574)
    public ImageItem() {
    }

    
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPId() {
        return this.pId;
    }
    public void setPId(String pId) {
        this.pId = pId;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public boolean getIsFull() {
        return this.isFull;
    }
    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }
    public String getResId() {
        return this.resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getTimes() {
        return this.times;
    }
    public void setTimes(int times) {
        this.times = times;
    }
    public String getAreaId() {
        return this.areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}

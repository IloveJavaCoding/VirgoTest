package com.harine.virgotest.data.bean;

import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Comparator;

/**
 * @author nepalese on 2021/3/31 16:39
 * @usage
 */
@Entity
public class TodayProgram extends BaseBean {
    @Id(autoincrement = true)
    private Long id;
    
    private String pid;
    private String startTime;
    private String endTime;
    private int type;
    //主副屏
    private int screen;
    
    @Generated(hash = 1427338091)
    public TodayProgram(Long id, String pid, String startTime, String endTime,
            int type, int screen) {
        this.id = id;
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.screen = screen;
    }
    @Generated(hash = 885359855)
    public TodayProgram() {
    }

    public TodayProgram(TodayProgram todayProgram){
        pid = todayProgram.getPid();
        type = todayProgram.getType();
        startTime = todayProgram.getStartTime();
        endTime = todayProgram.getEndTime();
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPid() {
        return this.pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getScreen() {
        return this.screen;
    }
    public void setScreen(int screen) {
        this.screen = screen;
    }

}

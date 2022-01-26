package com.harine.virgotest.data.bean;

import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

/**
 * @author nepalese on 2021/3/30 11:45
 * @usage 节目类
 */
@Entity
public class Program extends BaseBean {
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String pId;//节目id：唯一
    private Date startDate;
    private Date endDate;
    //定时
    private String startTime; //节目开始时间 00:00:00
    private String endTime; //节目结束时间 24:00:00

    //定点
    private Double programStartLat;
    private Double programStartLng;
    private Double programEndLat;
    private Double programEndLng;

    private int pType;//节目类型
    //循环
    private int pTimes;//播放次数

    //主副屏
    private int screen;

@Generated(hash = 1691591428)
    public Program(Long id, String pId, Date startDate, Date endDate,
            String startTime, String endTime, Double programStartLat,
            Double programStartLng, Double programEndLat, Double programEndLng,
            int pType, int pTimes, int screen) {
        this.id = id;
        this.pId = pId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.programStartLat = programStartLat;
        this.programStartLng = programStartLng;
        this.programEndLat = programEndLat;
        this.programEndLng = programEndLng;
        this.pType = pType;
        this.pTimes = pTimes;
        this.screen = screen;
    }
    @Generated(hash = 775603163)
    public Program() {
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
    public Double getProgramStartLat() {
        return this.programStartLat;
    }
    public void setProgramStartLat(Double programStartLat) {
        this.programStartLat = programStartLat;
    }
    public Double getProgramStartLng() {
        return this.programStartLng;
    }
    public void setProgramStartLng(Double programStartLng) {
        this.programStartLng = programStartLng;
    }
    public Double getProgramEndLat() {
        return this.programEndLat;
    }
    public void setProgramEndLat(Double programEndLat) {
        this.programEndLat = programEndLat;
    }
    public Double getProgramEndLng() {
        return this.programEndLng;
    }
    public void setProgramEndLng(Double programEndLng) {
        this.programEndLng = programEndLng;
    }
    public int getPType() {
        return this.pType;
    }
    public void setPType(int pType) {
        this.pType = pType;
    }
    public int getPTimes() {
        return this.pTimes;
    }
    public void setPTimes(int pTimes) {
        this.pTimes = pTimes;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
//    public String getTemplateId() {
//        return this.templateId;
//    }
//    public void setTemplateId(String templateId) {
//        this.templateId = templateId;
//    }
    public int getScreen() {
        return this.screen;
    }
    public void setScreen(int screen) {
        this.screen = screen;
    }
}

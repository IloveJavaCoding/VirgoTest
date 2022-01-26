package com.harine.virgotest.data.bean;

import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

/**
 * @author nepalese on 2021/4/1 15:51
 * @usage
 */
@Entity
public class PlayLog extends BaseBean {
    @Id(autoincrement = true)
    private Long id;

    private String pId;//节目id
    private String resId;//素材id
    private String areaId;//模板id: 区分多个同控件

    private Date today;//开始记录的日期：与今日不同则重置
    private int playTimes;//已播放次数
    private boolean isProgram;//节目？


    @Generated(hash = 783824051)
    public PlayLog(Long id, String pId, String resId, String areaId, Date today,
            int playTimes, boolean isProgram) {
        this.id = id;
        this.pId = pId;
        this.resId = resId;
        this.areaId = areaId;
        this.today = today;
        this.playTimes = playTimes;
        this.isProgram = isProgram;
    }
    @Generated(hash = 891504677)
    public PlayLog() {
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
    public String getResId() {
        return this.resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public int getPlayTimes() {
        return this.playTimes;
    }
    public void setPlayTimes(int playTimes) {
        this.playTimes = playTimes;
    }
    public boolean getIsProgram() {
        return this.isProgram;
    }
    public void setIsProgram(boolean isProgram) {
        this.isProgram = isProgram;
    }
    public Date getToday() {
        return this.today;
    }
    public void setToday(Date today) {
        this.today = today;
    }
    public String getAreaId() {
        return this.areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

}

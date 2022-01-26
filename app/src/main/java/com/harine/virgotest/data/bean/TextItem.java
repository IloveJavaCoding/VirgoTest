package com.harine.virgotest.data.bean;

import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author nepalese on 2021/3/31 10:43
 * @usage
 */
@Entity
public class TextItem extends BaseBean {
    @Id(autoincrement = true)
    private Long id;

    private String pId;//节目id
    private String resId;//素材id
    private String content;//文本内容

    //定时
    private String startTime;//开始时间
    private String endTime;//结束时间

    //循环
    private int duration;//时长


    @Generated(hash = 1882862426)
    public TextItem(Long id, String pId, String resId, String content,
            String startTime, String endTime, int duration) {
        this.id = id;
        this.pId = pId;
        this.resId = resId;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }
    @Generated(hash = 1321933503)
    public TextItem() {
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
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
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
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}

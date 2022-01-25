package com.harine.virgotest.data.bean;

import com.harine.virgotest.Constants;
import com.harine.virgotest.bean.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Comparator;

/**
 * @author nepalese on 2021/3/30 16:35
 * @usage 布局类
 */
@Entity
public class AreaItem extends BaseBean {
    @Id(autoincrement = true)
    private Long itemId;

    private String pId;//节目id
    @Unique
    private String areaId;
    private String areaType;// type="Video"
    private String areaIndex;// 同类模板索引
    private float areaLeft;// left="0,0%" 左坐标
    private float areaTop;// top="0,0%" 上坐标
    private float areaWidth;// width="960,50%" 宽
    private float areaHeigh;// height="1080,100%" 高
//    private String templateId;//模板id：与节目一一对应

    public static Comparator<AreaItem> comparator = new Comparator<AreaItem>() {
        @Override
        public int compare(AreaItem b1, AreaItem b2) {
            if (Constants.TYPE_LOGO.equals(b1.getAreaType())) {
                return 1;
            }
            if (Constants.TYPE_LOGO.equals(b2.getAreaType())) {
                return -1;
            }
            return 0;
        }
    };

    @Generated(hash = 1081121901)
    public AreaItem(Long itemId, String pId, String areaId, String areaType,
            String areaIndex, float areaLeft, float areaTop, float areaWidth,
            float areaHeigh) {
        this.itemId = itemId;
        this.pId = pId;
        this.areaId = areaId;
        this.areaType = areaType;
        this.areaIndex = areaIndex;
        this.areaLeft = areaLeft;
        this.areaTop = areaTop;
        this.areaWidth = areaWidth;
        this.areaHeigh = areaHeigh;
    }


    @Generated(hash = 1320214499)
    public AreaItem() {
    }


    public Long getItemId() {
        return this.itemId;
    }


    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }


    public String getAreaId() {
        return this.areaId;
    }


    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }


    public String getAreaType() {
        return this.areaType;
    }


    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }


    public float getAreaLeft() {
        return this.areaLeft;
    }


    public void setAreaLeft(float areaLeft) {
        this.areaLeft = areaLeft;
    }


    public float getAreaTop() {
        return this.areaTop;
    }


    public void setAreaTop(float areaTop) {
        this.areaTop = areaTop;
    }


    public float getAreaWidth() {
        return this.areaWidth;
    }


    public void setAreaWidth(float areaWidth) {
        this.areaWidth = areaWidth;
    }


    public float getAreaHeigh() {
        return this.areaHeigh;
    }


    public void setAreaHeigh(float areaHeigh) {
        this.areaHeigh = areaHeigh;
    }


//    public String getTemplateId() {
//        return this.templateId;
//    }
//
//
//    public void setTemplateId(String templateId) {
//        this.templateId = templateId;
//    }


    public String getAreaIndex() {
        return this.areaIndex;
    }


    public void setAreaIndex(String areaIndex) {
        this.areaIndex = areaIndex;
    }


    public String getPId() {
        return this.pId;
    }


    public void setPId(String pId) {
        this.pId = pId;
    }
}

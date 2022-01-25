package com.harine.virgotest.ui.manager;

import android.util.Log;

import com.harine.virgotest.MyApplication;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.ImageItem;
import com.harine.virgotest.data.bean.PlayLog;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.VideoItem;
import com.harine.virgotest.util.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * @author nepalese on 2021/4/2 14:44
 * @usage
 */
public class FunctionManager {
    private static final String TAG = "FunctionManager";

    // DBHelper.getInstance(MyApplication.getApplication()).getInstance(MyApplication.getApplication())
    //////////////////////////////////////////定次循环播放日志////////////////////////////////////
    //判断节目是否在有效次数内
    public static boolean isValide(Program program){
        int times = program.getPTimes();//有效次数
        if(times==-1){
            //无限播放
            Log.i(TAG, "无限播放: ");
            return true;
        }else{
            resetProgramLog(program.getPId());
            //已播放次数
            int hadPlaytime = DBHelper.getInstance(MyApplication.getApplication()).getProgramPlayLog(program.getPId()).getPlayTimes();
            Log.i(TAG, "已播放次数: " + hadPlaytime);
            return hadPlaytime < times;
        }
    }

    //判断循环节目列表是否已播放完
    public static boolean judgeOver(List<Program> cycle) {
        for(Program program: cycle){
            if(isValide(program)){
                return false;
            }
        }

        Log.i(TAG, "judgeOver: 循环节目已播放完!");
        return true;
    }

    //判断次数循环节目是否播放完一遍
    public static boolean isCycleOver(String pid){
        //节目下所有图片素材
        List<ImageItem> imgList = DBHelper.getInstance(MyApplication.getApplication()).getAllImageItem(pid);
        for(ImageItem item: imgList){
            if(isValide(item)){
                return false;
            }
        }

        //节目下所有视频素材
        List<VideoItem> videoList = DBHelper.getInstance(MyApplication.getApplication()).getAllVideoItem(pid);
        for(VideoItem item: videoList){
            if(isValide(item)){
                return false;
            }
        }

        return true;
    }

    //判断图片是否在有效次数内
    public static boolean isValide(ImageItem item){
        int times = item.getTimes();//有效次数
        if(times==-1){
            //无限播放
            return true;
        }else{
            //已播放次数
            int hadPlaytime = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId()).getPlayTimes();
            return hadPlaytime < times;
        }
    }

    //判断视频是否在有效次数内
    public static boolean isValide(VideoItem item){
        int times = item.getTimes();//有效次数
        if(times==-1){
            //无限播放
            return true;
        }else{
            //已播放次数
            int hadPlaytime = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId()).getPlayTimes();
            return hadPlaytime < times;
        }
    }

    //更新节目日志
    public static void updateProgramPlayLog(String pid){
        Log.i(TAG, "updateProgramPlayLog: ");
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getProgramPlayLog(pid);
        log.setPlayTimes(log.getPlayTimes()+1);
        DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
    }

    //若记录日期小于当前日期，重置
    public static void resetProgramLog(String pid){
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getProgramPlayLog(pid);
        Date lastDay = log.getToday();
        Date today = TimeUtil.getToday();
        if(lastDay.compareTo(today) < 0){//需重置
            Log.i(TAG, "resetProgramLog: ");
            log.setPlayTimes(0);
            log.setToday(today);
            DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
        }
    }

    //定次节目素材日志重置
    public static void resetAllResPlayLog(String pid){
        List<ImageItem> imgList = DBHelper.getInstance(MyApplication.getApplication()).getAllImageItem(pid);
        for(ImageItem item: imgList){
            resetImagePlayLog(item);
        }

        List<VideoItem> videoList = DBHelper.getInstance(MyApplication.getApplication()).getAllVideoItem(pid);
        for(VideoItem item: videoList){
            resetVideoPlayLog(item);
        }
    }

    //重置图片素材日志
    public static void resetImagePlayLog(ImageItem item){
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId());
        log.setPlayTimes(0);
        DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
    }

    //更新图片素材日志
    public static void updateImagePlayLog(ImageItem item){
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId());
        log.setPlayTimes(log.getPlayTimes()+1);
        DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
    }

    //重置视频素材日志
    public static void resetVideoPlayLog(VideoItem item){
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId());
        log.setPlayTimes(0);
        DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
    }

    //更新视频素材日志
    public static void updateVideoPlayLog(VideoItem item){
        PlayLog log = DBHelper.getInstance(MyApplication.getApplication()).getResPlayLog(item.getPId(), item.getResId(), item.getAreaId());
        log.setPlayTimes(log.getPlayTimes()+1);
        DBHelper.getInstance(MyApplication.getApplication()).updatePlayLog(log);
    }
}

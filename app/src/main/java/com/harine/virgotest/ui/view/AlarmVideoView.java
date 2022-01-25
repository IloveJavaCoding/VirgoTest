package com.harine.virgotest.ui.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.harine.virgotest.Constants;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.VideoItem;
import com.harine.virgotest.event.OnVideoFullEvent;
import com.harine.virgotest.event.OnVideoOverEvent;
import com.harine.virgotest.ui.manager.FunctionManager;
import com.harine.virgotest.util.TimeUtil;
import com.nepalese.virgosdk.Util.ScreenUtil;
import com.nepalese.virgosdk.VirgoView.VideoView.VirgoVideoViewSurface;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/3/27 09:48
 * @usage
 */
public class AlarmVideoView extends VirgoVideoViewSurface {
    private static final String TAG = "AlarmVideoView";
    private static final String DEFAULT_VIDEO = "http://117.25.163.26:9990/cdmsa/2021/03/18/eb425751636b0682.mp4";

    private static final int PLAY_MODE_DP = 1;//垫片
    private static final int PLAY_MODE_LOOP = 2;//循环
    private static final int PLAY_MODE_TIMGING = 3;//定时

    private Context mContext;
    private DBHelper dbHelper;
    private RectF mRectF;

    private Program mProgram;//当前节目
    private List<VideoItem> itemList;
    private List<String> timeList;
    private String mAreaId;//布局id
    private String mIndex;//控件索引

    private int fullWidth, fullHeight;
    private int dpIndex;//垫片循环索引
    private int cyIndex;//循环播放索引
    private int playMode;//
    private int mScreen;//主副屏节目

    private String curPath;//非垫片节目当前视频路径
    private boolean isRegister = false;
    private boolean isFull = false;
    private boolean hasPause = false;

    public AlarmVideoView(Context context, String index, int mScreen) {
        super(context);
        this.mContext = context;
        this.mIndex = index;
        this.mScreen = mScreen;
        dbHelper = DBHelper.getInstance(mContext);

        registerReceiver();
        init();
    }

    public AlarmVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void setLayout() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) mRectF.left;
        lp.topMargin = (int) mRectF.top;
        lp.width = (int) mRectF.right;
        lp.height = (int) mRectF.bottom;

        isFull = false;
        this.setLayoutParams(lp);
    }

    private void setFull(){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = 0;
        lp.topMargin = 0;
        lp.width = fullWidth;
        lp.height = fullHeight;

        isFull = true;
        this.setLayoutParams(lp);
        this.bringToFront();
        postEvent();
    }

    private void init() {
        fullWidth = ScreenUtil.getScreenWidth(mContext);
        fullHeight = ScreenUtil.getScreenHeight(mContext);

        itemList = new ArrayList<>();
        timeList = new ArrayList<>();

        this.dpIndex = 0;
        this.cyIndex = 0;
        this.playMode = -1;

        this.setLooping(false);
        this.setCompletionListener((mediaPlayer) -> {
            Log.i(TAG, mScreen + " onCompletion: complete:");
            if(playMode==PLAY_MODE_DP){
                playDefaultVideo();
            }else if(playMode==PLAY_MODE_TIMGING){
                //自己循环播放
                play(curPath);
            }else if(playMode==PLAY_MODE_LOOP){
                playVideo(getCycleItem());
            }
        });
    }

    private void getAreaId(){
        this.mAreaId = dbHelper.getAreaId(mProgram.getPId(), Constants.TYPE_VIDEO, mIndex);
        Log.i(TAG, mScreen + " getAreaId: " + mAreaId);
    }

    private void getVideoItems(){
        itemList.clear();
        itemList.addAll(dbHelper.getVideoItemPid(mProgram.getPId(), mAreaId));
    }

    //所有开始,结束时间
    private void getTimeList() {
        List<String> list = new ArrayList<>();
        for (VideoItem item : itemList) {
            list.add(TimeUtil.formatTime(item.getStartTime()));
            list.add(TimeUtil.formatTime(item.getEndTime()));
        }
        timeList.clear();
        timeList.addAll(list);
    }

    private void setAlarm(){
        if(timeList.isEmpty()){
            Log.i(TAG, mScreen + " setAlarm: empty");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constants.ACTION_UPDATE_TIME_LIST_VIDEO);
        long now = TimeUtil.getCurTimeTime();
        for(String time: timeList){
            long alarmTime = TimeUtil.string2LongTime(time, TimeUtil.DATE_FORMAT_TIME);

            if (alarmTime - now <= 0) {
                //已过或当前素材，不需要定时
                continue;
            }

            alarmTime = TimeUtil.getCurTime() + alarmTime - now;

            Log.i(TAG, mScreen + " setAlarm: " + alarmTime);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                    Constants.ALARM_REQUEST_CODE_VIDEO, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if(alarmManager!=null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                }
            }

            return;
        }
    }

    private void playVideo(VideoItem curItem) {
        if(curItem==null){
            //当前节目无素材播放
            playDianPian();
        }else{
            judgeFull(curItem);
            curPath = curItem.getPath();
            play(curPath);
            if(isCyclePlay()){
                playMode = PLAY_MODE_LOOP;
                cyIndex++;
            }else{
                playMode = PLAY_MODE_TIMGING;
            }
        }
    }

    private void playDianPian(){
        playMode = PLAY_MODE_DP;
        playDefaultVideo();
    }

    //播放垫片节目
    private void playDefaultVideo(){
        Log.i(TAG, mScreen + " playVideo: 垫片" + dpIndex);
        //获取垫片节目内所有视频素材
        List<VideoItem> list = dbHelper.getVideoItemDP();
        if(list.isEmpty()){
            //无垫片
            exitFull();
            play(DEFAULT_VIDEO);
        }else{
            if(dpIndex>=list.size()){
                dpIndex = 0;
            }
            judgeFull(list.get(dpIndex));
            play(list.get(dpIndex).getPath());
            dpIndex++;
        }
    }

    private void judgeFull(VideoItem item){
        if(item.getIsFull()){
            if(!isFull){
                setFull();
            }
        }else{
            exitFull();
        }
    }

    private void exitFull(){
        if(isFull){
            setLayout();
            postEvent();
        }
    }

    private void play(String path){
        File file = new File(path);
        if (file.exists()) {
            this.setVideoUri(Uri.fromFile(file));
        } else {
            Log.i(TAG, mScreen + " 播放在线视频");
            this.setVideoPath(path);
        }

        this.start();
    }

    //定时
    private VideoItem getCurItem() {
        long now = TimeUtil.getCurTimeTime();
        for(VideoItem item: itemList){
            if(now>=TimeUtil.string2LongTime(item.getStartTime(), TimeUtil.DATE_FORMAT_TIME) &&
                    now <TimeUtil.string2LongTime(item.getEndTime(), TimeUtil.DATE_FORMAT_TIME)){
                Log.i(TAG, mScreen + " getCurItem: " + item.getResId());
                return item;
            }
        }
        return null;
    }

    //循环
    private VideoItem getCycleItem(){
        if(cyIndex>=itemList.size()){
            cyIndex = 0;
        }
        VideoItem item = itemList.get(cyIndex);
        //判断其剩余播放次数
        if(FunctionManager.isValide(item)){
            FunctionManager.updateVideoPlayLog(item);
            Log.i(TAG, mScreen + " play cycle video: " + cyIndex);
            return item;
        }else{
            //该素材不能播放，播放下一个
            cyIndex ++;
            if(judgeOver()){
                //已全部播放完
                return null;
            }else{
                return getCycleItem();
            }
        }
    }

    //该节目内素材是否已播放完
    private boolean judgeOver() {
        for(VideoItem item: itemList){
            if(FunctionManager.isValide(item)){
                return false;
            }
        }

        EventBus.getDefault().post(new OnVideoOverEvent(mScreen));
        Log.i(TAG, mScreen + " judgeOver: 素材已播放完!");
        return true;
    }

    ///////////////////////////////////////////定时广播/////////////////////////////////////////////
    private void registerReceiver() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_UPDATE_TIME_LIST_VIDEO);
            mContext.registerReceiver(updateItem, filter);
        }
    }

    public void unRegisterReceiver() {
        if (isRegister) {
            isRegister = false;
            mContext.unregisterReceiver(updateItem);
        }
    }

    private final BroadcastReceiver updateItem = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, mScreen + " onReceive: update video");
            String action = intent.getAction();
            if (Constants.ACTION_UPDATE_TIME_LIST_VIDEO.equals(action)) {
                startPlay();
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(mContext,
                Constants.ALARM_REQUEST_CODE_VIDEO,
                new Intent(Constants.ACTION_UPDATE_TIME_LIST_VIDEO),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAlarm();
        unRegisterReceiver();
        this.stopPlay();
        this.clearFocus();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            setPause();
        } else {
            continuePlay();
        }
    }

    private boolean isCyclePlay(){
        return mProgram.getPType() == Constants.PROGRAM_TYPE_CYCLE || mProgram.getPType() == Constants.PROGRAM_TYPE_LOCATION;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void postEvent(){
        EventBus.getDefault().post(new OnVideoFullEvent(isFull, mIndex, mScreen));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setPause(){
        if(this.isPlaying()){
            Log.i(TAG, mScreen + " setPause: ");
            this.hasPause = true;
            this.pause();
        }
    }

    public void continuePlay() {
        if (this.hasPause) {
            Log.i(TAG, mScreen + " continuePlay: ");
            this.hasPause = false;
            this.start();
        }
    }

    public void setProgram(Program program) {
        this.mProgram = program;
        if(mProgram.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            dpIndex = 0;
            return;
        }

        getAreaId();
        getVideoItems();
        if(isCyclePlay()){
            //循环播放
            Log.i(TAG, mScreen + " setProgram: 循环播放");
            timeList.clear();
            cyIndex = 0;
        }else{
            getTimeList();
        }
    }

    public void setmRectF(RectF mRectF) {
        this.mRectF = mRectF;
        setLayout();
    }

    public void startPlay(){
        if(mProgram.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            cancelAlarm();
            playDianPian();
            return;
        }

        if(isCyclePlay()){
            playVideo(getCycleItem());
        }else{
            setAlarm();
            playVideo(getCurItem());
        }
    }
}

package com.harine.virgotest.ui.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.harine.virgotest.Constants;
import com.harine.virgotest.R;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.ImageItem;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.event.OnImageFullEvent;
import com.harine.virgotest.event.OnImageOverEvent;
import com.harine.virgotest.ui.manager.FunctionManager;
import com.harine.virgotest.util.TimeUtil;
import com.nepalese.virgosdk.Util.ScreenUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2021/3/30 08:50
 * @usage
 */
public class AlarmImageView2 extends RelativeLayout {
    private static final String TAG = "AlarmImageView2";

    private static final int MSG_NEXT_DP = 1;//下一张垫片
    private static final int MSG_NEXT_CYCLE = 2;//下一张循环
    private static final int MSG_NEXT_IN_ABOVE = 11;//下一张图片进入
    private static final int MSG_NEXT_IN_BACK = 12;//下一张图片进入

    private static final String DEFAULT_IMAGE = "http://pic1.win4000.com/wallpaper/f/52aa7ef301c0a.jpg";
    private static final int PLAY_MODE_DP = 1;//垫片
    private static final int PLAY_MODE_LOOP = 2;//循环
    private static final int PLAY_MODE_TIMGING = 3;//定时

    private Context mContext;
    private DBHelper dbHelper;
    private RectF mRectF;
    private DrawableTransitionOptions transitionOptions;

    private ImageView imgAbove, imgBack;
    private Animation inAnim; // 进入动画
    private Animation outAnim; // 出去动画

    private Program mProgram;//当前节目
    private List<ImageItem> itemList;
    private List<String> timeList;

    private String mAreaId;//布局id
    private String mIndex;//控件索引

    private int fullWidth, fullHeight;
    private int dpIndex;//垫片循环索引
    private int cyIndex;//循环播放索引
    private int playMode;//
    private int mScreen;//主副屏节目
    private boolean isRegister = false;
    private boolean isFull = false;
    private boolean isAbove = false;//记录目前使用的图片容器

    public AlarmImageView2(Context context, String index, int mScreen) {
        super(context);
        this.mContext = context;
        this.mIndex = index;
        this.mScreen = mScreen;
        dbHelper = DBHelper.getInstance(mContext);

        registerReceiver();
        init();
    }

    public AlarmImageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        initUI();
        initData();
    }

    private void initUI() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootview = inflater.inflate(R.layout.layout_alarm_image_view, this, true);
        imgAbove = rootview.findViewById(R.id.imgAbove);
        imgBack = rootview.findViewById(R.id.imgBack);
    }

    private void initData() {
        fullWidth = ScreenUtil.getScreenWidth(mContext);
        fullHeight = ScreenUtil.getScreenHeight(mContext);

        transitionOptions = new DrawableTransitionOptions().dontTransition();
        itemList = new ArrayList<>();
        timeList = new ArrayList<>();

        // 默认进出场动画
        inAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_in);
        outAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_out);

        dpIndex = 0;
        cyIndex = 0;
        playMode = -1;
    }

    private void setLayout() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) mRectF.left;
        lp.topMargin = (int) mRectF.top;
        lp.width = (int) mRectF.right;
        lp.height = (int) mRectF.bottom;

        isFull = false;
        this.setLayoutParams(lp);
    }

    private void setFull(){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = 0;
        lp.topMargin = 0;
        lp.width = fullWidth;
        lp.height = fullHeight;

        isFull = true;
        this.setLayoutParams(lp);
        this.bringToFront();
        postEvent();
    }

    private void getAreaId(){
        this.mAreaId = dbHelper.getAreaId(mProgram.getPId(), Constants.TYPE_IMP, mIndex);
        Log.i(TAG, mScreen + " getAreaId: " + mAreaId);
    }

    private void getImageItems(){
        itemList.clear();
        itemList.addAll(dbHelper.getImageItemPid(mProgram.getPId(), mAreaId));
    }

    //所有开始,结束时间
    private void getTimeList() {
        List<String> list = new ArrayList<>();
        for (ImageItem item : itemList) {
            list.add(TimeUtil.formatTime(item.getStartTime()));
            list.add(TimeUtil.formatTime(item.getEndTime()));
        }
        timeList.clear();
        timeList.addAll(list);
    }

    private void setAlarm(){
        if(timeList.isEmpty()){
            return;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constants.ACTION_UPDATE_TIME_LIST_IMAGE);
        long now = TimeUtil.getCurTimeTime();
        for(String time: timeList){
            long alarmTime = TimeUtil.string2LongTime(time, TimeUtil.DATE_FORMAT_TIME);

            if (alarmTime - now <= 0) {
                //已过或当前素材，不需要定时
                continue;
            }

            alarmTime = TimeUtil.getCurTime() + alarmTime - now;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, Constants.ALARM_REQUEST_CODE_IMAGE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    private void playImage(ImageItem curItem) {
        if(curItem==null){
            //当前节目无素材播放
            playDianPian();
        }else{
            //防止冲突
            removeMessage();
            judgeFull(curItem);
            loadImage(curItem.getPath());

            if(isCyclePlay()){
                playMode = PLAY_MODE_LOOP;
                handler.sendEmptyMessageDelayed(MSG_NEXT_CYCLE, curItem.getDuration()*1000L);
                cyIndex++;
            }else{
                playMode = PLAY_MODE_TIMGING;
            }
        }
    }

    private void playDianPian(){
        playMode = PLAY_MODE_DP;
        playDefaultItem();
    }

    private void loadImage(String path){
        if(!isAbove){
            //上层
            isAbove = true;
            imgBack.startAnimation(outAnim);
            Glide.with(mContext).load(path).transition(transitionOptions).into(imgAbove);
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_ABOVE, outAnim.getDuration());
        }else{
            //下层
            isAbove = false;
            imgAbove.startAnimation(outAnim);
            Glide.with(mContext).load(path).transition(transitionOptions).into(imgBack);
            handler.sendEmptyMessageDelayed(MSG_NEXT_IN_BACK, outAnim.getDuration());
        }
    }

    //播放垫片节目
    private void playDefaultItem(){
        Log.i(TAG, mScreen + " playImage: 垫片" + dpIndex);
        //获取垫片节目内所有图片素材
        List<ImageItem> list = dbHelper.getImageItemDP();
        if(list.isEmpty()){
            //无垫片
            exitFull();
            loadImage(DEFAULT_IMAGE);
        }else{
            if(dpIndex>=list.size()){
                dpIndex = 0;
            }
            judgeFull(list.get(dpIndex));
            loadImage(list.get(dpIndex).getPath());

            if(list.size()>1){//垫片数大于1才轮播
                handler.sendEmptyMessageDelayed(MSG_NEXT_DP, list.get(dpIndex).getDuration()*1000L);
                dpIndex++;
            }
        }
    }

    private void judgeFull(ImageItem item){
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

    private ImageItem getCurItem() {
        long now = TimeUtil.getCurTimeTime();
        for(ImageItem item: itemList){
            if(now>=TimeUtil.string2LongTime(item.getStartTime(), TimeUtil.DATE_FORMAT_TIME) &&
                    now <TimeUtil.string2LongTime(item.getEndTime(), TimeUtil.DATE_FORMAT_TIME)){
                Log.i(TAG, mScreen + " getCurItem: " + item.getResId());
                return item;
            }
        }
        return null;
    }

    private ImageItem getCycleItem(){
        if(cyIndex>=itemList.size()){
            cyIndex = 0;
        }
        ImageItem item = itemList.get(cyIndex);
        //判断其剩余播放次数
        if(FunctionManager.isValide(item)){
            FunctionManager.updateImagePlayLog(item);
            Log.i(TAG, mScreen + " play image cycle: " + cyIndex);
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
        for(ImageItem item: itemList){
            if(FunctionManager.isValide(item)){
                return false;
            }
        }

        EventBus.getDefault().post(new OnImageOverEvent(mScreen));
        Log.i(TAG, mScreen + " judgeOver: 素材已播放完!");
        return true;
    }

    ///////////////////////////////////////////定时广播/////////////////////////////////////////////
    private void registerReceiver() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_UPDATE_TIME_LIST_IMAGE);
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
            Log.i(TAG, mScreen + " onReceive: update image");
            String action = intent.getAction();
            if (Constants.ACTION_UPDATE_TIME_LIST_IMAGE.equals(action)) {
                startPlay();
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeMessage();
        cancelAlarm();
        unRegisterReceiver();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            removeMessage();
        } else {
            wakeUp();
        }
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(mContext,
                Constants.ALARM_REQUEST_CODE_IMAGE,
                new Intent(Constants.ACTION_UPDATE_TIME_LIST_IMAGE),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    //////////////////////////////////////////handler///////////////////////////////////////////////
    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NEXT_DP:
                    playDefaultItem();
                    break;
                case MSG_NEXT_CYCLE:
                    playImage(getCycleItem());
                    break;
                case MSG_NEXT_IN_ABOVE:
                    playAbove();
                    break;
                case MSG_NEXT_IN_BACK:
                    playBack();
                    break;
            }
        }
    };

    private void playAbove(){
        imgBack.setVisibility(INVISIBLE);
        imgAbove.setVisibility(VISIBLE);
//        imgAbove.startAnimation(inAnim);
    }

    private void playBack(){
        imgAbove.setVisibility(INVISIBLE);
        imgBack.setVisibility(VISIBLE);
//        imgBack.startAnimation(inAnim);
    }

    private boolean isCyclePlay(){
        return mProgram.getPType() == Constants.PROGRAM_TYPE_CYCLE || mProgram.getPType() == Constants.PROGRAM_TYPE_LOCATION;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void postEvent(){
        EventBus.getDefault().post(new OnImageFullEvent(isFull, mIndex, mScreen));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setProgram(Program program) {
        this.mProgram = program;
        if(program.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            dpIndex = 0;
            return;
        }

        getAreaId();
        getImageItems();
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
        removeMessage();
        if(mProgram.getPType()==Constants.PROGRAM_TYPE_DIANPIAN){
            cancelAlarm();
            playDianPian();
            return;
        }

        if(isCyclePlay()){
            playImage(getCycleItem());
        }else{
            setAlarm();
            playImage(getCurItem());
        }
    }

    public void removeMessage(){
        handler.removeMessages(MSG_NEXT_DP);
        handler.removeMessages(MSG_NEXT_CYCLE);
    }

    public void wakeUp(){
        if(playMode==PLAY_MODE_DP){
            playDefaultItem();
        }if(playMode==PLAY_MODE_LOOP){
            playImage(getCycleItem());
        }
    }
}

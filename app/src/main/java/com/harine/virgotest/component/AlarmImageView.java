//package com.harine.virgotest.component;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.RectF;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.bumptech.glide.Glide;
//import com.harine.virgotest.Constants;
//import com.harine.virgotest.data.DBHelper;
//import com.harine.virgotest.data.bean.ImageItem;
//import com.harine.virgotest.event.OnImageFullEvent;
//import com.harine.virgotest.util.TimeUtil;
//import com.nepalese.virgosdk.Util.ScreenUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author nepalese on 2021/3/26 16:25
// * @usage
// */
//public class AlarmImageView extends androidx.appcompat.widget.AppCompatImageView {
//    private static final String TAG = "AlarmImageView";
//
//    private static final int MSG_NEXT_DP = 1;//下一张垫片
//    private static final String DEFAULT_IMAGE = "http://pic1.win4000.com/wallpaper/f/52aa7ef301c0a.jpg";
//    private static final int PLAY_MODE_DP = 1;//垫片
//    private static final int PLAY_MODE_LOOP = 2;//循环
//    private static final int PLAY_MODE_TIMGING = 3;//定时
//
//    private Context mContext;
//    private DBHelper dbHelper;
//    private RectF mRectF;
//
//    private String pId;//当前节目id
//    private List<ImageItem> itemList;
//    private List<String> timeList;
//
//    private int fullWidth, fullHeight;
//    private int dpIndex;//垫片循环索引
//    private int playMode;//
//    private boolean isRegister = false;
//    private boolean isFull = false;
//
//    public AlarmImageView(@NonNull Context context) {
//        super(context);
//        this.mContext = context;
//        dbHelper = DBHelper.getInstance(mContext);
//
//        registerReceiver();
//        init();
//    }
//
//    public AlarmImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public AlarmImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void setLayout() {
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.leftMargin = (int) mRectF.left;
//        lp.topMargin = (int) mRectF.top;
//        lp.width = (int) (mRectF.right-mRectF.left);
//        lp.height = (int) (mRectF.bottom-mRectF.top);
//
//        isFull = false;
//        this.setLayoutParams(lp);
//    }
//
//    private void setFull(){
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.leftMargin = 0;
//        lp.topMargin = 0;
//        lp.width = fullWidth;
//        lp.height = fullHeight;
//
//        isFull = true;
//        this.setLayoutParams(lp);
//        this.bringToFront();
//        EventBus.getDefault().post(new OnImageFullEvent(isFull));
//    }
//
//    private void init() {
//        this.setScaleType(ScaleType.FIT_XY);
//        fullWidth = ScreenUtil.getScreenWidth(mContext);
//        fullHeight = ScreenUtil.getScreenHeight(mContext);
//
//        itemList = new ArrayList<>();
//        timeList = new ArrayList<>();
//
//        dpIndex = 0;
//        playMode = PLAY_MODE_DP;
//    }
//
//    private void getImageItems(){
//        itemList.clear();
//        itemList.addAll(dbHelper.getImageItemPid(pId));
//    }
//
//    //所有开始,结束时间
//    private void getTimeList() {
//        List<String> list = new ArrayList<>();
//        for (ImageItem item : itemList) {
//            list.add(TimeUtil.formatTime(item.getStartTime()));
//            list.add(TimeUtil.formatTime(item.getEndTime()));
//        }
//        timeList.clear();
//        timeList.addAll(list);
//    }
//
//    private void setAlarm(){
//        if(timeList.isEmpty()){
//            return;
//        }
//
//        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(Constants.ACTION_UPDATE_TIME_LIST_IMAGE);
//        long now = TimeUtil.getCurTimeTime();
//        for(String time: timeList){
//            long alarmTime = TimeUtil.string2LongTime(time, TimeUtil.DATE_FORMAT_TIME);
//
//            if (alarmTime - now <= 0) {
//                //已过或当前素材，不需要定时
//                continue;
//            }
//
//            alarmTime = TimeUtil.getCurTime() + alarmTime - now;
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, Constants.ALARM_REQUEST_CODE_IMAGE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            if(alarmManager!=null){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
//                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
//                }else {
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
//                }
//            }
//
//            return;
//        }
//    }
//
//    private void playImage(ImageItem curItem) {
//        if(curItem==null){
//            //当前节目无素材播放
//            playMode = PLAY_MODE_DP;
//            playDefaultItem();
//        }else{
//            playMode = PLAY_MODE_TIMGING;
//
//            judgeFull(curItem);
//            Glide.with(mContext).load(curItem.getPath()).into(this);
//        }
//    }
//
//    //播放垫片节目
//    private void playDefaultItem(){
//        Log.i(TAG, "playImage: 垫片" + + dpIndex);
//        //获取垫片节目内所有图片素材
//        List<ImageItem> list = dbHelper.getImageItemPid("000");
//        if(list.isEmpty()){
//            //无垫片
//            exitFull();
//            Glide.with(mContext).load(DEFAULT_IMAGE).into(this);
//        }else{
//            if(dpIndex>=list.size()){
//                dpIndex = 0;
//            }
//            judgeFull(list.get(dpIndex));
//            Glide.with(mContext).load(list.get(dpIndex).getPath()).into(this);
//            handler.sendEmptyMessageDelayed(MSG_NEXT_DP, list.get(dpIndex).getDuration()*1000L);
//            dpIndex++;
//        }
//    }
//
//    private void judgeFull(ImageItem item){
//        if(item.getIsFull()){
//            if(!isFull){
//                setFull();
//            }
//        }else{
//            exitFull();
//        }
//    }
//
//    private void exitFull(){
//        if(isFull){
//            setLayout();
//            EventBus.getDefault().post(new OnImageFullEvent(isFull));
//        }
//    }
//
//    private ImageItem getCurItem() {
//        long now = TimeUtil.getCurTimeTime();
//        for(ImageItem item: itemList){
//            if(now>=TimeUtil.string2LongTime(item.getStartTime(), TimeUtil.DATE_FORMAT_TIME) &&
//            now <TimeUtil.string2LongTime(item.getEndTime(), TimeUtil.DATE_FORMAT_TIME)){
//                Log.i(TAG, "getCurItem: " + item.getResId());
//                return item;
//            }
//        }
//        return null;
//    }
//
//    ///////////////////////////////////////////定时广播/////////////////////////////////////////////
//    private void registerReceiver() {
//        if (!isRegister) {
//            isRegister = true;
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(Constants.ACTION_UPDATE_TIME_LIST_IMAGE);
//            mContext.registerReceiver(updateItem, filter);
//        }
//    }
//
//    public void unRegisterReceiver() {
//        if (isRegister) {
//            isRegister = false;
//            mContext.unregisterReceiver(updateItem);
//        }
//    }
//
//    private final BroadcastReceiver updateItem = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i(TAG, "onReceive: updateList");
//            String action = intent.getAction();
//            if (Constants.ACTION_UPDATE_TIME_LIST_IMAGE.equals(action)) {
//                startPlay();
//            }
//        }
//    };
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        removeMessage();
//        cancelAlarm();
//        unRegisterReceiver();
//    }
//
//    private void cancelAlarm(){
//        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(PendingIntent.getBroadcast(mContext,
//                Constants.ALARM_REQUEST_CODE_IMAGE,
//                new Intent(Constants.ACTION_UPDATE_TIME_LIST_IMAGE),
//                PendingIntent.FLAG_UPDATE_CURRENT));
//    }
//
//    //////////////////////////////////////////handler///////////////////////////////////////////////
//    private final Handler handler = new Handler(Looper.myLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case MSG_NEXT_DP:
//                    playDefaultItem();
//                    break;
//            }
//        }
//    };
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public void setpId(String pId) {
//        this.pId = pId;
//        getImageItems();
//        getTimeList();
//    }
//
//    public void setmRectF(RectF mRectF) {
//        this.mRectF = mRectF;
//        setLayout();
//    }
//
//    public void startPlay(){
//        removeMessage();
//        setAlarm();
//        playImage(getCurItem());
//    }
//
//    public void removeMessage(){
//        handler.removeMessages(MSG_NEXT_DP);
//    }
//
//    public void wakeUp(){
//        if(playMode==PLAY_MODE_DP){
//            playDefaultItem();
//        }
//    }
//}

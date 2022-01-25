package com.harine.virgotest.ui.manager;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.harine.ui.interfaces.ErrorCallBack;
import com.harine.ui.view.StationView_S1;
import com.harine.virgotest.Constants;
import com.harine.virgotest.R;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.AreaItem;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.event.OnImageFullEvent;
import com.harine.virgotest.event.OnImageOverEvent;
import com.harine.virgotest.event.OnProgramListChangeEvent;
import com.harine.virgotest.event.OnStopageChangeEvent;
import com.harine.virgotest.event.OnVideoFullEvent;
import com.harine.virgotest.event.OnVideoOverEvent;
import com.harine.virgotest.ui.view.AlarmImageView2;
import com.harine.virgotest.ui.view.AlarmTextView2;
import com.harine.virgotest.ui.view.AlarmVideoView;
import com.harine.virgotest.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nepalese on 2021/3/30 16:06
 * @usage 节目显示页：传入当前播放节目列表
 */
public class ProgramPage {
    private static final String TAG = "ProgramPage";

    private Context context;
    private DBHelper dbHelper;
//    private DTMBHelper.TvHelper tvHelper;//dtmb 控制器

    private RelativeLayout rlContent;//布局容器
    private Program curProgram = null;//当前节目
    private final ProgramController programController;//节目排期
    private final LinkedHashMap<String, View> mCacheViews = new LinkedHashMap<>();//记录已添加控件
    private float mWidth, mHeight;//容器宽高
    private int cyIndex;//循环节目列表索引
    private int mScreen;//主副屏节目

    public ProgramPage(RelativeLayout rlContent, float mWidth, float mHeight, int mScreen) {
        this.rlContent = rlContent;
        this.context = rlContent.getContext();
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mScreen = mScreen;
        this.cyIndex = 0;
        registerEvent();

        dbHelper = DBHelper.getInstance(context);
        //todo
//        if (BuildConfig.flavorType != Cfg.Flavors.SWH5168) {
//            tvHelper = new DTMBHelper.TvHelper();
//        }
        programController = new ProgramController(context, mScreen);
        getCurProgram(programController.getCurPid());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void playProgram(String pid) {
        getCurProgram(pid);
        startPlay();
    }

    //重置当前节目
    private void resetCurProgram(Program program){
        curProgram = program;
        updateContent(program);
    }

    //获取当前节目
    private void getCurProgram(String pid){
        if(!pid.equals("")){
            curProgram = dbHelper.getProgramById(pid);
        }else{
            getCycleProgram();
        }
    }

    //获取循环节目
    private void getCycleProgram(){
        //获取循环节目
        List<Program> cycle = dbHelper.getTodayProgramList(Constants.PROGRAM_TYPE_CYCLE, mScreen);
        if(cycle==null || cycle.isEmpty()){
            Log.i(TAG, mScreen + " getCurProgram: 无循环节目");
            curProgram = null;
        }else{
            if(cyIndex >=cycle.size()){
                cyIndex =0;
            }
            Program program = cycle.get(cyIndex);
            if(FunctionManager.isValide(program)){
                curProgram = program;
            }else{
                cyIndex ++;
                if(FunctionManager.judgeOver(cycle)) {
                    curProgram = null;
                }else{
                    getCycleProgram();
                }
            }
        }
    }

    //新增节目：
    private void addNewProgram(){
        programController.resetController();
        String newId = programController.getCurPid();
        if(curProgram==null){
            //原先无节目
            playProgram(newId);
            deleteUselessRes();
            return;
        }

        String pid = curProgram.getPId();
        //TODO 暂不考虑同节目更新
        if(!pid.equals(newId)){
            playProgram(newId);
        }
    }

    //重新载入
    private void reLoadProgram(){
        programController.resetController();
        getCurProgram(programController.getCurPid());
        startPlay();
    }

    //无效素材删除
    private void deleteUselessRes(){
        //todo
    }

    private boolean isContainsLocation(Program program, double lng, double lat) {
        //todo
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void startPlay(){
        if(curProgram ==null){
            //主屏无节目时默认播放垫片节目
            if(mScreen==Constants.SCREEN_MIAN){
                List<Program> dpList = dbHelper.getDPProgram();
                if(dpList==null || dpList.isEmpty()){
                    Log.e(TAG, "主屏无节目");
                    //不处理、继续播放垫片节目
                }else {
                    EventBus.getDefault().post(new OnStopageChangeEvent());
                    //播放垫片节目
                    curProgram = dpList.get(0);
                    updateContent(curProgram);
                }
            }else{//副屏无节目时清空
                Log.i(TAG, "副屏无节目");
                rlContent.removeAllViews();
            }
        }else{
            EventBus.getDefault().post(new OnStopageChangeEvent());
            updateContent(curProgram);
        }
    }

    //定点节目更新
    public void playByLocation(double lng, double lat) {
        Log.i(TAG, mScreen + " playByLocation: lng=" + lng + " lat=" + lat);

        //1. 在有效范围内
        for (Program program : programController.getmLocationList()) {
            if (isContainsLocation(program, lng, lat)) {
                //更换定点节目：
                //1. 原无节目
                //2. type<location
                //3. 不同定点节目
                if (curProgram == null || curProgram.getPType() < Constants.PROGRAM_TYPE_LOCATION
                || (curProgram.getPType()==Constants.PROGRAM_TYPE_LOCATION && !curProgram.getPId().equals(program.getPId()))) {
                    //播放定点节目
                    resetCurProgram(program);
                }
                return;
            }
        }

        //2. 不在有效范围
        if (curProgram != null && curProgram.getPType() == Constants.PROGRAM_TYPE_LOCATION) {
            //当前为定点节目：退出定点播放，重新载入节目
            reLoadProgram();
        }//原无节目或非定点节目：do nothing
    }

    //释放资源
    public void release(){
        unregisterEvent();
        rlContent.removeAllViews();
        programController.cancelAlarm();
        programController.unRegisterReceiver();
    }

    //更新文本速度.
    public void updateTextSpeed(int speed) {
        Set<String> keySet = mCacheViews.keySet();
        for (String key : keySet) {
            if (key != null) {
                if (key.contains(Constants.TYPE_SCROLL_TEXT)) {
                    View view = mCacheViews.get(key);
                    ((AlarmTextView2) view).setSpeed(speed);
                }
            }
        }
    }

    //更新时间日期
    public void updateDateAndTimeText() {
        Set<String> keySet = mCacheViews.keySet();
        for (String key : keySet) {
            if (key != null) {
                if (key.contains(Constants.TYPE_DATE)) {
                    TextView textView = (TextView) mCacheViews.get(key);
                    String data = TimeUtil.getCurDate(TimeUtil.DATE_FORMAT_DATE);
                    textView.setText(data);
                } else if (key.contains(Constants.TYPE_TIME)) {
                    TextView textView = (TextView) mCacheViews.get(key);
                    String time = TimeUtil.getCurDate(TimeUtil.DATE_FORMAT_TIME2);
                    textView.setText(time);
                }
            }
        }
    }

    //更新天气
//    public void updateWeatherText(Weather weather) {
//        if (weather == null) return;
//        Set<String> keySet = mCacheViews.keySet();
//        for (String key : keySet) {
//            if (key != null) {
//                if (key.contains(Constants.TYPE_WEATHER)) {
//                    WeatherView view = (WeatherView) mCacheViews.get(key);
//                    view.setWeather(weather).updateContent();
//                }
//            }
//        }
//    }

    public void openTv() {
        Set<String> keySet = mCacheViews.keySet();
        for (String key : keySet) {
            if (key != null) {
                if (key.contains(Constants.TYPE_LIVE)) {
//                    DTMBHelper.closeTv();
//                    View view = mCacheViews.get(key);
//                    DTMBHelper.openTV(view, mLiveX, mLiveY, mLiveW, mLiveH);
                    break;
                }
            }
        }
    }

    public void closeTv() {
        Set<String> keySet = mCacheViews.keySet();
        for (String key : keySet) {
            if (key != null) {
                if (key.contains(Constants.TYPE_LIVE)) {
//                    DTMBHelper.closeTv();
                    break;
                }
            }
        }
    }

    private void loadImage(ImageView view, AreaItem area) {
        //todo
//        File file = CacheFileUtil.getResource(context, area.getSignature());
//        if (file != null) {
//            Glide.with(context).load(file.getAbsolutePath()).into(view);
//        }
    }

    public void stopPlay(){
        hideAllAddView();
    }

    public void continuePlay(){
        showAllAddView();
    }

    /////////////////////////////////////更新节目布局////////////////////////////////////////////////
    //隐藏已有控件
    private void hideAllAddView() {
        for (Map.Entry<String, View> entry : mCacheViews.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
    }

    //显示已有控件
    private void showAllAddView() {
        for (Map.Entry<String, View> entry : mCacheViews.entrySet()) {
            entry.getValue().setVisibility(View.VISIBLE);
        }
    }

    //移除多余控件
    private void removeUselessView() {
        for (Map.Entry<String, View> entry : mCacheViews.entrySet()) {
            if(entry.getValue().getVisibility()!=View.VISIBLE){
                Log.i(TAG, mScreen + " removeUselessView: " + entry.getKey());
                rlContent.removeView(entry.getValue());
            }
        }
    }

    private void updateContent(Program program){
        //1. 获取该节目下所有布局: 根据布局排序排列
        List<AreaItem> list = dbHelper.getAreaItems(program.getPId());
        if (list == null || list.size() == 0) {
            return;
        }
      
//        Collections.sort(list, AreaItem.comparator);

        //2. 先将所有已添加控件隐藏
        hideAllAddView();
        
        //3. 根据布局增加对应控件
        for(AreaItem area: list){
            switch (area.getAreaType()) {
                case Constants.TYPE_BG:
                case Constants.TYPE_MASK:
                case Constants.TYPE_LOGO:
                    updateImageView(area);
                    break;
                case Constants.TYPE_BUTTON:
                    updateButton(area);
                    break;
                case Constants.TYPE_STATIC_TEXT:
                    updateStaticText(area);
                    break;
                case Constants.TYPE_WEB:
                    updateWeb(area);
                    break;
                case Constants.TYPE_IMP:
                    updateImp(area);
                    break;
                case Constants.TYPE_DATE:
                    updateDateOrTime(area, true);
                    break;
                case Constants.TYPE_TIME:
                    updateDateOrTime(area, false);
                    break;
                case Constants.TYPE_VIDEO_IMAGE:
//                    updateVideoImage(area);
                    break;
                case Constants.TYPE_VIDEO:
                    updateVideo(area);
                    break;
                case Constants.TYPE_SCROLL_TEXT:
                    updateScrollText(area);
                    break;
                case Constants.TYPE_WEATHER:
                    //weather 显示天数由服务器配置,
                    updateWeather(area);
                    break;
                case Constants.TYPE_DYNAMIC:
                    updateDynamic(area);
                    break;
                case Constants.TYPE_TOUCH:
                    //to be continue
                    break;
                case Constants.TYPE_STATION_LIST:
                    //站名列表view
                    updateStationList(area);
                    break;
                case Constants.TYPE_LIVE:
                    //在线电视view
                    updateLive(area);
                    break;
            }
        }

        //删除多余布局（上一个节目有，现在没有的）
        removeUselessView();
    }

    /**
     * 单图片显示
     * @param area
     */
    private void updateImageView(AreaItem area) {
        if (rlContent == null || area == null) return;

        ImageView view = null;
        String key = String.format("%s%s", area.getAreaType(), area.getAreaIndex());
        if (mCacheViews.containsKey(key)) {
            view = (ImageView) mCacheViews.get(key);
        }
        if (view == null) {
            view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.FIT_XY);

            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        updateLayoutParams(view, area);
        loadImage(view, area);
    }

    /**
     * app 跳转按钮
     * @param area
     */
    private void updateButton(AreaItem area) {
        if (rlContent == null || area == null) return;

        //todo
        String key = String.format("%s%s", area.getAreaType(), area.getAreaIndex());
        TextView view;
        if (mCacheViews.containsKey(key)) {
            view = (TextView) mCacheViews.get(key);
        } else {
            view = new TextView(context);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ComponentUtil.doStartApplicationWithPackageName(context, area.getName());
                }
            });
            view.setTextColor(ContextCompat.getColor(context, R.color.white));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        view.setGravity(Gravity.CENTER);
        updateLayoutParams(view, area);

//        view.setText(area.getName());
    }

    /**
     * 静态文本：显示本控件对应名字？
     * @param area
     */
    private void updateStaticText(AreaItem area) {
        if (rlContent == null || area == null) return;
        TextView view = null;
        String key = String.format("%s%s", area.getAreaType(), area.getAreaIndex());
        if (mCacheViews.containsKey(key)) {
            view = (TextView) mCacheViews.get(key);
        }
        if (view == null) {
            view = new TextView(context);
            view.setTextColor(ContextCompat.getColor(context, R.color.black));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);

            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        view.setGravity(Gravity.CENTER);
        //todo
//        view.setText(area.getName());
        updateLayoutParams(view, area);
    }

    /**
     * 网页：唯一
     * @param area
     */
    private void updateWeb(AreaItem area) {
        if (rlContent == null || area == null)
            return;
        //todo
//        CarourselWebView view = null;
//        String key = area.getAreaType();
//        if (mCacheViews.containsKey(key)) {
//            view = (CarourselWebView) mCacheViews.get(key);
//        }
//        if (view == null) {
//            view = new CarourselWebView(rlContent.getContext());
//
//            rlContent.addView(view);
//            mCacheViews.put(key, view);
//        }
//
//        updateLayoutParams(view, area);
//
//        List<DBResource> resList = DBHelper.getInstance()
//                .findResource(area.getPlaylistId(), area.getAreaId());
//        if (resList != null && resList.size() > 0) {
//            view.setUrl2(resList, SettingSpData.getPlayTimeIndex(context));
//            view.startShow();
//        }
    }

    /**
     * 时间日期：唯一
     * @param area
     * @param b 是否为日期
     */
    private void updateDateOrTime(AreaItem area, boolean b) {
        if (rlContent == null || area == null) return;

        TextView view;
        String key = area.getAreaType();

        if (mCacheViews.containsKey(key)) {
            view = (TextView) mCacheViews.get(key);
        } else {
            view = new TextView(context);

            //todo
//            List<Material> list = dbHelper.getMaterialListByCtrlId(item.getId());
//            if (list == null || list.size() == 0) {
//                view.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//            } else {
//                Material material = list.get(0);
//                view.setTextColor(Color.parseColor(material.getTextColor()));
//                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, CommUtil.toFloat(material.getTextSize().substring(0, material.getTextSize().length() - 2)));
//            }

            rlContent.addView(view);
            mCacheViews.put(key, view);
        }
        updateLayoutParams(view, area);
        view.setGravity(Gravity.CENTER);
        view.setText(TimeUtil.getCurDate(b ? TimeUtil.DATE_FORMAT_DATE : TimeUtil.DATE_FORMAT_TIME2));
    }

    /**
     * 天气控件：唯一
     * @param area
     */
    private void updateWeather(AreaItem area) {
        if (rlContent == null || area == null) return;

        //todo
//        WeatherView view = null;
//        String key = area.getAreaType();
//        if (mCacheViews.containsKey(key)) {
//            view = (WeatherView) mCacheViews.get(key);
//        }
//        if (view == null) {
//            view = new WeatherView(rlContent.getContext());
//
//            rlContent.addView(view);
//            mCacheViews.put(key, view);
//        }
//        updateLayoutParams(view, area);
    }

    /**
     * 动态表单: 唯一
     * @param area
     */
    private void updateDynamic(AreaItem area) {
        if (rlContent == null || area == null) return;

        //todo
//        DynamicView view;
//        String key = area.getAreaType();
//        if (mCacheViews.containsKey(key)) {
//            view = (DynamicView) mCacheViews.get(key);
//        } else {
//            view = new DynamicView(rlContent.getContext());
//            rlContent.addView(view);
//            mCacheViews.put(key, view);
//        }
//
//        File tmp = CacheFileUtil.getResource(context, area.getSignature());
//        if (tmp != null) {
//            ParseDynamic dynamic = new ParseDynamic();
//            Dynamic dynamicXml = dynamic.parse(tmp.getPath());
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getLayoutParams());
//            NumberFormat numberFormat = NumberFormat.getPercentInstance();
//            try {
//                int left = (int) area.getAreaLeft(); //(area.getAreaLeftPercent() * mWidth * 0.01f);
//                int top = (int) area.getAreaTop();//(area.getAreaTopPercent() * mHeight * 0.01f);
//                int width = (int) area.getAreaWidth();//(area.getAreaWidthPercent() * mWidth * 0.01f);
//                int height = (int) area.getAreaHeigh();//(area.getAreaHeighPercent() * mHeight * 0.01f);
//                lp.leftMargin = left;
//                lp.topMargin = top;
//                lp.width = width;
//                lp.height = height;
//            } catch (Exception ignored) {}
//            view.setData(dynamicXml, lp).update();
//        }
    }

    /**
     * 报站器：唯一
     * @param area
     */
    private void updateStationList(AreaItem area) {
        if (rlContent == null || area == null) return;

        View view = null;
        String key = area.getAreaType();
        if (mCacheViews.containsKey(key)) {
            view = mCacheViews.get(key);
            view.setVisibility(View.VISIBLE);
        }

        if(view==null){
            view = new StationView_S1(context);
            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        updateLayoutParams(view, area);

        ((StationView_S1) view).startInitial();
        ((StationView_S1) view).setCallBack(new ErrorCallBack() {
            @Override
            public void onOpenFail(String s) {

            }

            @Override
            public void onNoResponse(String s) {

            }
        });
    }

    /**
     * 直播：唯一
     * @param area
     */
    private void updateLive(AreaItem area) {
        if (rlContent == null || area == null) return;

        //todo
        View view = null;
        String key = area.getAreaType();
        if (mCacheViews.containsKey(key)) {
            view = (SurfaceView) mCacheViews.get(key);
        }
//        if (view == null) {
//            view = VideoPlayHelper.createVideoPlayer(context);
//            rlContent.addView(view);
//            mCacheViews.put(key, view);
//        }
//        updateLayoutParams(view, area);
//
//        if (tvHelper != null) {
//            DTMBHelper.workDTMBChecking(tvHelper);
//            tvHelper.bindItem(rlContent, area, (int) mWidth, (int) mHeight);
//            tvHelper.bindLiveSize(mLiveX, mLiveY, mLiveW, mLiveH);
//        }
        closeTv();
        openTv();
    }

    /**
     * 滚动字幕：唯一
     * @param area
     */
    private void updateScrollText(AreaItem area) {
        if (rlContent == null || area == null)
            return;

        View view = null;
        String key = area.getAreaType();
        if (mCacheViews.containsKey(key)) {
            view = mCacheViews.get(key);
            view.setVisibility(View.VISIBLE);
        }
        if (view == null) {
            view = new AlarmTextView2(context);
            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        updateLayoutParams(view, area);

        ((AlarmTextView2) view).setmProgram(curProgram);
        ((AlarmTextView2) view).startPlay();
    }

    /**
     * 轮播图片组
     * @param area
     */
    private void updateImp(AreaItem area) {
        if (rlContent == null || area == null)
            return;

        View view = null;
        String key = String.format("%s%s", area.getAreaType(), area.getAreaIndex());
        if (mCacheViews.containsKey(key)) {
            view = mCacheViews.get(key);
            view.setVisibility(View.VISIBLE);
        }
        if (view == null) {
            view = new  AlarmImageView2(context, area.getAreaIndex(), mScreen);
            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        ((AlarmImageView2) view).setmRectF(new RectF(area.getAreaLeft(),
                area.getAreaTop(), area.getAreaWidth(), area.getAreaHeigh()));
        ((AlarmImageView2) view).setProgram(curProgram);
        ((AlarmImageView2) view).startPlay();
    }

    /**
     * 视频
     * @param area
     */
    private void updateVideo(AreaItem area) {
        if (rlContent == null || area == null)
            return;

        View view = null;
        String key = String.format("%s%s", area.getAreaType(), area.getAreaIndex());
        if (mCacheViews.containsKey(key)) {
            view = mCacheViews.get(key);
            view.setVisibility(View.VISIBLE);
        }
        if (view == null) {
            view = new AlarmVideoView(context, area.getAreaIndex(), mScreen);
            rlContent.addView(view);
            mCacheViews.put(key, view);
        }

        ((AlarmVideoView) view).setmRectF(new RectF(area.getAreaLeft(),
                area.getAreaTop(), area.getAreaWidth(), area.getAreaHeigh()));
        ((AlarmVideoView) view).setProgram(curProgram);
        ((AlarmVideoView) view).startPlay();
    }

    //设置控件位置
    private void updateLayoutParams(View view, AreaItem area) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        try {
            int left = (int) area.getAreaLeft(); //(area.getAreaLeftPercent() * mWidth * 0.01f);
            int top = (int) area.getAreaTop();//(area.getAreaTopPercent() * mHeight * 0.01f);
            int width = (int) area.getAreaWidth();//(area.getAreaWidthPercent() * mWidth * 0.01f);
            int height = (int) area.getAreaHeigh();//(area.getAreaHeighPercent() * mHeight * 0.01f);
            Log.d(TAG, mScreen + " 布局：l = " + left + " t = " + top + " w = " + width + " h = " + height);
            lp.leftMargin = left;
            lp.topMargin = top;
            lp.width = width;
            lp.height = height;
        } catch (Exception e) {
           e.printStackTrace();
        }
        view.setLayoutParams(lp);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //播放下一个循环节目
    private void playNextCycle(){
        String pid = curProgram.getPId();
        if(FunctionManager.isCycleOver(pid)){
            Log.i(TAG, mScreen + " playNextCycle: ");

            //1. 节目日志更新
            FunctionManager.updateProgramPlayLog(pid);

            //2. 素材日志重置
            FunctionManager.resetAllResPlayLog(pid);

            //播放下一个循环节目
            cyIndex++;
            getCycleProgram();
            startPlay();
        }
    }

    ////////////////////////////////////////////even bus////////////////////////////////////////////
    private void registerEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unregisterEvent() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /////////////////////////////////////////////////main///////////////////////////////////////////
    @Subscribe
    public void onThreadMainEvent(OnImageFullEvent event){
        if(event.getScreen()==mScreen){
            String key = String.format("%s%s", Constants.TYPE_IMP, event.getIndex());
            Log.i(TAG, "OnImageFullEvent: " + key);
            for (Map.Entry<String, View> entry : mCacheViews.entrySet()) {
                if(!entry.getKey().equals(key)){
                    if(event.isFull()){
                        entry.getValue().setVisibility(View.GONE);
                    }else {
                        entry.getValue().setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onThreadMainEvent(OnVideoFullEvent event){
        if(event.getScreen()==mScreen){
            String key = String.format("%s%s", Constants.TYPE_VIDEO, event.getIndex());
            Log.i(TAG, "OnVideoFullEvent: " + key);
            for (Map.Entry<String, View> entry : mCacheViews.entrySet()) {
                if(!entry.getKey().equals(key)){
                    if(event.isFull()){
                        entry.getValue().setVisibility(View.GONE);
                    }else {
                        entry.getValue().setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onThreadMainEvent(OnProgramListChangeEvent event){
        if(event.getScreen()==mScreen){
            String pid = event.getCurPid();
            Log.i(TAG, "触发节目时间点: " + pid);

            if(pid.equals("")){
                //节目结束
                if(curProgram!=null && curProgram.getPType()==Constants.PROGRAM_TYPE_LOCATION){
                    return;
                }else{
                    playProgram(pid);
                    return;
                }
            }

            //新节目
            Program program = dbHelper.getProgramById(pid);
            //如果当前播放的是定点节目：定时将不起作用
            if(curProgram!=null && curProgram.getPType()==Constants.PROGRAM_TYPE_LOCATION && program.getPType()<curProgram.getPType()){
                return;
            }

            curProgram = program;
            startPlay();
        }
    }

    @Subscribe
    public void onThreadMainEvent(OnImageOverEvent event){
        if(event.getScreen()==mScreen){
            Log.i(TAG, "OnImageOverEvent: ");
            playNextCycle();
        }
    }

    @Subscribe
    public void onThreadMainEvent(OnVideoOverEvent event){
        if(event.getScreen()==mScreen){
            Log.i(TAG, "OnVideoOverEvent: ");
            playNextCycle();
        }
    }
}

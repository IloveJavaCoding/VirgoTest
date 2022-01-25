package com.harine.virgotest.ui.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.harine.virgotest.Constants;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.TodayProgram;
import com.harine.virgotest.event.OnProgramListChangeEvent;
import com.harine.virgotest.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author nepalese on 2021/3/30 11:09
 * @usage 节目管理类
 */
public class ProgramController {
    private static final String TAG = "ProgramController";

    private final Context mContext;
    private final DBHelper dbHelper;

    private Set<String> timeList;//当天节目排期
    private List<Program> mLocationList;//定点节目
    private String curPid = null;//当前节目id:  "": 无节目
    private boolean isRegister = false;
    private final int mScreen;//主副屏节目

    public ProgramController(Context mContext, int mScreen) {
        this.mContext = mContext;
        this.mScreen = mScreen;
        this.dbHelper = DBHelper.getInstance(mContext);
        registerReceiver();

        init();
        setController();
    }

    private void init() {
        timeList = new TreeSet<>();
    }

    private void setController() {
        initTodayProgram();
        initLocationList();
        setAlarm();
        getCurProgram();
    }

    //获取定点节目列表
    private void initLocationList() {
        Log.i(TAG, mScreen + " initLocationList: ");
        mLocationList = dbHelper.getTodayProgramList(Constants.PROGRAM_TYPE_LOCATION, mScreen);
    }

    //获取今日节目，排期
    private void initTodayProgram() {
        dbHelper.deleteAllTodayProgram();
        List<Program> timingList = dbHelper.getTodayProgramList(Constants.PROGRAM_TYPE_TIMING, mScreen);
        saveTodayProgram(timingList);
        List<Program> urgentList = dbHelper.getTodayProgramList(Constants.PROGRAM_TYPE_URGENT, mScreen);
        saveTodayProgram(urgentList);
        List<Program> mostUrgentList = dbHelper.getTodayProgramList(Constants.PROGRAM_TYPE_MOST_URGENT, mScreen);
        saveTodayProgram(mostUrgentList);


        //1. 今日所有节目：按开始时间排序
        List<TodayProgram> allToday = dbHelper.getAllTodayProgram(mScreen);

        //2.将所有节目开始，结束时间添加到节目排期列表（从小到大排序，去重）
        List<String> list = new ArrayList<>();
        for(TodayProgram program: allToday){
            list.add(TimeUtil.formatTime(program.getStartTime()));
            list.add(TimeUtil.formatTime(program.getEndTime()));
        }

        Collections.sort(list);
        timeList.clear();
        timeList.addAll(list);
    }

    private void saveTodayProgram(List<Program> list) {
        if (list == null) {
            return;
        }
        
        for (Program program : list) {
            List<TodayProgram> overloadList = dbHelper.getOverloadTodayProgram(program.getStartTime(), program.getEndTime(), mScreen);
            dbHelper.deleteTodayProgram(overloadList);
            TodayProgram frontItem = null;
            TodayProgram backItem = null;
            if (dbHelper.getTodayProgramItem(program.getStartTime(), mScreen).size() != 0) {
                frontItem = dbHelper.getTodayProgramItem(program.getStartTime(), mScreen).get(0);
            }
            if (dbHelper.getTodayProgramItem(program.getEndTime(), mScreen).size() != 0) {
                backItem = dbHelper.getTodayProgramItem(program.getEndTime(), mScreen).get(0);
            }
            if (frontItem != null && frontItem.equals(backItem)) {
                backItem = new TodayProgram(frontItem);
                frontItem.setEndTime(program.getStartTime());
                dbHelper.updateTodayProgram(frontItem);
                backItem.setStartTime(program.getEndTime());
                backItem.setScreen(program.getScreen());
                dbHelper.saveTodayProgram(backItem);
            } else {
                if (frontItem != null) {
                    frontItem.setEndTime(program.getStartTime());
                    dbHelper.updateTodayProgram(frontItem);
                }
                if (backItem != null) {
                    backItem.setStartTime(program.getEndTime());
                    dbHelper.updateTodayProgram(backItem);
                }
            }
            TodayProgram todayProgram = new TodayProgram();
            todayProgram.setPid(program.getPId());
            todayProgram.setType(program.getPType());
            todayProgram.setStartTime(program.getStartTime());
            todayProgram.setEndTime(program.getEndTime());
            todayProgram.setScreen(program.getScreen());
            dbHelper.saveTodayProgram(todayProgram);
        }
    }

    //3. 获取当下时间段应播放节目列表（按等级排序，取与最高等级同级）
    private void getCurProgram(){
        List<TodayProgram> nowList = new ArrayList<>();
        long now = TimeUtil.getCurTimeTime();
        List<TodayProgram> allToday = dbHelper.getAllTodayProgram(mScreen);
        for(TodayProgram item: allToday){
            if(now>=TimeUtil.string2LongTime(item.getStartTime(), TimeUtil.DATE_FORMAT_TIME) &&
                    now <TimeUtil.string2LongTime(item.getEndTime(), TimeUtil.DATE_FORMAT_TIME)){
                nowList.add(item);
            }
        }

        if(nowList.isEmpty()){
            //当前无节目
            Log.i(TAG, mScreen + " 当前无节目: ");
            curPid = "";
            return;
        }

        Collections.sort(nowList, comparatorType2);
        int type = nowList.get(0).getType();//当前列表最高等级
        Log.i(TAG, mScreen + " type " + type);
        List<TodayProgram> delete = new ArrayList<>();
        for(TodayProgram program: nowList){
            if(program.getType()<type){
                delete.add(program);
            }
        }
        nowList.removeAll(delete);
        curPid = nowList.get(0).getPid();
    }

    //定时任务
    private void setAlarm(){
        if(timeList.isEmpty()){
            Log.i(TAG, mScreen + " setAlarm: empty");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constants.ACTION_UPDATE_TIME_LIST_PROGRAM);
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
                    Constants.ALARM_REQUEST_CODE_PROGRAM, intent,
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Program> getmLocationList(){
        return this.mLocationList;
    }

    public String getCurPid(){
        return curPid;
    }

    //重新载入节目
    public void resetController() {
        setController();
    }

    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(mContext,
                Constants.ALARM_REQUEST_CODE_PROGRAM,
                new Intent(Constants.ACTION_UPDATE_TIME_LIST_PROGRAM),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }
    ///////////////////////////////////////////定时广播/////////////////////////////////////////////
    private void registerReceiver() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_UPDATE_TIME_LIST_PROGRAM);
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
            Log.i(TAG, mScreen + " onReceive: update program");
            String action = intent.getAction();
            if (Constants.ACTION_UPDATE_TIME_LIST_PROGRAM.equals(action)) {
                setAlarm();
                getCurProgram();
                //更新提醒
                EventBus.getDefault().post(new OnProgramListChangeEvent(curPid, mScreen));
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////
    private final Comparator<TodayProgram> comparatorType2 = new Comparator<TodayProgram>() {
        @Override
        public int compare(TodayProgram o1, TodayProgram o2) {
            return o2.getType() - o1.getType();
        }
    };
}

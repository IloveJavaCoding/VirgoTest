package com.harine.virgotest.data;

import android.content.Context;

import com.harine.virgotest.Constants;
import com.harine.virgotest.data.bean.AreaItem;
import com.harine.virgotest.data.bean.ImageItem;
import com.harine.virgotest.data.bean.PlayLog;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.TextItem;
import com.harine.virgotest.data.bean.TodayProgram;
import com.harine.virgotest.data.bean.VideoItem;
import com.harine.virgotest.data.db.AreaItemDao;
import com.harine.virgotest.data.db.DaoMaster;
import com.harine.virgotest.data.db.DaoSession;
import com.harine.virgotest.data.db.ImageItemDao;
import com.harine.virgotest.data.db.PlayLogDao;
import com.harine.virgotest.data.db.ProgramDao;
import com.harine.virgotest.data.db.TextItemDao;
import com.harine.virgotest.data.db.TodayProgramDao;
import com.harine.virgotest.data.db.VideoItemDao;
import com.harine.virgotest.util.TimeUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author nepalese on 2021/3/16 16:42
 * @usage
 */
public class DBHelper {
    private static final String TAG = "DBHelper";

    private Context mContext;
    private static volatile DBHelper instance;

    private DaoMaster daoMaster;
    private DaoSession daoSession;

    //声明需用到的Dao类
    private VideoItemDao videoItemDao;
    private ImageItemDao imageItemDao;
    private ProgramDao programDao;
    private AreaItemDao areaItemDao;
    private TextItemDao textItemDao;
    private TodayProgramDao todayProgramDao;
    private PlayLogDao playLogDao;

    private DBHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context not null");
        }
        mContext = context;
        DaoSession session = getDaoSession(context);

        //初始化每个dao
        videoItemDao = session.getVideoItemDao();
        imageItemDao = session.getImageItemDao();
        programDao = session.getProgramDao();
        areaItemDao = session.getAreaItemDao();
        textItemDao = session.getTextItemDao();
        todayProgramDao = session.getTodayProgramDao();
        playLogDao = session.getPlayLogDao();
    }

    private DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            daoMaster = getDaoMaster(context);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    private DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DatabaseOpenHelper helper = new DatabaseOpenHelper(context, DatabaseOpenHelper.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDb());
        }
        return daoMaster;
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    ///////////////////////对每个表的具体操作：增删改查//////////////////////////////
    public boolean hadItem(){
        List<ImageItem> list = imageItemDao.loadAll();
        return !list.isEmpty();
    }

    //增
    public void saveVideoItem(VideoItem item){
        videoItemDao.insertOrReplace(item);
    }

    public void saveImageItem(ImageItem item){
        imageItemDao.insertOrReplace(item);
    }

    public void saveProgram(Program item){
        programDao.insertOrReplace(item);
    }

    public void saveAreaItem(AreaItem item){
        areaItemDao.insertOrReplace(item);
    }

    public void saveTextItem(TextItem item){
        textItemDao.insertOrReplace(item);
    }

    public void saveTodayProgram(TodayProgram todayProgram) {
        todayProgramDao.insert(todayProgram);
    }

    public void savePlayLog(PlayLog playLog) {
        playLogDao.insertOrReplace(playLog);
    }

    //删
    public void deleteVideoItem(VideoItem item){
        videoItemDao.delete(item);
    }

    public void deleteImageItem(ImageItem item){
        imageItemDao.delete(item);
    }

    public void deleteProgram(Program item){
        programDao.delete(item);
    }

    public void deleteAreaItem(AreaItem item){
        areaItemDao.delete(item);
    }

    public void deleteTextItem(TextItem item){
        textItemDao.delete(item);
    }

    public void deleteTodayProgram(List<TodayProgram> list) {
        for (int i = 0; i < list.size(); i++) {
            todayProgramDao.deleteByKey(list.get(i).getId());
        }
    }

    public void deletePlayLog(PlayLog item){
        playLogDao.delete(item);
    }

    //清空
    public void clearVideoItem(){
        videoItemDao.deleteAll();
    }

    public void clearImageItem(){
        imageItemDao.deleteAll();
    }

    public void clearProgram(){
        programDao.deleteAll();
    }

    public void clearAreaItem(){
        areaItemDao.deleteAll();
    }

    public void clearTextItem(){
        textItemDao.deleteAll();
    }

    public void clearPlayLog(){
        playLogDao.deleteAll();
    }

    public void deleteAllTodayProgram() {
        todayProgramDao.deleteAll();
    }

    //改
    public void updateTodayProgram(TodayProgram todayProgram) {
        todayProgramDao.update(todayProgram);
    }

    public void updatePlayLog(PlayLog playLog) {
        playLogDao.update(playLog);
    }

    //查
    //获取某节目下所有视频排期
    public List<VideoItem> getAllVideoItem(String pid){
        QueryBuilder<VideoItem> queryBuilder = videoItemDao.queryBuilder();
        queryBuilder.where(VideoItemDao.Properties.PId.eq(pid));
        return queryBuilder.build().list();
    }

    //获取某节目下指定控件所有视频排期
    public List<VideoItem> getVideoItemPid(String pid, String areaId){
        QueryBuilder<VideoItem> queryBuilder = videoItemDao.queryBuilder();
        queryBuilder.where(VideoItemDao.Properties.PId.eq(pid),
                VideoItemDao.Properties.AreaId.eq(areaId))
                .orderAsc(VideoItemDao.Properties.StartTime);
        return queryBuilder.build().list();
    }

    //获取某节目下所有图片排期
    public List<ImageItem> getAllImageItem(String pid){
        QueryBuilder<ImageItem> queryBuilder = imageItemDao.queryBuilder();
        queryBuilder.where(ImageItemDao.Properties.PId.eq(pid));
        return queryBuilder.build().list();
    }

    //获取某节目下指定控件所有图片排期
    public List<ImageItem> getImageItemPid(String pid, String areaId){
        QueryBuilder<ImageItem> queryBuilder = imageItemDao.queryBuilder();
        queryBuilder.where(ImageItemDao.Properties.PId.eq(pid),
                ImageItemDao.Properties.AreaId.eq(areaId))
                .orderAsc(ImageItemDao.Properties.StartTime);
        return queryBuilder.build().list();
    }

    //获取某节目下所有文本排期
    public List<TextItem> getTextItemPid(String pid){
        QueryBuilder<TextItem> queryBuilder = textItemDao.queryBuilder();
        queryBuilder.where(TextItemDao.Properties.PId.eq(pid))
                .orderAsc(TextItemDao.Properties.StartTime);
        return queryBuilder.build().list();
    }

    //获取指定id的节目
    public Program getProgramById(String pid){
        QueryBuilder<Program> qb = programDao.queryBuilder();
        qb.where(ProgramDao.Properties.PId.eq(pid));
        return qb.build().list().get(0);
    }

    //某节目下所有布局：按zindex排序
    public List<AreaItem> getAreaItems(String pid){
        QueryBuilder<AreaItem> qb = areaItemDao.queryBuilder();
        qb.where(AreaItemDao.Properties.PId.eq(pid));
        return qb.build().list();
    }

    //获取某个节目下，某种类型控件，某个布局id
    public String getAreaId(String pid, String type, String index){
        QueryBuilder<AreaItem> qb = areaItemDao.queryBuilder();
        qb.where(AreaItemDao.Properties.PId.eq(pid),
                AreaItemDao.Properties.AreaType.eq(type),
                AreaItemDao.Properties.AreaIndex.eq(index));
        return qb.build().list().get(0).getAreaId();
    }

    //获取某节目，某布局下素材播放日志
    public PlayLog getResPlayLog(String pid, String resId, String areaId){
        QueryBuilder<PlayLog> qb = playLogDao.queryBuilder();
        qb.where(PlayLogDao.Properties.PId.eq(pid),
                PlayLogDao.Properties.AreaId.eq(areaId),
                PlayLogDao.Properties.ResId.eq(resId));
        return qb.build().list().get(0);
    }

    //获取某节目播放日志
    public PlayLog getProgramPlayLog(String pid){
        QueryBuilder<PlayLog> qb = playLogDao.queryBuilder();
        qb.where(PlayLogDao.Properties.PId.eq(pid), PlayLogDao.Properties.IsProgram.eq(true));
        return qb.build().list().get(0);
    }


//    public List<Program> getAllTodayPrograms(){
//        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.DATE_FORMAT_DATE, Locale.getDefault());
//        String time = sdf.format(new Date(TimeUtil.getCurTimeDate()));
//        Date today;
//        try {
//            today = sdf.parse(time);
//        } catch (ParseException e) {
//            today = new Date(TimeUtil.getCurTimeDate());
//            e.printStackTrace();
//        }
//
//        QueryBuilder<Program> qb = programDao.queryBuilder();
//        qb.where(ProgramDao.Properties.StartDate.le(today),
//                ProgramDao.Properties.EndDate.ge(today)).orderAsc(ProgramDao.Properties.StartTime);
//        return qb.build().list();
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //今日某类型节目列表
    public List<Program> getTodayProgramList(int type, int screen) {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.DATE_FORMAT_DATE, Locale.getDefault());
        String time = sdf.format(new Date(TimeUtil.getCurTimeDate()));
        Date today;
        try {
            today = sdf.parse(time);
        } catch (ParseException e) {
            today = new Date(TimeUtil.getCurTimeDate());
            e.printStackTrace();
        }
        QueryBuilder<Program> qb = programDao.queryBuilder();
        qb.where(ProgramDao.Properties.Screen.eq(screen),
                ProgramDao.Properties.PType.eq(type),
                ProgramDao.Properties.StartDate.le(today),
                ProgramDao.Properties.EndDate.ge(today));
        return qb.build().list();
    }

    public List<TodayProgram> getAllTodayProgram(int screen) {
        QueryBuilder<TodayProgram> qb = todayProgramDao.queryBuilder();
        qb.where(TodayProgramDao.Properties.Screen.eq(screen));
        return qb.build().list();
    }

    public List<TodayProgram> getTodayProgramItem(String time, int screen) {
        QueryBuilder<TodayProgram> qb = todayProgramDao.queryBuilder();
        qb.where(TodayProgramDao.Properties.Screen.eq(screen),
                TodayProgramDao.Properties.StartTime.le(time),
                TodayProgramDao.Properties.EndTime.ge(time));
        return qb.build().list();
    }

    public List<TodayProgram> getOverloadTodayProgram(String startTime, String endTime, int screen) {
        QueryBuilder<TodayProgram> qb = todayProgramDao.queryBuilder();
        qb.where(TodayProgramDao.Properties.Screen.eq(screen),
                TodayProgramDao.Properties.StartTime.ge(startTime),
                TodayProgramDao.Properties.EndTime.le(endTime));
        return qb.build().list();
    }

    ////////////////////////////////////////////垫片素材///////////////////////////////
    //获取垫片节目
    public List<Program> getDPProgram(){
        QueryBuilder<Program> qb = programDao.queryBuilder();
        qb.where(ProgramDao.Properties.PType.eq(Constants.PROGRAM_TYPE_DIANPIAN));
        return qb.build().list();
    }

    //所有垫片视频
    public List<VideoItem> getVideoItemDP(){
        QueryBuilder<VideoItem> qb = videoItemDao.queryBuilder();
        qb.join(VideoItemDao.Properties.PId, Program.class, ProgramDao.Properties.PId)
                .where(ProgramDao.Properties.PType.eq(Constants.PROGRAM_TYPE_DIANPIAN));
        return qb.build().list();
    }

    //所有垫片图片
    public List<ImageItem> getImageItemDP(){
        QueryBuilder<ImageItem> qb = imageItemDao.queryBuilder();
        qb.join(ImageItemDao.Properties.PId, Program.class, ProgramDao.Properties.PId)
                .where(ProgramDao.Properties.PType.eq(Constants.PROGRAM_TYPE_DIANPIAN));
        return qb.build().list();
    }

    //所有垫片滚动文本
    public List<TextItem> getTextItemDP(){
        //查询：要返回数据对应类
        QueryBuilder<TextItem> qb = textItemDao.queryBuilder();
        qb.join(TextItemDao.Properties.PId, Program.class, ProgramDao.Properties.PId)
                .where(ProgramDao.Properties.PType.eq(Constants.PROGRAM_TYPE_DIANPIAN));
        return qb.build().list();
    }

    /////////////////////////////
    public void clearAllTable(){
        clearAreaItem();
        clearImageItem();
        clearProgram();
        clearTextItem();
        clearVideoItem();
        clearPlayLog();
        deleteAllTodayProgram();
    }
}
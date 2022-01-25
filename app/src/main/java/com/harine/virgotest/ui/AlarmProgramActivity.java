package com.harine.virgotest.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.harine.virgotest.Constants;
import com.harine.virgotest.R;
import com.harine.virgotest.data.DBHelper;
import com.harine.virgotest.data.bean.AreaItem;
import com.harine.virgotest.data.bean.ImageItem;
import com.harine.virgotest.data.bean.PlayLog;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.TextItem;
import com.harine.virgotest.data.bean.VideoItem;
import com.harine.virgotest.event.OnStopageChangeEvent;
import com.harine.virgotest.ui.manager.ProgramPage;
import com.harine.virgotest.util.TimeUtil;
import com.nepalese.virgosdk.Base.BaseEventActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

public class AlarmProgramActivity extends BaseEventActivity {
    private static final String TAG = "AlarmProgramActivity";

    private Context context;
    private DBHelper dbHelper;

    private RelativeLayout rootLayout;
    private RelativeLayout layoutPage1, layoutpage2, layoutStop;
    private String imgPath, videoPath;

    private ProgramPage programPage, programPage2;

    private final String hour = "10";
    private final String hour2 = "11";

    private boolean hasStopage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_program);

        init();
        setData();
    }

    private void init() {
        initData();
        createData();
        initUI();
    }

    private void initData() {
        context = getApplicationContext();
        dbHelper = DBHelper.getInstance(context);
        imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Material/Img/";
        videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Material/Video/";
    }

    private void createData() {
        if(dbHelper.hadItem()) return;

        createProgram();
        createArea();
        createDianPian();
        createCrycle();
        createTiming();
        createLog();
    }

    private void createProgram() {
        Program dianpian = new Program();
        dianpian.setPType(Constants.PROGRAM_TYPE_DIANPIAN);
        dianpian.setPId("000");
        dbHelper.saveProgram(dianpian);

        Date start = TimeUtil.string2Date("2021-03-01", TimeUtil.DATE_FORMAT_DATE);
        Date end = TimeUtil.string2Date("2021-04-30", TimeUtil.DATE_FORMAT_DATE);

        Program crycle = new Program();
        crycle.setPType(Constants.PROGRAM_TYPE_CYCLE);
        crycle.setPId("100");
        crycle.setStartDate(start);
        crycle.setEndDate(end);
        crycle.setStartTime("00:00:00");
        crycle.setEndTime("24:00:00");
        crycle.setPTimes(3);
        crycle.setScreen(Constants.SCREEN_MIAN);
        dbHelper.saveProgram(crycle);

        Program timing = new Program();
        timing.setPType(Constants.PROGRAM_TYPE_TIMING);
        timing.setPId("200");
        timing.setStartDate(start);
        timing.setEndDate(end);
        timing.setStartTime(hour + ":30:00");
        timing.setEndTime(hour2 + ":00:00");
        timing.setScreen(Constants.SCREEN_MIAN);
        dbHelper.saveProgram(timing);
    }

    private void createArea() {
        AreaItem dpImg = new AreaItem();
        dpImg.setAreaId("000_dpimg");
        dpImg.setPId("000");
        dpImg.setAreaType(Constants.TYPE_IMP);
        dpImg.setAreaLeft(0f);
        dpImg.setAreaTop(620f);
        dpImg.setAreaWidth(1920f);
        dpImg.setAreaHeigh(460f);
        dbHelper.saveAreaItem(dpImg);

        AreaItem dpText = new AreaItem();
        dpText.setAreaId("000_dptext");
        dpText.setPId("000");
        dpText.setAreaType(Constants.TYPE_SCROLL_TEXT);
        dpText.setAreaLeft(0f);
        dpText.setAreaTop(540f);
        dpText.setAreaWidth(1920f);
        dpText.setAreaHeigh(80f);
        dbHelper.saveAreaItem(dpText);

        AreaItem dpVideo = new AreaItem();
        dpVideo.setAreaId("000_dpvideo");
        dpVideo.setPId("000");
        dpVideo.setAreaType(Constants.TYPE_VIDEO);
        dpVideo.setAreaLeft(0f);
        dpVideo.setAreaTop(0f);
        dpVideo.setAreaWidth(1920f);
        dpVideo.setAreaHeigh(540f);
        dbHelper.saveAreaItem(dpVideo);

        AreaItem cyImg = new AreaItem();
        cyImg.setAreaId("100_cyImg");
        cyImg.setPId("100");
        cyImg.setAreaType(Constants.TYPE_IMP);
        cyImg.setAreaLeft(0f);
        cyImg.setAreaTop(540f);
        cyImg.setAreaWidth(1920f);
        cyImg.setAreaHeigh(460f);
        cyImg.setAreaIndex("0");
        dbHelper.saveAreaItem(cyImg);

        AreaItem cyText = new AreaItem();
        cyText.setAreaId("100_cytext");
        cyText.setPId("100");
        cyText.setAreaType(Constants.TYPE_SCROLL_TEXT);
        cyText.setAreaLeft(0f);
        cyText.setAreaTop(1000f);
        cyText.setAreaWidth(1920f);
        cyText.setAreaHeigh(80f);
        dbHelper.saveAreaItem(cyText);

        AreaItem cyVideo = new AreaItem();
        cyVideo.setAreaId("100_cyVideo");
        cyVideo.setPId("100");
        cyVideo.setAreaType(Constants.TYPE_VIDEO);
        cyVideo.setAreaLeft(0f);
        cyVideo.setAreaTop(0f);
        cyVideo.setAreaWidth(1920f);
        cyVideo.setAreaHeigh(540f);
        cyVideo.setAreaIndex("0");
        dbHelper.saveAreaItem(cyVideo);

        AreaItem tmImg = new AreaItem();
        tmImg.setAreaId("200_tmImg");
        tmImg.setPId("200");
        tmImg.setAreaType(Constants.TYPE_IMP);
        tmImg.setAreaLeft(0f);
        tmImg.setAreaTop(540f);
        tmImg.setAreaWidth(1920f);
        tmImg.setAreaHeigh(540f);
        tmImg.setAreaIndex("0");
        dbHelper.saveAreaItem(tmImg);

        AreaItem tmVideo = new AreaItem();
        tmVideo.setAreaId("200_tmVideo");
        tmVideo.setPId("200");
        tmVideo.setAreaType(Constants.TYPE_VIDEO);
        tmVideo.setAreaLeft(0f);
        tmVideo.setAreaTop(0f);
        tmVideo.setAreaWidth(1920f);
        tmVideo.setAreaHeigh(540f);
        tmVideo.setAreaIndex("0");
        dbHelper.saveAreaItem(tmVideo);
    }

    private void createDianPian() {
        String dpId = "000";
        ImageItem img1 = new ImageItem();
        img1.setPId(dpId);
        img1.setResId("11");
        img1.setIsFull(false);
        img1.setPath(imgPath + "img11.jpg");
        img1.setDuration(30);
        dbHelper.saveImageItem(img1);

        ImageItem img2 = new ImageItem();
        img2.setPId(dpId);
        img2.setResId("12");
        img2.setIsFull(false);
        img2.setPath(imgPath + "img12.jpg");
        img2.setDuration(30);
        dbHelper.saveImageItem(img2);

        ImageItem img3 = new ImageItem();
        img3.setPId(dpId);
        img3.setResId("13");
        img3.setIsFull(false);
        img3.setPath(imgPath + "img13.jpg");
        img3.setDuration(30);
        dbHelper.saveImageItem(img3);

        VideoItem video1 = new VideoItem();
        video1.setPId(dpId);
        video1.setResId("21");
        video1.setIsFull(false);
        video1.setPath(videoPath + "video11.mp4");
        dbHelper.saveVideoItem(video1);

        VideoItem video2 = new VideoItem();
        video2.setPId(dpId);
        video2.setResId("22");
        video2.setIsFull(false);
        video2.setPath(videoPath + "video12.mp4");
        dbHelper.saveVideoItem(video2);

        TextItem text1 = new TextItem();
        text1.setPId(dpId);
        text1.setResId("31");
        text1.setContent("富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善");
        dbHelper.saveTextItem(text1);
    }

    private void createCrycle(){
        //循环
        String cyId = "100";

        ImageItem img1 = new ImageItem();
        img1.setPId(cyId);
        img1.setResId("img1");
        img1.setAreaId("100_cyImg");
        img1.setIsFull(false);
        img1.setPath(imgPath + "img1.jpg");
        img1.setDuration(20);
        img1.setTimes(6);
        dbHelper.saveImageItem(img1);

        ImageItem img2 = new ImageItem();
        img2.setPId(cyId);
        img2.setResId("img2");
        img2.setAreaId("100_cyImg");
        img2.setIsFull(false);
        img2.setPath(imgPath + "img2.jpg");
        img2.setDuration(30);
        img2.setTimes(3);
        dbHelper.saveImageItem(img2);

        ImageItem img3 = new ImageItem();
        img3.setPId(cyId);
        img3.setResId("img3");
        img3.setAreaId("100_cyImg");
        img3.setIsFull(false);
        img3.setPath(imgPath + "img3.jpg");
        img3.setDuration(50);
        img3.setTimes(5);
        dbHelper.saveImageItem(img3);

        ImageItem img4 = new ImageItem();
        img4.setPId(cyId);
        img4.setResId("img4");
        img4.setAreaId("100_cyImg");
        img4.setIsFull(false);
        img4.setPath(imgPath + "img4.jpg");
        img4.setDuration(10);
        img4.setTimes(8);
        dbHelper.saveImageItem(img4);

        ImageItem img5 = new ImageItem();
        img5.setPId(cyId);
        img5.setResId("img5");
        img5.setAreaId("100_cyImg");
        img5.setIsFull(false);
        img5.setPath(imgPath + "img5.jpg");
        img5.setDuration(30);
        img5.setTimes(7);
        dbHelper.saveImageItem(img5);

        ImageItem img6 = new ImageItem();
        img6.setPId(cyId);
        img6.setResId("img6");
        img6.setAreaId("100_cyImg");
        img6.setIsFull(false);
        img6.setPath(imgPath + "img6.jpg");
        img6.setDuration(30);
        img6.setTimes(2);
        dbHelper.saveImageItem(img6);

        VideoItem video1 = new VideoItem();
        video1.setPId(cyId);
        video1.setResId("video1");
        video1.setAreaId("100_cyVideo");
        video1.setIsFull(false);
        video1.setPath(videoPath + "video4.mp4");
        video1.setTimes(4);
        dbHelper.saveVideoItem(video1);

        VideoItem video2 = new VideoItem();
        video2.setPId(cyId);
        video2.setResId("video2");
        video2.setAreaId("100_cyVideo");
        video2.setIsFull(false);
        video2.setPath(videoPath + "video5.mp4");
        video2.setTimes(3);
        dbHelper.saveVideoItem(video2);

        VideoItem video3 = new VideoItem();
        video3.setPId(cyId);
        video3.setResId("video3");
        video3.setAreaId("100_cyVideo");
        video3.setIsFull(false);
        video3.setPath(videoPath + "video6.mp4");
        video3.setTimes(2);
        dbHelper.saveVideoItem(video3);

        TextItem text1 = new TextItem();
        text1.setPId(cyId);
        text1.setResId("text1");
        text1.setDuration(120);
        text1.setContent("君不见，黄河之水天上来，奔流到海不复回。君不见，高堂明镜悲白发，朝如青丝暮成雪。人生得意须尽欢，莫使金樽空对月。天生我材必有用，千金散尽还复来。烹羊宰牛且为乐，会须一饮三百杯。" +
                "岑夫子，丹丘生，将进酒，杯莫停。与君歌一曲，请君为我倾耳听。钟鼓馔玉不足贵，但愿长醉不复醒。不复醒 一作：不愿醒/不用醒)古来圣贤皆寂寞，惟有饮者留其名。陈王昔时宴平乐，斗酒十千恣欢谑。主人何为言少钱，径须沽取对君酌。五花马，千金裘，呼儿将出换美酒，与尔同销万古愁。");
        dbHelper.saveTextItem(text1);

        TextItem text2 = new TextItem();
        text2.setPId(cyId);
        text2.setResId("text2");
        text2.setDuration(60);
        text2.setContent("青青子衿，悠悠我心。纵我不往，子宁不嗣音？青青子佩，悠悠我思。纵我不往，子宁不来？跳兮达兮，在城阙兮。一日不见，如三月兮。");
        dbHelper.saveTextItem(text2);
    }

    //仅对有次数要求的循环节目
    private void createLog(){
        String cyId = "100";

        PlayLog programLog = new PlayLog();
        programLog.setPId(cyId);
        programLog.setIsProgram(true);
        programLog.setToday(TimeUtil.getToday());
        programLog.setPlayTimes(0);
        dbHelper.savePlayLog(programLog);

        //所有图片，视频素材
        String imgAreaId = "100_cyImg";
        List<ImageItem> imgList = dbHelper.getImageItemPid(cyId, imgAreaId);
        for(ImageItem item: imgList){
            PlayLog imgLog = new PlayLog();
            imgLog.setPId(cyId);
            imgLog.setResId(item.getResId());
            imgLog.setAreaId(imgAreaId);
            imgLog.setIsProgram(false);
            imgLog.setPlayTimes(0);
            dbHelper.savePlayLog(imgLog);
        }

        String videoAredId = "100_cyVideo";
        List<VideoItem> videoList = dbHelper.getVideoItemPid(cyId, videoAredId);
        for(VideoItem item: videoList){
            PlayLog videoLog = new PlayLog();
            videoLog.setPId(cyId);
            videoLog.setResId(item.getResId());
            videoLog.setAreaId(videoAredId);
            videoLog.setIsProgram(false);
            videoLog.setPlayTimes(0);
            dbHelper.savePlayLog(videoLog);
        }
    }
    
    private void createTiming(){
        String tmId = "200";

        ImageItem img1 = new ImageItem();
        img1.setPId(tmId);
        img1.setResId("2");
        img1.setAreaId("200_tmImg");
        img1.setIsFull(false);
        img1.setPath(imgPath + "img2.jpg");
        img1.setStartTime(hour+":30:00");
        img1.setEndTime(hour+":32:00");
        dbHelper.saveImageItem(img1);

        ImageItem img2 = new ImageItem();
        img2.setPId(tmId);
        img2.setResId("3");
        img2.setAreaId("200_tmImg");
        img2.setIsFull(false);
        img2.setPath(imgPath + "img3.jpg");
        img2.setStartTime(hour+":32:00");
        img2.setEndTime(hour+":35:00");
        dbHelper.saveImageItem(img2);

        ImageItem img3 = new ImageItem();
        img3.setPId(tmId);
        img3.setResId("5");
        img3.setAreaId("200_tmImg");
        img3.setIsFull(false);
        img3.setPath(imgPath + "img5.jpg");
        img3.setStartTime(hour+":35:00");
        img3.setEndTime(hour+":38:00");
        dbHelper.saveImageItem(img3);

        ImageItem img4 = new ImageItem();
        img4.setPId(tmId);
        img4.setResId("4");
        img4.setAreaId("200_tmImg");
        img4.setIsFull(false);
        img4.setPath(imgPath + "img4.jpg");
        img4.setStartTime(hour+":38:00");
        img4.setEndTime(hour+":40:00");
        dbHelper.saveImageItem(img4);

        ImageItem img5 = new ImageItem();
        img5.setPId(tmId);
        img5.setResId("6");
        img5.setAreaId("200_tmImg");
        img5.setIsFull(false);
        img5.setPath(imgPath + "img6.jpg");
        img5.setStartTime(hour+":40:00");
        img5.setEndTime(hour+":44:00");
        dbHelper.saveImageItem(img5);

        ImageItem img6 = new ImageItem();
        img6.setPId(tmId);
        img6.setResId("2");
        img6.setAreaId("200_tmImg");
        img6.setIsFull(false);
        img6.setPath(imgPath + "img2.jpg");
        img6.setStartTime(hour+":44:00");
        img6.setEndTime(hour+":45:00");
        dbHelper.saveImageItem(img6);

        //45-50 kongbai

        //50-55 垫片

        ImageItem img7 = new ImageItem();
        img7.setPId(tmId);
        img7.setResId("1");
        img7.setAreaId("200_tmImg");
        img7.setIsFull(false);
        img7.setPath(imgPath + "img1.jpg");
        img7.setStartTime(hour+":55:00");
        img7.setEndTime(hour+":57:00");
        dbHelper.saveImageItem(img7);

        ImageItem img8 = new ImageItem();
        img8.setPId(tmId);
        img8.setResId("3");
        img8.setAreaId("200_tmImg");
        img8.setIsFull(false);
        img8.setPath(imgPath + "img3.jpg");
        img8.setStartTime(hour+":57:00");
        img8.setEndTime(hour2+":00:00");
        dbHelper.saveImageItem(img8);


        VideoItem video21 = new VideoItem();
        video21.setPId(tmId);
        video21.setResId("4");
        video21.setAreaId("200_tmVideo");
        video21.setIsFull(false);
        video21.setPath(videoPath + "video4.mp4");
        video21.setStartTime(hour+":30:00");
        video21.setEndTime(hour+":32:00");
        dbHelper.saveVideoItem(video21);

        VideoItem video22 = new VideoItem();
        video22.setPId(tmId);
        video22.setResId("5");
        video22.setAreaId("200_tmVideo");
        video22.setIsFull(false);
        video22.setPath(videoPath + "video5.mp4");
        video22.setStartTime(hour+":32:00");
        video22.setEndTime(hour+":33:00");
        dbHelper.saveVideoItem(video22);

        VideoItem video23 = new VideoItem();
        video23.setPId(tmId);
        video23.setResId("6");
        video23.setAreaId("200_tmVideo");
        video23.setIsFull(false);
        video23.setPath(videoPath + "video6.mp4");
        video23.setStartTime(hour+":33:00");
        video23.setEndTime(hour+":34:00");
        dbHelper.saveVideoItem(video23);

        VideoItem video24 = new VideoItem();
        video24.setPId(tmId);
        video24.setResId("3");
        video24.setAreaId("200_tmVideo");
        video24.setIsFull(false);
        video24.setPath(videoPath + "video3.mp4");
        video24.setStartTime(hour+":34:00");
        video24.setEndTime(hour+":35:00");
        dbHelper.saveVideoItem(video24);

        //35-40 kongbai

        VideoItem video25 = new VideoItem();
        video25.setPId(tmId);
        video25.setResId("2");
        video25.setAreaId("200_tmVideo");
        video25.setIsFull(false);
        video25.setPath(videoPath + "video2.mp4");
        video25.setStartTime(hour+":40:00");
        video25.setEndTime(hour+":40:30");
        dbHelper.saveVideoItem(video25);

        VideoItem video26 = new VideoItem();
        video26.setPId(tmId);
        video26.setResId("5");
        video26.setAreaId("200_tmVideo");
        video26.setIsFull(false);
        video26.setPath(videoPath + "video5.mp4");
        video26.setStartTime(hour+":40:30");
        video26.setEndTime(hour+":41:00");
        dbHelper.saveVideoItem(video26);

        VideoItem video27 = new VideoItem();
        video27.setPId(tmId);
        video27.setResId("4");
        video27.setAreaId("200_tmVideo");
        video27.setIsFull(false);
        video27.setPath(videoPath + "video4.mp4");
        video27.setStartTime(hour+":41:00");
        video27.setEndTime(hour+":43:00");
        dbHelper.saveVideoItem(video27);

        VideoItem video28 = new VideoItem();
        video28.setPId(tmId);
        video28.setResId("3");
        video28.setAreaId("200_tmVideo");
        video28.setIsFull(false);
        video28.setPath(videoPath + "video3.mp4");
        video28.setStartTime(hour+":43:00");
        video28.setEndTime(hour+":45:00");
        dbHelper.saveVideoItem(video28);

        //45-50 full
        VideoItem video29 = new VideoItem();
        video29.setPId(tmId);
        video29.setResId("1");
        video29.setAreaId("200_tmVideo");
        video29.setIsFull(true);
        video29.setPath(videoPath + "video1.mp4");
        video29.setStartTime(hour+":45:00");
        video29.setEndTime(hour+":50:00");
        dbHelper.saveVideoItem(video29);

        VideoItem video30 = new VideoItem();
        video30.setPId(tmId);
        video30.setResId("2");
        video30.setAreaId("200_tmVideo");
        video30.setIsFull(false);
        video30.setPath(videoPath + "video2.mp4");
        video30.setStartTime(hour+":55:00");
        video30.setEndTime(hour+":55:30");
        dbHelper.saveVideoItem(video30);

        VideoItem video31 = new VideoItem();
        video31.setPId(tmId);
        video31.setResId("5");
        video31.setAreaId("200_tmVideo");
        video31.setIsFull(false);
        video31.setPath(videoPath + "video5.mp4");
        video31.setStartTime(hour+":55:30");
        video31.setEndTime(hour+":56:00");
        dbHelper.saveVideoItem(video31);

        VideoItem video32 = new VideoItem();
        video32.setPId(tmId);
        video32.setResId("4");
        video32.setAreaId("200_tmVideo");
        video32.setIsFull(false);
        video32.setPath(videoPath + "video4.mp4");
        video32.setStartTime(hour+":56:00");
        video32.setEndTime(hour+":58:00");
        dbHelper.saveVideoItem(video32);

        VideoItem video33 = new VideoItem();
        video33.setPId(tmId);
        video33.setResId("3");
        video33.setAreaId("200_tmVideo");
        video33.setIsFull(false);
        video33.setPath(videoPath + "video3.mp4");
        video33.setStartTime(hour+":58:00");
        video33.setEndTime(hour2+":00:00");
        dbHelper.saveVideoItem(video33);
    }

    private void initUI() {
        rootLayout = findViewById(R.id.layoutRootMain);
        layoutPage1 = findViewById(R.id.layoutPage1);
        layoutpage2 = findViewById(R.id.layoutPage2);
        layoutStop = findViewById(R.id.layoutStop);

        layoutPage1.setBackgroundColor(Color.TRANSPARENT);
        layoutpage2.setBackgroundColor(Color.TRANSPARENT);
        layoutStop.setBackgroundColor(Color.TRANSPARENT);

        programPage = new ProgramPage(layoutPage1, 1920, 1080, Constants.SCREEN_MIAN);
        programPage.startPlay();

        programPage2 = new ProgramPage(layoutpage2, 1920, 1080, Constants.SCREEN_SECOND);
        programPage2.startPlay();

        showStopage();
    }

    private void setData() {

    }

    private void showStopage(){
        if(hasStopage){
            layoutStop.setVisibility(View.VISIBLE);
            stopPlay();
        }else{
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(lp);
            imageView.setImageResource(R.mipmap.img_cover);
            layoutStop.addView(imageView);
        }
        hasStopage = true;
    }

    private void hideStopage(){
        layoutStop.setVisibility(View.GONE);
        hasStopage = false;
    }

    private void stopPlay(){
        if(programPage!=null){
            programPage.stopPlay();
        }

        if(programPage2!=null){
            programPage2.stopPlay();
        }
    }

    private void continuePlay(){
        hideStopage();
        if(programPage!=null){
            programPage.continuePlay();
        }

        if(programPage2!=null){
            programPage2.continuePlay();
        }
    }
    ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        programPage.release();
        programPage2.release();
    }

    @Subscribe
    public void onThreadMainEvent(OnStopageChangeEvent event){
        Log.i(TAG, "OnStopageChangeEvent: 隐藏 stoppage");
        hideStopage();
    }
}
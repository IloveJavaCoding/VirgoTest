package com.harine.virgotest.data.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.harine.virgotest.data.bean.AreaItem;
import com.harine.virgotest.data.bean.ImageItem;
import com.harine.virgotest.data.bean.PlayLog;
import com.harine.virgotest.data.bean.Program;
import com.harine.virgotest.data.bean.TextItem;
import com.harine.virgotest.data.bean.TodayProgram;
import com.harine.virgotest.data.bean.VideoItem;

import com.harine.virgotest.data.db.AreaItemDao;
import com.harine.virgotest.data.db.ImageItemDao;
import com.harine.virgotest.data.db.PlayLogDao;
import com.harine.virgotest.data.db.ProgramDao;
import com.harine.virgotest.data.db.TextItemDao;
import com.harine.virgotest.data.db.TodayProgramDao;
import com.harine.virgotest.data.db.VideoItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig areaItemDaoConfig;
    private final DaoConfig imageItemDaoConfig;
    private final DaoConfig playLogDaoConfig;
    private final DaoConfig programDaoConfig;
    private final DaoConfig textItemDaoConfig;
    private final DaoConfig todayProgramDaoConfig;
    private final DaoConfig videoItemDaoConfig;

    private final AreaItemDao areaItemDao;
    private final ImageItemDao imageItemDao;
    private final PlayLogDao playLogDao;
    private final ProgramDao programDao;
    private final TextItemDao textItemDao;
    private final TodayProgramDao todayProgramDao;
    private final VideoItemDao videoItemDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        areaItemDaoConfig = daoConfigMap.get(AreaItemDao.class).clone();
        areaItemDaoConfig.initIdentityScope(type);

        imageItemDaoConfig = daoConfigMap.get(ImageItemDao.class).clone();
        imageItemDaoConfig.initIdentityScope(type);

        playLogDaoConfig = daoConfigMap.get(PlayLogDao.class).clone();
        playLogDaoConfig.initIdentityScope(type);

        programDaoConfig = daoConfigMap.get(ProgramDao.class).clone();
        programDaoConfig.initIdentityScope(type);

        textItemDaoConfig = daoConfigMap.get(TextItemDao.class).clone();
        textItemDaoConfig.initIdentityScope(type);

        todayProgramDaoConfig = daoConfigMap.get(TodayProgramDao.class).clone();
        todayProgramDaoConfig.initIdentityScope(type);

        videoItemDaoConfig = daoConfigMap.get(VideoItemDao.class).clone();
        videoItemDaoConfig.initIdentityScope(type);

        areaItemDao = new AreaItemDao(areaItemDaoConfig, this);
        imageItemDao = new ImageItemDao(imageItemDaoConfig, this);
        playLogDao = new PlayLogDao(playLogDaoConfig, this);
        programDao = new ProgramDao(programDaoConfig, this);
        textItemDao = new TextItemDao(textItemDaoConfig, this);
        todayProgramDao = new TodayProgramDao(todayProgramDaoConfig, this);
        videoItemDao = new VideoItemDao(videoItemDaoConfig, this);

        registerDao(AreaItem.class, areaItemDao);
        registerDao(ImageItem.class, imageItemDao);
        registerDao(PlayLog.class, playLogDao);
        registerDao(Program.class, programDao);
        registerDao(TextItem.class, textItemDao);
        registerDao(TodayProgram.class, todayProgramDao);
        registerDao(VideoItem.class, videoItemDao);
    }
    
    public void clear() {
        areaItemDaoConfig.clearIdentityScope();
        imageItemDaoConfig.clearIdentityScope();
        playLogDaoConfig.clearIdentityScope();
        programDaoConfig.clearIdentityScope();
        textItemDaoConfig.clearIdentityScope();
        todayProgramDaoConfig.clearIdentityScope();
        videoItemDaoConfig.clearIdentityScope();
    }

    public AreaItemDao getAreaItemDao() {
        return areaItemDao;
    }

    public ImageItemDao getImageItemDao() {
        return imageItemDao;
    }

    public PlayLogDao getPlayLogDao() {
        return playLogDao;
    }

    public ProgramDao getProgramDao() {
        return programDao;
    }

    public TextItemDao getTextItemDao() {
        return textItemDao;
    }

    public TodayProgramDao getTodayProgramDao() {
        return todayProgramDao;
    }

    public VideoItemDao getVideoItemDao() {
        return videoItemDao;
    }

}

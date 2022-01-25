package com.harine.virgotest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.harine.virgotest.data.db.DaoMaster;

/**
 * @author nepalese on 2021/3/16 16:48
 * @usage
 */
public class DatabaseOpenHelper extends DaoMaster.DevOpenHelper {
    //数据库名：自定义
    public static final String DATABASE_NAME = "VirgoTest.db";

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
}
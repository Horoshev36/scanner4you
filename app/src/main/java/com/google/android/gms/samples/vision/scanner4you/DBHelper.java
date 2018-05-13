package com.google.android.gms.samples.vision.scanner4you;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATEBASE_NAME = "contactBD";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "_id";
    public static final String KEY_CHAR = "mainvalue";
    public static final String KEY_VALUE = "value";
    public static final String KEY_VALUE2 = "value2";
    public static final String KEY_VALUE3 = "value3";

    public DBHelper(Context context) {
        super(context, DATEBASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_CONTACTS+"("+KEY_ID+" int primary key,"
                + KEY_CHAR+" text,"
                + KEY_VALUE+" text,"
                + KEY_VALUE2+" text,"
                + KEY_VALUE3+" text"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS );

        onCreate(db);
    }
}

package com.google.android.gms.samples.vision.scanner4you;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATEBASE_NAME = "contactBD";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "id";
    public static final String KEY_Serial = "Serial";
    public static final String KEY_Type = "Type";
    public static final String KEY_Model = "Model";
    public static final String KEY_Inventary = "Inventary";
    public static final String KEY_Department = "Department";

    public DBHelper(Context context) {
        super(context, DATEBASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_CONTACTS+"("+KEY_ID+" int primary key,"
                + KEY_Serial+" text,"
                + KEY_Type+" text,"
                + KEY_Model+" text,"
                + KEY_Inventary+" text,"
                + KEY_Department+" text"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS );

        onCreate(db);
    }
}

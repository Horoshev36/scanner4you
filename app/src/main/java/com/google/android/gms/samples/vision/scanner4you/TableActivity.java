package com.google.android.gms.samples.vision.scanner4you;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class TableActivity extends Activity  {


    DBHelper dbHelper;
    TextView big_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);


        big_text= findViewById(R.id.big_text);
        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String mLog =("SQLite  \n\n");

        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int charvalue = cursor.getColumnIndex(DBHelper.KEY_Serial);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_Type);
            int value2Index = cursor.getColumnIndex(DBHelper.KEY_Model);
            int value3Index = cursor.getColumnIndex(DBHelper.KEY_Inventary);
            int value4Index = cursor.getColumnIndex(DBHelper.KEY_Department);
            int value5Index = cursor.getColumnIndex(DBHelper.KEY_ID);
            do {

                mLog += " Серийный номер: " + cursor.getString(charvalue) + "\n" +
                        " Тип оборудования: " + cursor.getString(valueIndex) + "\n" +
                        " Модель: " + cursor.getString(value2Index) + "\n" +
                        " Инвентарный номер: " + cursor.getString(value3Index) + "\n" +
                        " Расположение: " + cursor.getString(value4Index) + "\n" +
                        " ID: " + cursor.getString(value5Index) + "\n\n";

            } while (cursor.moveToNext());

        } else
            Log.d("mLog","0 rows");


        big_text.setText(mLog);
        cursor.close();
        dbHelper.close();
    }
}
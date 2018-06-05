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
                mLog=null;
                mLog = "Char = " + cursor.getString(charvalue) + "\n" +
                        " value = " + cursor.getString(valueIndex) + "\n" +
                        " value2 = " + cursor.getString(value2Index) + "\n" +
                        " value3 = " + cursor.getString(value3Index) + "\n" +
                        " value4 = " + cursor.getString(value4Index) + "\n" +
                        " value5 = " + cursor.getString(value5Index) + "\n\n";
                big_text.setText(mLog);
            } while (cursor.moveToNext());

        } else
            Log.d("mLog","0 rows");


        big_text.setText(mLog);
        cursor.close();
        dbHelper.close();
    }
}
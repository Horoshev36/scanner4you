package com.google.android.gms.samples.vision.scanner4you;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.samples.vision.scanner4you.R;


public class TableActivity extends Activity  {


    DBHelper dbHelper;
    TextView big_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);


        big_text= (TextView) findViewById(R.id.big_text);
        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String mLog =("SQLite  \n\n");

        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int charvalue = cursor.getColumnIndex(DBHelper.KEY_CHAR);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
            int value2Index = cursor.getColumnIndex(DBHelper.KEY_VALUE2);
            int value3Index = cursor.getColumnIndex(DBHelper.KEY_VALUE3);
            do {
                Log.d("mLog", "Char = " + cursor.getString(charvalue) +
                        ", value = " + cursor.getString(valueIndex) +
                        ", value2 = " + cursor.getString(value2Index)+
                        ", value3 = " + cursor.getString(value3Index));
                mLog += "Char = " + cursor.getString(charvalue) + "\n" + " value = " + cursor.getString(valueIndex) + "\n" + " value2 = " + cursor.getString(value2Index) + "\n" + " value3 = " + cursor.getString(value3Index) + "\n\n";

            } while (cursor.moveToNext());

        } else
            Log.d("mLog","0 rows");


        big_text.setText(mLog);
        cursor.close();
        dbHelper.close();
    }
}
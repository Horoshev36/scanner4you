package com.google.android.gms.samples.vision.scanner4you;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

public class activity_export extends AppCompatActivity implements View.OnClickListener {
  // DBHelper dbHelper;
  // SQLiteDatabase database = dbHelper.getWritableDatabase();

    ContentValues contentvalues = new ContentValues();
 public    CompoundButton swt_code;
 public static boolean CHEEP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        findViewById(R.id.btn_export_form).setOnClickListener(this);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        swt_code = findViewById(R.id.swt_code);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_export_form:
                    if (swt_code.isChecked()){
                        CHEEP=swt_code.isChecked();
                        exportDB(CHEEP);
            }else {
                        CHEEP=swt_code.isChecked();
                        exportDB(CHEEP);
                    }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void exportDB(boolean CHEEP) {

        File dbFile=getDatabasePath("contactBD.db");
        DBHelper dbhelper = new DBHelper(getApplicationContext());
        File exportDir = new File("/sdcard/my-logs", "");

        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "pouExit.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite=null;

            if (CHEEP) {
                csvWrite = new CSVWriter(new FileWriter(file), '*');
            }else {
                csvWrite = new CSVWriter(new FileWriter(file), ';');
            }
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM contacts",null);
            //csvWrite.writeNext(curCSV.getColumnNames());
            String arrStr[];
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort

                if (CHEEP) {

                     arrStr = new String[]{SecuritySettings.encrypt(curCSV.getString(4)), SecuritySettings.encrypt(curCSV.getString(1)),SecuritySettings.encrypt( curCSV.getString(2)), SecuritySettings.encrypt(curCSV.getString(3))};

                }else {

                     arrStr = new String[]{curCSV.getString(4), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};

                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            new Message("Success!");
        } catch(Exception sqlEx){
            Log.e("mLog", sqlEx.getMessage(), sqlEx);
            new Message("Error");
        }
    }
    public class Message {
        Message(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }




}
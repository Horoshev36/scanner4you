package com.google.android.gms.samples.vision.scanner4you;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import static org.apache.commons.lang3.BooleanUtils.and;

public class activity_import extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_BARCODE_CAPTURE = 9001;




    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        findViewById(R.id.btn_export_form).setOnClickListener(this);
        ContentValues contentvalues = new ContentValues();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ContentValues contentvalues = new ContentValues();
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String str = barcode.displayValue;
                    String[] subStr;
                    String delimeter = ";"; // Разделитель
                    subStr = str.split(delimeter);//subStr[i]= массив с данными строки (разбитый)

                    for (int i = 0; i < subStr.length; i++) {
                        switch (i) {
                            case 0:
                                if (subStr[i].length() == 1) {
                                    contentvalues.put(DBHelper.KEY_CHAR, subStr[i]);
                                }
                                break;
                            case 1:
                                if (subStr[i].length() == 4) {
                                    contentvalues.put(DBHelper.KEY_VALUE, subStr[i]);
                                }
                                break;
                            case 2:
                                if (subStr[i].length() == 3) {
                                    contentvalues.put(DBHelper.KEY_VALUE2, subStr[i]);
                                }
                                break;
                            case 3:
                                if (subStr[i].length() == 7) {
                                    contentvalues.put(DBHelper.KEY_VALUE3, subStr[i]);
                                }
                                break;
                            default:
                                break;
                        }

                    }
                    if (str.length() == 18 && subStr[0] != null && subStr[1] != null && subStr[2] != null&& subStr[3] != null) {
                        insertOrUpdate(contentvalues);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_export_form:

                break;
        }
    }





    public void insertOrUpdate(ContentValues cv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int id = getID(cv);
        if (id == -1)
            db.insert(DBHelper.TABLE_CONTACTS, null, cv);
        else
            db.update(DBHelper.TABLE_CONTACTS, cv, "_id=?", new String[]{Integer.toString(id)});
    }

    private int getID(ContentValues cv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.TABLE_CONTACTS, new String[]{"_id"}, "mainvalue =? AND value3=?",
                new String[]{cv.getAsString(DBHelper.KEY_CHAR), cv.getAsString(DBHelper.KEY_VALUE3)}, null, null, null, null);

        if (c.moveToFirst()) //if the row exist then return the id
            return c.getInt(c.getColumnIndex("_id"));
        return -1;
    }
}
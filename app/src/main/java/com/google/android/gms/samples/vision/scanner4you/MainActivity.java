package com.google.android.gms.samples.vision.scanner4you;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    DBHelper dbHelper;
    Button btn_table;
    Button btn_import;
    Button btn_export;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        btn_table = (Button) findViewById(R.id.btn_table);
        btn_import = (Button) findViewById(R.id.btn_import);
        btn_export = (Button) findViewById(R.id.btn_export);

        findViewById(R.id.btn_table).setOnClickListener(this);
        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.btn_import).setOnClickListener(this);
        findViewById(R.id.btn_export).setOnClickListener(this);


        autoFocus.setChecked(true);

        dbHelper = new DBHelper(this);
    }


    @Override
    public void onClick(View v) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //main
        switch (v.getId()) {
            case R.id.read_barcode:
                // launch barcode activity.
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                break;

            case R.id.btn_table:
                // launch TABLE activity
                intent = new Intent(this, TableActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_import:
                Log.d("mLog", "Key insert pressed");
                Insert insert = new Insert(MainActivity.this);
                break;

            case R.id.btn_export:
                //Need more program code
                break;

            default:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    barcodeValue.setText(barcode.displayValue);
                    Log.d("mLog", "Barcode read: " + barcode.displayValue);


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
                    insertOrUpdate(contentvalues);
                    //database.insert(DBHelper.TABLE_CONTACTS, null, contentvalues);

                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d("mLog", "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        dbHelper.close();
    }
        public class Insert {

            Insert(Context context){
            AssetManager manager = context.getAssets();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //чтение файла
            String mCSVfile = "stats.csv";

            InputStream inStream = null;
            try {
                inStream = manager.open(mCSVfile);
            } catch (IOException e) {
                Log.d("mLog", "Ошибка");
                e.printStackTrace();
            }
            //портирование csv в SQLite
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
            String line = "";
            db.beginTransaction();
            try {
                while ((line = buffer.readLine()) != null) {
                    String[] colums = line.split(";");
                    if (colums.length != 4) {
                        Log.d("mLog", "Skipping Bad CSV Row");
                        continue;
                    }
                    ContentValues contentvalues = new ContentValues(3);


                    contentvalues.put(DBHelper.KEY_CHAR, colums[0].trim());
                    contentvalues.put(DBHelper.KEY_VALUE, colums[1].trim());
                    contentvalues.put(DBHelper.KEY_VALUE2, colums[2].trim());
                    contentvalues.put(DBHelper.KEY_VALUE3, colums[3].trim());

                    insertOrUpdate(contentvalues);
                    //db.insert(DBHelper.TABLE_CONTACTS, null, contentvalues);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
}
    public void insertOrUpdate(ContentValues cv){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int id = getID(cv);
        if(id==-1)
            db.insert(DBHelper.TABLE_CONTACTS, null, cv);
        else
            db.update(DBHelper.TABLE_CONTACTS, cv, "_id=?", new String[]{Integer.toString(id)});
    }



    private int getID(ContentValues cv){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.TABLE_CONTACTS,new String[]{"_id"}, "mainvalue =? AND value3=?",
                new String[]{cv.getAsString(DBHelper.KEY_CHAR),cv.getAsString(DBHelper.KEY_VALUE3)},null,null,null,null);

        if (c.moveToFirst()) //if the row exist then return the id
            return c.getInt(c.getColumnIndex("_id"));
        return -1;
    }


}




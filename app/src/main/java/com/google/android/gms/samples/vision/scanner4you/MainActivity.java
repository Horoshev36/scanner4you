package com.google.android.gms.samples.vision.scanner4you;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.Telephony.Mms.Part.FILENAME;
import com.opencsv.CSVWriter;


public class MainActivity extends Activity implements View.OnClickListener {

    DBHelper dbHelper;
    Button btn_table;
    Button btn_import;
    Button btn_export;
    String FILENAME_INPUT = "/storage/32FC-8A50/Android/data/com.google.android.gms/files/pou.csv";
    String FILENAME_EXPORT = "/storage/32FC-8A50/Android/data/com.google.android.gms/files/pouExit.csv";
    //String FILENAME_INPUT = "pou.csv";
    //String FILENAME_EXPORT = "pouExit.csv";

    public static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
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

        statusMessage = findViewById(R.id.status_message);
        barcodeValue = findViewById(R.id.barcode_value);

        autoFocus = findViewById(R.id.auto_focus);
        useFlash = findViewById(R.id.use_flash);

        btn_table = findViewById(R.id.btn_table);
        btn_import = findViewById(R.id.btn_import);
        btn_export = findViewById(R.id.btn_export);

        findViewById(R.id.btn_table).setOnClickListener(this);
        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.btn_import).setOnClickListener(this);
        findViewById(R.id.btn_export).setOnClickListener(this);


        autoFocus.setChecked(true);

        dbHelper = new DBHelper(this);
        int permissionStatusW = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatusW == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
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
                InsertFile insertFile = new InsertFile();


                //Insert insert = new Insert(MainActivity.this);
                break;

            case R.id.btn_export:
                //Need more program code
                //ExportFile();
                exportDB();

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
                    if (str.length() == 18) {
                        insertOrUpdate(contentvalues);
                    }
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

    public class InsertFile {
        InsertFile() {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String s = "";
            new Message("Установка буффера");
            int permissionStatusR = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionStatusR == PackageManager.PERMISSION_GRANTED) {
                try (BufferedReader br = new BufferedReader(new FileReader
                        (FILENAME_INPUT))) {
                    //чтение построчно
                    new Message("Начало портирования");
                    db.beginTransaction();
                    while ((s = br.readLine()) != null) {
                        String[] colums = s.split(";");
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

                        System.out.println(s);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    new Message("Конец портирования");
                } catch (IOException ex) {

                    System.out.println(ex.getMessage());
                }

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }
    }

    public class Message {
        Message(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    public class Insert {
        Insert(Context context) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //чтение файла
            AssetManager manager = context.getAssets();
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
            Toast.makeText(getApplicationContext(), "Начало портирования", Toast.LENGTH_SHORT).show();
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Toast.makeText(getApplicationContext(), "Конец портирования", Toast.LENGTH_SHORT).show();
        }
    }


    void ExportFile() {
        dbHelper = new DBHelper(MainActivity.this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String mLog = ("SQLite  \n\n");

        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int charvalue = cursor.getColumnIndex(DBHelper.KEY_CHAR);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
            int value2Index = cursor.getColumnIndex(DBHelper.KEY_VALUE2);
            int value3Index = cursor.getColumnIndex(DBHelper.KEY_VALUE3);
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(FILENAME_EXPORT, MODE_PRIVATE)));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            do {
                Log.d("mLog", "Char = " + cursor.getString(charvalue) +
                        ", value = " + cursor.getString(valueIndex) +
                        ", value2 = " + cursor.getString(value2Index) +
                        ", value3 = " + cursor.getString(value3Index));
                mLog += "Char = " + cursor.getString(charvalue) + "\n" + " value = " + cursor.getString(valueIndex) + "\n" + " value2 = " + cursor.getString(value2Index) + "\n" + " value3 = " + cursor.getString(value3Index) + "\n\n";
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput(FILENAME_EXPORT, MODE_APPEND)));
                    bw.write(cursor.getString(charvalue) + ";" + cursor.getString(valueIndex) + ";" + cursor.getString(value2Index) + ";" + cursor.getString(value3Index));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            Log.d("mLog", "Файл записан");
            new Message("Файл записан");
        } else
            Log.d("mLog", "0 rows");
        new Message("База пустая проверьте данные!");
    }

    void exportDB() {

        int permissionStatusW = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatusW == PackageManager.PERMISSION_GRANTED) {
            DBHelper dbhelper = new DBHelper(MainActivity.this);
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
                Log.d("mLog", "Skipping Bad CSV Row "+exportDir.mkdirs());
            }
            File file = new File(exportDir, "pouExit.csv");//FILENAME_EXPORT);
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = dbhelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM contacts", null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    //Which column you want to exprort
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
            } catch (Exception sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }
}





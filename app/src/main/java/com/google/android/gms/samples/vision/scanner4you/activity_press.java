package com.google.android.gms.samples.vision.scanner4you;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class activity_press extends Activity implements View.OnClickListener {

    Button SAVE;
    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    ContentValues contentvalues = new ContentValues();
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press);

        SAVE=findViewById(R.id.button2);
        SAVE.setOnClickListener(this);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
    }


    @Override
    public void onClick(View v) {
        if (editText != null &&
                editText2 != null &&
                editText3 != null &&
                editText4 != null &&
                editText5 != null &&
                editText6 != null)
        {
        contentvalues.put(DBHelper.KEY_Serial, String.valueOf(editText.getText()));
        contentvalues.put(DBHelper.KEY_Type, String.valueOf(editText2.getText()));
        contentvalues.put(DBHelper.KEY_Model, String.valueOf(editText3.getText()));
        contentvalues.put(DBHelper.KEY_Inventary, String.valueOf(editText4.getText()));
        contentvalues.put(DBHelper.KEY_Department, String.valueOf(editText5.getText()));
        contentvalues.put(DBHelper.KEY_ID, String.valueOf(editText6.getText()));

    }   else new Message("Проверьте правильность введенных данных!");

        if (contentvalues.get(DBHelper.KEY_Serial) != null &&
                contentvalues.get(DBHelper.KEY_Type) != null &&
                contentvalues.get(DBHelper.KEY_Model) != null &&
                contentvalues.get(DBHelper.KEY_Inventary) != null &&
                contentvalues.get(DBHelper.KEY_Department) != null &&
                contentvalues.get(DBHelper.KEY_ID) != null) {
            insertOrUpdate(contentvalues);
            new Message("Запись добавлена!");
            editText.setText(null);
            editText2.setText(null);
            editText3.setText(null);
            editText4.setText(null);
            editText5.setText(null);
            editText6.setText(null);
        }else {new Message("Проверьте правильность введенных данных!");}
    }
    public void insertOrUpdate(ContentValues cv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int id = getID(cv);
        if (id == -1)
            db.insert(DBHelper.TABLE_CONTACTS, null, cv);
        else
            db.update(DBHelper.TABLE_CONTACTS, cv, "id=?", new String[]{Integer.toString(id)});
    }

    private int getID(ContentValues cv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.TABLE_CONTACTS, new String[]{"id"}, "id =? AND Serial=? AND Inventary=?",
                new String[]{cv.getAsString(DBHelper.KEY_ID), cv.getAsString(DBHelper.KEY_Serial), cv.getAsString(DBHelper.KEY_Inventary)}, null, null, null, null);

        if (c.moveToFirst()) //if the row exist then return the id
            return c.getInt(c.getColumnIndex("id"));
        return -1;
    }

    public class Message {
        Message(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }
}


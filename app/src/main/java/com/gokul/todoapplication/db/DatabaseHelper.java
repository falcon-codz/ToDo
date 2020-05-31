package com.gokul.todoapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String  DATABASE_NAME = "notes.db";
    private static final String TABLE_NAME = "notes_table";
    public static final String COL_1 = "ID";
    private static final String COL_2 = "NOTE";
    private static final String COL_3 = "PRIORITY";
    private static final String COL_4 = "STATUS";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NOTE TEXT, PRIORITY INTEGER, STATUS INTEGER)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);

    }

    public boolean insertNote(String note, int priority){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,note);
        contentValues.put(COL_3,priority);
        contentValues.put(COL_4,1);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
    }

    public Cursor searchNotes(String key){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE NOTE LIKE '"+key+"%'",null);

    }

    public boolean updateStatus(int id,String note,int priority,int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,note);
        contentValues.put(COL_3,priority);
        contentValues.put(COL_4,status);
        long result = db.update(TABLE_NAME, contentValues,"ID = "+id, null);
        if(result==-1)
            return false;
        else
            return true;
    }

    public boolean deteleNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "ID = "+id, null);

        if(result==-1)
            return false;
        else
            return true;
    }


}

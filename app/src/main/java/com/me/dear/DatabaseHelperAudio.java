package com.me.dear;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelperAudio extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelperAudio";

    private static final String TABLE_NAME = "audio";

    private static final String COL0 = "ID";
    private static final String COL1 = "UID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "PATH";
    private static final String COL4 = "DURATION";

    public DatabaseHelperAudio(@Nullable Context context) {
        super(context, TABLE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String uid, String name, String path, int duration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, uid);
        contentValues.put(COL2, name);
        contentValues.put(COL3, path);
        contentValues.put(COL4, duration);

        long res = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        if(res == -1) {
            db.close();
            return false;
        }else{
            db.close();
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}

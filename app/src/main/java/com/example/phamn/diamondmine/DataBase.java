package com.example.phamn.diamondmine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "save_game";
    private static final String TABLE_NAME = "max_score";
    private static final String ID = "id";
    private static final String SCORE = "score";
    private static final String TIME = "time";
    private static int version = 1;
    private String SQLQuery = " create table " + TABLE_NAME + " (" +
            SCORE + " integer, " +
            TIME + " NUMERIC)";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void saveGame(int s, String t) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCORE, s);
        values.put(TIME, t);
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public String getMax() {
        String result = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "select MAX(" + SCORE + ") from " + TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                result = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public void removeData(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}

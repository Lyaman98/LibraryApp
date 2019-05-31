package com.demo.dictionary.helperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.dictionary.Words;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordsDB.db";
    public static final int DATABASE_VERSION = 5;
    public static final String TABLE_NAME = "words";
    public static final String COLUMN_1 = "date";
    public static final String COLUMN_2 = "english";
    public static final String COLUMN_3 = "german";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_2 + " varchar(256)" + ","
            + COLUMN_3 + " varchar(256)" + "," + COLUMN_1 + " date" + " );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void updateWord(String oldValue, String english, String german) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("update " + TABLE_NAME + " SET " + COLUMN_2 + "='" + english + "',"
                + COLUMN_3 + "='" + german + "'" + " WHERE " + COLUMN_3 + "='" + oldValue + "';");

    }

    public long insertWord(String english, String german) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues words = new ContentValues();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = mdformat.format(calendar.getTime());


        words.put("date", strDate);
        words.put("english", english);
        words.put("german", german);

        return database.insert(TABLE_NAME, null, words);
    }

    public ArrayList<Words> getList() throws ParseException {

        ArrayList<Words> list = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String select = "select * from " + TABLE_NAME;
        Cursor cursor = database.rawQuery(select, null);

        if (cursor == null) return null;

        while (cursor.moveToNext()) {

            Words words = new Words(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(cursor.getString(cursor.getColumnIndex(COLUMN_1))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_2)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_3)));
            words.setAdded(true);
            list.add(words);
        }

        cursor.close();
        return list;
    }

    public void deleteWord(Words word) {
        SQLiteDatabase database = getWritableDatabase();
        String select = "delete from " + TABLE_NAME + " where english=" + "'" + word.getEnglish() + "';";

        database.execSQL(select);
    }

    public ArrayList<Words> getListByDate() throws ParseException {


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = mdformat.format(calendar.getTime());

        ArrayList<Words> list = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String select = "select * from " + TABLE_NAME + " where " + COLUMN_1 + "='" + strDate + "'";
        Cursor cursor = database.rawQuery(select, null);

        if (cursor == null) return null;

        while (cursor.moveToNext()) {

            Words words = new Words(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(cursor.getString(cursor.getColumnIndex(COLUMN_1))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_2)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_3)));
            words.setAdded(true);
            list.add(words);
        }

        cursor.close();
        return list;
    }

    public ArrayList<Words> getListByEnglishWord(String word) throws ParseException {

        ArrayList<Words> list = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String select = "select * from " + TABLE_NAME + " where " + COLUMN_2 + " like " + "'" + word + "%" + "'";
        Cursor cursor = database.rawQuery(select, null);

        if (cursor == null) return null;

        while (cursor.moveToNext()) {

            Words words = new Words(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(cursor.getString(cursor.getColumnIndex(COLUMN_1))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_2)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_3)));
            words.setAdded(true);
            list.add(words);
        }

        cursor.close();
        return list;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}

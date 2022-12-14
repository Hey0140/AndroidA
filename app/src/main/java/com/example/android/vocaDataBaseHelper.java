package com.example.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class vocaDataBaseHelper extends SQLiteOpenHelper {
    public static final int vocaDataBase_Version = 1;
    public static final String dataBaseName = "vocaDB";
    public static final String vocaName = "vocaName";
    public static final String wordLang = "wordLang";
    public static final String meanLang = "meanLang";
    public static final String date = "vocaDate";
    public static final String count = "count";
    private static final String TAG = "WordBook";


    public vocaDataBaseHelper(Context context) {
        super(context, dataBaseName, null, vocaDataBase_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String vocaSQL = "create table vocaDB (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                vocaName + "," + wordLang + "," + meanLang + "," + date + "," + count + ")";
        sqLiteDatabase.execSQL(vocaSQL);
        Log.w(TAG, "SQLite| create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        if (new_version == vocaDataBase_Version) {
            sqLiteDatabase.execSQL("drop table if exists vocaDB");
            onCreate(sqLiteDatabase);
        }
        Log.w(TAG, "SQLite| upgrate");

    }


    public void addVoca(String vocaName, String wordLang, String meanLang, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String da = formatter.format(date);
        ContentValues values = new ContentValues();
        values.put(vocaDataBaseHelper.vocaName, vocaName);
        values.put(vocaDataBaseHelper.wordLang, wordLang);
        values.put(vocaDataBaseHelper.meanLang, meanLang);
        values.put(vocaDataBaseHelper.date, da);
        values.put(vocaDataBaseHelper.count, 0);
        db.insert("vocaDB", null, values);
        Log.w(TAG, "SQLite| add voca");
        db.close();
    }

    public void updateVoca(int id, String vocaName, String wordLang,
                           String meanLang, Date date) {
        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String da = formatter.format(date);

        db.execSQL("UPDATE vocaDB " +
                "SET vocaName = '" + vocaName +
                "', wordLang = '" + wordLang +
                "', meanLang = '" + meanLang +
                "', vocaDate = '" + da + "' Where _id = " + id);
        Log.w(TAG, "SQLite| update voca");
        db.close();
    }

    public void updatePlusCount(int id) {
        //단어장 내에 단어의 갯수가 변경될 때 로딩하는 함수
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        Cursor c = db.rawQuery("select * from vocaDB where _id = " + id, null);
        if (c.moveToNext()) {
            count = c.getInt(5);
            count++;
        }
        db.execSQL("UPDATE vocaDB " +
                "SET count = " + count + " Where _id = " + id);
        Log.w(TAG, "SQLite| plus count");
        db.close();
    }

    public void updateMinusCount(int id) {
        //단어장 내에 단어의 갯수가 변경될 때 로딩하는 함수
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        Cursor c = db.rawQuery("select * from vocaDB where _id = " + id, null);
        if (c.moveToNext()) {
            count = c.getInt(5);
            count--;
        }
        db.execSQL("UPDATE vocaDB " +
                "SET count = " + count + " Where _id = " + id);
        Log.w(TAG, "SQLite| minus count");
        db.close();
    }


    public void deleteVoca(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from vocaDB Where _id = " + id + ";");
        db.close();
        Log.w(TAG, "SQLite| delete");
    }


    public LinkedList<LocalWordBook> showVoca() {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from vocaDB order by _id asc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            Integer count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        Log.w(TAG, "SQLite| read");
        db.close();

        return vocalist;
    }

    public LinkedList<LocalWordBook> showFilterDateIncVoca() {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from vocaDB order by vocaDate asc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            Integer count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        Log.i("read", "success");
        db.close();

        return vocalist;
    }

    public LinkedList<LocalWordBook> showFilterDateDescVoca() {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from vocaDB order by vocaDate desc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            Integer count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        Log.i("read", "success");
        db.close();

        return vocalist;
    }

    public LinkedList<LocalWordBook> showFilterNameIncVoca() {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from vocaDB order by vocaName asc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            Integer count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        Log.i("read", "success");
        db.close();
        return vocalist;
    }

    public LinkedList<LocalWordBook> showFilterNamedescVoca() {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from vocaDB order by vocaName desc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            Integer count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        Log.i("read", "success");
        db.close();
        return vocalist;
    }

    public int showId(String vocaName, String wordLang, String meanLang) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * From vocaDB" +
                " Where vocaName = '" + vocaName +
                "'and wordLang = '" + wordLang +
                "'and meanLang = '" + meanLang + "'", null);
        if (c.moveToNext()) {
            int id = c.getInt(0);
            Log.w(TAG, "SQLite| show id");
            db.close();
            return id;
        } else {
            db.close();
            return -1;
        }
    }


    public String showName(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * From vocaDB" +
                " Where _id = " + id, null);
        if (c.moveToNext()) {
            String name = c.getString(1);
            Log.i("showName", name);
            db.close();
            Log.w(TAG, "SQLite| show name");

            return name;
        } else {
            db.close();
            return "";
        }
    }

    public LinkedList<LocalWordBook> showFilterName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        LinkedList<LocalWordBook> vocalist = new LinkedList<LocalWordBook>();
        Cursor c = db.rawQuery("SELECT * From vocaDB" +
                " Where vocaName = '" + name + "'", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            int count = c.getInt(5);
//            table이 있는 상태에서 실행 시 에러 발생
            vocalist.addLast(new LocalWordBook(vocabularyName, word, wordMean, id, date, count));
        }
        db.close();
        Log.w(TAG, "SQLite| show filter name");

        return vocalist;
    }

    public LocalWordBook showWordBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * From vocaDB" +
                " Where _id = " + id, null);
        if (c.moveToNext()) {
            String vocabularyName = c.getString(1);
            String word = c.getString(2);
            String wordMean = c.getString(3);
            String date = c.getString(4);
            int count = c.getInt(5);
            db.close();
            Log.w(TAG, "SQLite| show word book");

            return new LocalWordBook(vocabularyName, word, wordMean, id, date, count);
        } else {
            db.close();
            return null;
        }
    }

}

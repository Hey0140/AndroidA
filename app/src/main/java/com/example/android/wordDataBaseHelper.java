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
import java.util.List;

public class wordDataBaseHelper extends SQLiteOpenHelper {
    public static final int wordDataBase_Version = 2;
    public static final String wordDataBase = "wordDataBase";
    public static final String word = "word";
    public static final String mean = "mean";
    public static final String date = "date";
    public static final String quizInclude = "quizInclude";
    public static final String vocaId = "vocaId";


    vocaDataBaseHelper vocaDB;
    SQLiteDatabase sqlDB;

    public wordDataBaseHelper(Context context) {
        super(context, wordDataBase, null, wordDataBase_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String wordSQL = "create table wordDB (" + "_id INTEGER primary key autoincrement, " +
                word + ", " + mean + ", " + date + ", " + quizInclude + " INTEGER, " + vocaId + " INTEGER, " + " FOREIGN KEY (" + vocaId + ") REFERENCES vocaDB (_id) ON DELETE CASCADE)";
        sqLiteDatabase.execSQL(wordSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        if (new_version == wordDataBase_Version) {
            sqLiteDatabase.execSQL("drop table if exists wordDB");
            onCreate(sqLiteDatabase);
        }
    }

    public void addWordVoca(String _word, String _mean, int voca_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(word, _word);
        values.put(mean, _mean);
        values.put(vocaId, voca_id);
        db.insert("wordDB", null, values);
        db.close();
        Log.i("add", "단어success");
    }

    public void updateWordVoca(int id, String _word, String _mean, Date _date) {
        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String da = formatter.format(_date);
        db.execSQL("UPDATE wordDB " +
                "SET word = '" + _word +
                "', mean = '" + _mean +
                "', date '" + da +
                "' Where _id = " + id);
        Log.i("update", "단어success");
        db.close();
    }

    public void updateQuizInclude(int id, int check) {
        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();

        if (check == 0) {
            db.execSQL("UPDATE wordDB " +
                    "SET quizInclude = " + 1 +
                    " Where _id = " + id);
        }
        if (check == 1) {
            db.execSQL("UPDATE wordDB " +
                    "SET quizInclude = " + 0 +
                    " Where _id = " + id);
        }

        Log.i("update", "단어success");
        db.close();
    }


    public void deleteWordVoca(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from wordDB Where _id = " + id + ";");
        db.close();
        Log.i("SQLite", "단어삭제");
    }


    public LinkedList<LocalWordBook> showAllWord(int voca_id) {
        LinkedList<LocalWordBook> vocalist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String word = c.getString(1);
            String mean = c.getString(2);
//            vocalist.addLast(new LocalWordBook(word, mean, id));
        }
        Log.i("read", "success");
        db.close();
        return vocalist;
    }


    public List<String> showWord(int voca_id) {
        List<String> wordlist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            String word = c.getString(1);
            wordlist.add(word);
        }
        Log.i("read", "success");
        db.close();
        return wordlist;
    }

    public List<String> showMean(int voca_id) {
        List<String> meanlist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            String mean = c.getString(2);
            meanlist.add(mean);
        }
        Log.i("read", "success");
        db.close();
        return meanlist;

    }

    public List<Integer> showQuizInclude(int voca_id) {
        List<Integer> quizIncludeList = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            Integer include = c.getInt(4);
            quizIncludeList.add(include);
        }
        Log.i("read", "success");
        db.close();
        return quizIncludeList;

    }


    public int showIdOfWord(String _word, String _mean, int voca_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * From vocaDB" +
                " Where word = '" + _word +
                "'and mean = '" + _mean +
                "'and vocaId = " + voca_id, null);
        if (c.moveToNext()) {
            int id = c.getInt(0);
            Log.i("showId", Integer.toString(id));
            db.close();
            return id;
        } else {
            db.close();
            return -1;
        }
    }
}

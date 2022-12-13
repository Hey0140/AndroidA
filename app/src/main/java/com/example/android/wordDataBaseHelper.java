package com.example.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
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
    public static final String TAG = "SQLite";


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
        Log.i(TAG, "add WordVoca success");
    }

    public void updateWordVoca(int id, String _word, String _mean, int voca_id) {
        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE wordDB " +
                "SET word = '" + _word +
                "', mean = '" + _mean +
                "' Where _id = " + id + " and vocaId = " + voca_id);
        Log.i(TAG, "word update success");
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


    public void deleteWordVoca(int id, int voca_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from wordDB Where _id = " + id + " and vocaId = " + voca_id + ";");
        db.close();
        Log.i(TAG, "delete WordVoca success");
    }

    public String showWordVoca(int id, int voca_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String wordName;
        Cursor c = db.rawQuery("select * from wordDB " +
                "Where _id = " + id +
                " and vocaId = " + voca_id +
                " order by _id asc", null);
        if (c.moveToNext()) {
            wordName = c.getString(1);
        } else {
            wordName = "None";
        }
        db.close();

        Log.i(TAG, "Show WordName success");
        return wordName;
    }


    public ArrayList<String> showWord(int voca_id) {
        ArrayList<String> wordlist = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            String word = c.getString(1);
            wordlist.add(word);
        }
        Log.i(TAG, "Read word List success");
        db.close();
        return wordlist;
    }

    public int showWordCount(int voca_id) {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        count = c.getCount();
        Log.i(TAG, "Read word List success");
        Log.i(TAG, Integer.toString(count));
        db.close();
        return count;
    }


    public List<String> showFilterWordOnWord(int voca_id, String word) {
        List<String> wordlist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from wordDB " +
                "where word = '" + word + "' " +
                "and vocaId = " + voca_id +
                " order by _id asc", null);
        while (c.moveToNext()) {
            String _word = c.getString(1);
            wordlist.add(_word);
        }
        Log.i(TAG, "Searching the word name. And read Word List success");
        db.close();
        return wordlist;
    }

    public List<String> showFilterWordOnMean(int voca_id, String word) {
        List<String> meanlist = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from wordDB " +
                "where word = '" + word + "' " +
                "and vocaId = " + voca_id +
                " order by _id asc", null);
        while (c.moveToNext()) {
            String _mean = c.getString(2);
            meanlist.add(_mean);
        }
        Log.i(TAG, "Searching the word name. And read Mean List success");
        db.close();
        return meanlist;
    }


    public ArrayList<String> showMean(int voca_id) {
        ArrayList<String> meanlist = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select *" +
                "from wordDB where vocaId = " + voca_id + " order by _id asc", null);
        while (c.moveToNext()) {
            String mean = c.getString(2);
            meanlist.add(mean);
        }
        Log.i(TAG, "Read mean List success");
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
        Cursor c = db.rawQuery("SELECT * From wordDB" +
                " Where word = '" + _word +
                "'and mean = '" + _mean +
                "'and vocaId = " + voca_id, null);
        if (c.moveToNext()) {
            int id = c.getInt(0);
            Log.i(TAG, "Find the Id of the word");
            db.close();
            return id;
        } else {
            db.close();
            return -1;
        }
    }
//
//    public List<String> showFilterWord(String _word) {
//        List<String> wordlist = new LinkedList<>();
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor c = db.rawQuery("select * " +
//                "from wordDB where word = '" + _word + "'", null);
//        while (c.moveToNext()) {
//            String word = c.getString(1);
//            wordlist.add(word);
//        }
//        Log.i(TAG, "Filter Word Name success");
//        db.close();
//        return wordlist;
//    }


}

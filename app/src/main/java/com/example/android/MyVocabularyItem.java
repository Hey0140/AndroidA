package com.example.android;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyVocabularyItem {

    public String vocabularyName;
    public String vocabularyWord;
    public String vocabularyMean;
    public String vocabularyBirthDay;
    public int vocabularyCount;
    public String languageRelation;
    private int id;
    View v1;



    public MyVocabularyItem(String name, String word, String wordMean, int id){
        // 단어장 이름 초기화
        vocabularyName = name;
        this.id = id;
        // 단어장 생성 날짜 초기화
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        vocabularyBirthDay = formatter.format(date);
        languageRelation = word + "/" + wordMean;
        // 단어장 수 초기화
        vocabularyCount = 0;
    }

    public MyVocabularyItem(String word, String mean, int id){
        // 단어장 이름 초기화
        vocabularyWord = word;
        vocabularyMean = mean;
        this.id = id;
        // 단어장 생성 날짜 초기화
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        vocabularyBirthDay = formatter.format(date);
    }

    public int getId(){
        return id;
    }

}

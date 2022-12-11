package com.example.android;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.List;

public class WordBook {
    private String name;
    @ServerTimestamp
    private Timestamp createDate;
    private int likeCount;
    private int wordCount;
    private String uID;
    private String meanLang;
    private String wordLang;
    private List<String> likeId;


    public WordBook() {
    }

    public WordBook(String name, int likeCount, int wordCount, String uID, String meanLang, String wordLang, List<String> likeId) {
        this.name = name;
        this.likeCount = 0;
        this.wordCount = 0;
        this.uID = uID;
        this.meanLang = meanLang;
        this.wordLang = wordLang;
        this.likeId = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getuID() {
        return uID;
    }

    public String getMeanLang() {
        return meanLang;
    }

    public String getWordLang() {
        return wordLang;
    }

    public List<String> getLikeId() {
        return likeId;
    }
}


package com.example.android;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordBook implements Serializable {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public void setMeanLang(String meanLang) {
        this.meanLang = meanLang;
    }

    public void setWordLang(String wordLang) {
        this.wordLang = wordLang;
    }

    public void setLikeId(List<String> likeId) {
        this.likeId = likeId;
    }
}


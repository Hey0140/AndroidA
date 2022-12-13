package com.example.android;

public class Word {
    private String word;
    private String mean;
    private String uID;

    public Word(String word, String mean, String uID) {
        this.word = word;
        this.mean = mean;
        this.uID = uID;
    }

    public Word() {
    }

    public String getWord() {
        return word;
    }

    public String getMean() {
        return mean;
    }

    public String getuID() {
        return uID;
    }

}

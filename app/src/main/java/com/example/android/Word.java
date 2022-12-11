package com.example.android;

public class Word {
    private final String word;
    private final String mean;
    private final String uID;

    public Word(String word, String mean, String uID) {
        this.word = word;
        this.mean = mean;
        this.uID = uID;
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

package com.example.android;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordBooKParcel extends WordBook implements Parcelable {
    public WordBooKParcel(WordBook wordBook) {
        super.setName(wordBook.getName());
        super.setCreateDate(wordBook.getCreateDate());
        super.setLikeCount(wordBook.getLikeCount());
        super.setWordCount(wordBook.getWordCount());
        super.setuID(wordBook.getuID());
        super.setMeanLang(wordBook.getMeanLang());
        super.setWordLang(wordBook.getWordLang());
        List<String> temp = new ArrayList<>();
        Collections.copy(temp, wordBook.getLikeId());
        super.setLikeId(temp);
    }

    protected WordBooKParcel(Parcel in) {
        super.setName(in.readString());
        super.setCreateDate(in.readParcelable(Timestamp.class.getClassLoader()));
        super.setLikeCount(in.readInt());
        super.setWordCount(in.readInt());
        super.setuID(in.readString());
        super.setMeanLang(in.readString());
        super.setWordLang(in.readString());
        List<String> temp = new ArrayList<>();
        in.readList(temp, List.class.getClassLoader());
        super.setLikeId(temp);
    }

    public static final Creator<WordBooKParcel> CREATOR = new Creator<WordBooKParcel>() {
        @Override
        public WordBooKParcel createFromParcel(Parcel in) {
            return new WordBooKParcel(in);
        }

        @Override
        public WordBooKParcel[] newArray(int size) {
            return new WordBooKParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(super.getName());
         dest.writeParcelable(super.getCreateDate(), 0);
         dest.writeInt(super.getLikeCount());
         dest.writeInt(super.getWordCount());
         dest.writeString(super.getuID());
         dest.writeString(super.getMeanLang());
         dest.writeString(super.getWordLang());
         dest.writeList(super.getLikeId());
    }
}

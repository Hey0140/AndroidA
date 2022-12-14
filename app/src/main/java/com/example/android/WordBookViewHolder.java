package com.example.android;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WordBookViewHolder extends RecyclerView.ViewHolder {
    View mItem;
    TextView mName;
    TextView mCreateDate;
    TextView mLang;
    TextView mWordCount;


    public WordBookViewHolder(@NonNull View itemView) {
        super(itemView);
        mItem = itemView.findViewById(R.id.sharedVocabularyListItem);
        mName = itemView.findViewById(R.id.sharedVocabularyListItemName);
        mCreateDate = itemView.findViewById(R.id.sharedVocabularyBirthDay);
        mLang = itemView.findViewById(R.id.languageRelation);
        mWordCount = itemView.findViewById(R.id.wordCount);
    }

    void bind(@NonNull WordBook wordBook){
        mName.setText(wordBook.getName());
        Date d = wordBook.getCreateDate().toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String date = formatter.format(d);
        mCreateDate.setText(date);
        String lang = wordBook.getWordLang()+"/"+wordBook.getMeanLang();
        mLang.setText(lang);
        mWordCount.setText(wordBook.getWordCount());
    }
}

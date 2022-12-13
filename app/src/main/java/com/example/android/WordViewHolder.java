package com.example.android;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WordViewHolder extends RecyclerView.ViewHolder{
    TextView mWord;
    TextView mMean;
    View mWordItem;

    public WordViewHolder(@NonNull View itemView) {
        super(itemView);
        mWord = itemView.findViewById(R.id.word);
        mMean = itemView.findViewById(R.id.mean);
        mWordItem = itemView.findViewById(R.id.myWordListItem);
    }

    void bind(@NonNull Word word){
        mWord.setText(word.getWord());
        mMean.setText(word.getMean());
    }
}

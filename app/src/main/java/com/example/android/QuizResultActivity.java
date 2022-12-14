package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {


    LinearLayout myWordListItemContainer;
    ImageView backbutton;


    int idEdit = 1000000000;
    private static final String TAG = "WordBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        ArrayList<Integer> rightIndexList = new ArrayList<>();
        ArrayList<String> wordList = new ArrayList<>();
        ArrayList<String> meanList = new ArrayList<>();

        Intent intent = getIntent();
        int size = intent.getIntExtra("wholeSize", 0);
        rightIndexList = intent.getIntegerArrayListExtra("clearList");
        wordList = intent.getStringArrayListExtra("wordList");
        meanList = intent.getStringArrayListExtra("meanList");
        backbutton = findViewById(R.id.backButtonForQuizResult);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        for (int i = 0; i < rightIndexList.size(); i++)
            Log.d(TAG,"MyWordQuizResult| rightIndexList i : "+ Integer.toString(rightIndexList.get(i)));
        myWordListItemContainer = findViewById(R.id.wordListItemContainerForQuizResult);
        Log.d(TAG,"MyWordQuizResult| size : " +Integer.toString(size));
        for (int i = 0; i < size; i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_word_listitem_for_quiz_result, myWordListItemContainer, true);
            TextView temp = findViewById(R.id.wordForQuizResult);
            TextView temp2 = findViewById(R.id.meanForQuizResult);
            ImageView temp3 = findViewById(R.id.imageViewForQuizResult);
            ImageView temp4 = findViewById(R.id.imageViewForQuizResult2);
            if (!rightIndexList.contains(i)) {
                temp3.setVisibility(View.GONE);
                temp4.setVisibility(View.VISIBLE);
            } else {
                temp4.setVisibility(View.GONE);
                temp3.setVisibility(View.VISIBLE);
            }
            temp.setId(idEdit++);
            temp2.setId(idEdit++);
            temp.setText(wordList.get(i));
            temp2.setText(meanList.get(i));
            temp3.setId(idEdit++);
            temp4.setId(idEdit++);
        }
    }
}
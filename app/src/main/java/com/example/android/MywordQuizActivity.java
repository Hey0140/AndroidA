package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MywordQuizActivity extends AppCompatActivity implements View.OnClickListener {

    //데이터
    wordDataBaseHelper wordDB = new wordDataBaseHelper(this);

    Button submit_answer;
    ImageView back_button_image;
    Button next_quiz;
    TextView quiz_text;
    TextView quiz_co_answer;
    TextView input_answer;
    View quiz_co_answer_box;
    View quiz_text_box;
    ImageView quiz_answer_correct;
    ImageView quiz_answer_incorrect;
    wordDataBaseHelper quiz_data;
    wordDataBaseHelper answer_data;

    String[] quiz;
    String[] answer;
    int[] check = {1,1,0,1,1};
//    List<String> quiz;
//    List<String> answer;
//    List<Integer> check;
    //1이 나오는 상태 0이 나오지 않는 상태





    int[] randomIndex;
    Random random = new Random();
    int bound;
    Handler mHandler = new Handler();

    public int quiz_answer (int i){
        while(check[randomIndex[i]] == 0){
            i++;
            if(i >= randomIndex.length){
                finish();
            }
        }
        if(i >= randomIndex.length){
            finish();
        }

        return i;
    }

    int indexOfQuiz = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        next_quiz = findViewById(R.id.next_quiz);
        quiz_co_answer = findViewById(R.id.quiz_co_answer);
        quiz_co_answer_box = findViewById(R.id.quiz_co_answer_box);
        quiz_text_box = findViewById(R.id.quiz_text_box);
        input_answer =  findViewById(R.id.input_answer);
        quiz_text =  findViewById(R.id.quiz_text);
        submit_answer = findViewById(R.id.submit_answer);
        back_button_image = findViewById(R.id.back_button_image);
        quiz_answer_correct = findViewById(R.id.quiz_answer_correct);
        quiz_answer_incorrect = findViewById(R.id.quiz_answer_incorrect);
        quiz_data = new wordDataBaseHelper(this);
        answer_data = new wordDataBaseHelper(this);
        //리스너
        submit_answer.setOnClickListener(this);
        next_quiz.setOnClickListener(this);
        back_button_image.setOnClickListener(this);
        quiz = getResources().getStringArray(R.array.quiz);
        answer = getResources().getStringArray(R.array.answer);
        bound = quiz.length;
        randomIndex = new int[bound];

        Thread randThread = new Thread("random Thread"){
            public void run(){
                int[] a = new int[bound];
                for(int i = 0; i < bound; i++){
                    a[i] = random.nextInt(bound);
                    for(int j = 0; j < i; j ++){
                        if(a[i] == a[j]){
                            i--;
                        }
                    }
                }

                for(int i = 0; i <bound; i++){
                    randomIndex[i] = a[i];
                }

                Runnable callback = new Runnable() {
                    @Override
                    public void run() {
                        if(randomIndex.length >=1){
                            indexOfQuiz = quiz_answer(0);
                            quiz_text.setText(quiz[randomIndex[indexOfQuiz]]);
                            quiz_co_answer.setText(answer[randomIndex[indexOfQuiz]]);
                        }
                    }
                };
                Message m = Message.obtain(mHandler, callback);
                mHandler.sendMessage(m);
            }
        };
        randThread.start();


        int vocaId = getIntent().getIntExtra("vocaId", 0);
    }





    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_answer) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(1500);
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            Animation _animation = new AlphaAnimation(1, 0);
            _animation.setDuration(1500);

            quiz_co_answer.setText(answer[randomIndex[indexOfQuiz]]);
            quiz_text.setVisibility(View.INVISIBLE);
            quiz_co_answer.setVisibility(View.VISIBLE);
            quiz_text_box.setVisibility(View.INVISIBLE);
            submit_answer.setVisibility(View.INVISIBLE);
            quiz_text.setVisibility(View.INVISIBLE);
            quiz_co_answer.setVisibility(View.VISIBLE);
            next_quiz.setVisibility(View.VISIBLE);
            quiz_co_answer_box.setVisibility(View.VISIBLE);

            if (input_answer.getText().toString().equals(answer[randomIndex[indexOfQuiz]])) {
                quiz_answer_correct.setVisibility(View.VISIBLE);
                quiz_answer_correct.setAnimation(animation);
                quiz_answer_correct.setVisibility(View.INVISIBLE);
                quiz_answer_correct.setAnimation(_animation);
            } else {
                quiz_answer_incorrect.setVisibility(View.VISIBLE);
                quiz_answer_incorrect.setAnimation(animation);
                quiz_answer_incorrect.setVisibility(View.INVISIBLE);
                quiz_answer_incorrect.setAnimation(_animation);
            }
            indexOfQuiz++;
        }

        if(view.getId() == R.id.next_quiz){
            input_answer.setText("");
            indexOfQuiz = quiz_answer(indexOfQuiz);
            quiz_text.setText(quiz[randomIndex[indexOfQuiz]]);
            next_quiz.setVisibility(View.INVISIBLE);
            quiz_co_answer_box.setVisibility(View.INVISIBLE);
            quiz_text.setVisibility(View.VISIBLE);
            quiz_co_answer.setVisibility(View.INVISIBLE);
            quiz_text_box.setVisibility(View.VISIBLE);
            submit_answer.setVisibility(View.VISIBLE);

        }
        if(view.getId() == R.id.back_button_image){
            finish();
        }

    }

}

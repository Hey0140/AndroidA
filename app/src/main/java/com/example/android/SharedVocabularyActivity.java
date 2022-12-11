package com.example.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SharedVocabularyActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";


    // 화면 스와이프를 위한 좌표
    float x1,x2,y1,y2;

    // 객체 연결
    EditText searchWindow;
    Button searchOptionButton;
    Button searchButton;
    ImageButton addButton;
    ImageView networkChecking;

    // 단어장 뷰 생성을 위한 콘테이너
    LinearLayout myVocaContainer;

    //단어생성 뷰 연결
    Button acceptButton;
    EditText acceptfirst;
    Button acceptsecond;
    Button acceptthird;
    ConstraintLayout addViewWindow;
    ArrayList<LocalWordBook> sharedVocaArrayList;

    boolean isNetWork =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_vocabulary_activity);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        // 객체 연결
        networkChecking = findViewById(R.id.networkChecking);
        searchWindow = findViewById(R.id.searchWindow2);
        searchOptionButton = findViewById(R.id.searchOptionButton2);
        searchButton = findViewById(R.id.searchButton2);
        addButton = findViewById(R.id.addButton2);
        addViewWindow = findViewById(R.id.addViewWindow2);
        acceptButton = findViewById(R.id.acceptButton2);
        addViewWindow.setVisibility(View.GONE);
        acceptfirst = findViewById(R.id.vocabularyNameForAdd2);
        acceptsecond = findViewById(R.id.wordForAdd2);
        acceptthird = findViewById(R.id.wordMeanForAdd2);
        myVocaContainer = findViewById(R.id.vocabularyListItemContainer2);

        // 객체 이벤트 리스너 등
        addButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);

        sharedVocaArrayList = new ArrayList<LocalWordBook>();

        isNetWork = isConnected(SharedVocabularyActivity.this);
        if(isNetWork){
            networkChecking.setVisibility(View.INVISIBLE);
        }
        else{
            networkChecking.setVisibility(View.VISIBLE);
        }

//        FirebaseDB.setWordBook(db, new WordBook("JPT", ));

    }




    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }


    // 버튼 클릭 이벤트 구현
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton2: // 검색 버튼 (미완료)
                String searchWindowString = getSearchWindowString();
                //
                break;
            case R.id.addButton2: // 단어장 추가 버튼 클릭시
                addViewWindow.setVisibility(View.VISIBLE);
                break;
            case R.id.acceptButton2: // 단어장 [생성하기] 버튼 클릭시
                LayoutInflater inflater2 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater2.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
                break;
        }

    }

    // 화면 스와이프를 통한 내 단어장 액티비티로 전환
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x2 + 500> x1){
                    addViewWindow.setVisibility(View.GONE);
                    finish();
                }
        }
        return false;
    }


    // 검색창의 값을 리턴. 빈 값이면 null을 리턴.
    public String getSearchWindowString(){
        String str = searchWindow.getText().toString();

        if(str.length() == 0)
        {
            return null;
        }else{
            return str;
        }
    }
    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success "+mAuth.getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }



}
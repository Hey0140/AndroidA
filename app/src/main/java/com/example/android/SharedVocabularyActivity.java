package com.example.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SharedVocabularyActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";


    // 화면 스와이프를 위한 좌표
    float x1, x2, y1, y2;


    // 객체 연결
    EditText searchWindow;
    Button searchOptionButton;
    Button searchButton;
    ImageButton addButton;
    ImageView networkChecking;
    View backgroundViewOfFull;


    // 단어장 뷰 생성을 위한 콘테이너
    LinearLayout myVocaContainer;

    //단어생성 뷰 연결
    Button acceptButton;
    EditText acceptfirst;
    Button acceptsecond;
    Button acceptthird;
    ConstraintLayout addViewWindow;
    ArrayList<LocalWordBook> sharedVocaArrayList;

    boolean isNetWork;


    FrameLayout sharedbackgroundView;
    ScrollView myVocaListScrollView;

    // 단어생성 뷰 연결
    Button buttonForAddWordBook;
    EditText nameForAddWordBook;
    Button wordForAddWordBook;
    Button wordMeanForAddWordBook;
    ScrollView languagePickerScrollView;
    Button acceptButtonForAdd;
    EditText vocaNameForAdd;
    Button wordForAdd;
    Button wordMeanForAdd;
    ConstraintLayout languagePickerWindow;

    // 단어수정 뷰 연결
    ConstraintLayout rewriteViewWindow;
    ConstraintLayout deleteViewWindow;
    EditText vocaNameForRewrite;
    Button acceptButtonForRewrite;
    Button acceptButtonForDelete;
    Button wordForRewrite;
    Button wordMeanForRewrite;
    Button acceptButtonForDeleteConfirm;
    TextView deleteConfirmText;

    // 단어 피커 뷰 버튼 연결
    TextView korB;
    TextView japB;
    TextView grkB;
    TextView gerB;
    TextView porB;
    TextView spnB;
    TextView chiB;
    TextView frhB;
    TextView rusB;
    TextView engB;


    //디비 관련 변수
    public vocaDataBaseHelper vocabularyDB;
    String vocaId;


    // 단어 피커 뷰를 위한 변수
    boolean isSecond = true;
    boolean isForRewrite = false;
    boolean isReset = false;
    int idForRewrite;
    int i;
    String vocaBookId;
    WordBook vocaWordBook;


    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();

            vocaWordBook = (WordBook) bundle.getSerializable("wordBooks");
            vocaBookId = bundle.getString("wordBookId");
            i = bundle.getInt("iData");

            int idEdit = (i - 1) * 5;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
            String _vocaid = vocaBookId;
            String vocabularyName = vocaWordBook.getName();
            String word = vocaWordBook.getWordLang();
            String wordMean = vocaWordBook.getMeanLang();
            Timestamp date = vocaWordBook.getCreateDate();
            View v1;

            v1 = findViewById(R.id.view);
            v1.setId(idEdit);
            v1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //롱클릭 시 발동함
                    Log.d("롱클릭", Integer.toString(v.getId()));
                    rewriteViewWindow.setVisibility(View.VISIBLE);
                    wordForRewrite.setText(word);
                    wordMeanForRewrite.setText(wordMean);
                    vocaNameForRewrite.setText(vocabularyName);
                    idForRewrite = v.getId();
                    rewriteViewWindow.bringToFront();
                    backgroundViewOfFull.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundViewOfFull.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                    vocaId = _vocaid;
                    Log.i("롱클릭 시 id", vocaId);
                    isForRewrite = true;
                    return true;
                }
            });

            //수정
            v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SharedVocabularyActivity.this, SharedWordBookActivity.class);
                    vocaId = _vocaid;
                    intent.putExtra("단어장 data", vocaWordBook + "@" + v.getId());
                    intent.putExtra("vocaId", vocaId);
                    Log.d("intent 클릭", "vocaId 전송");
                    startActivity(intent);
                    //어떤 걸로 액티비티끼리 다시 정보를 받을 수 있는 거지
                }
            });

            TextView one = myVocaContainer.findViewById(R.id.myVocabularyListItemName);
            one.setId(idEdit + 1);
            one.setText(vocabularyName);
            TextView two = myVocaContainer.findViewById(R.id.languageRelation);
            two.setId(idEdit + 2);
            two.setText(word + "/" + wordMean);
            TextView three = myVocaContainer.findViewById(R.id.myVocabularyBirthDay);
            three.setId(idEdit + 3);
            Date d = date.toDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            String shared_date = formatter.format(d);
            three.setText(shared_date);
            TextView five = myVocaContainer.findViewById(R.id.wordCount);
            five.setId(idEdit + 4);
//            five.setText(Integer.toString(word_count));
        }
    };


    @SuppressLint({"LongLogTag", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_vocabulary_activity);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();


        // 객체 연결
        backgroundViewOfFull = findViewById(R.id.sharedBackgroundOfFull);
        sharedbackgroundView = findViewById(R.id.shearedBackgroundView);
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
        sharedbackgroundView.bringToFront();

        //맨 위로 backgroundview 빼내기
        /*
        sharedBackgroundView.bringToFront();
        sharedBackgroundView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        */

        Log.i("SharedVocabulary", "create success");

        // 객체 이벤트 리스너 등
        addButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        networkChecking.setOnClickListener(this);

        sharedVocaArrayList = new ArrayList<LocalWordBook>();

        isNetWork = isConnected(SharedVocabularyActivity.this);
        if (isNetWork == false) {
            sharedbackgroundView.setBackgroundColor(Color.parseColor("#85323232"));
            sharedbackgroundView.setVisibility(View.VISIBLE);
            sharedbackgroundView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            Log.i(TAG, "network가 꺼짐");
        }


        WordBook[] wordBooks = new WordBook[1];
        String[] wordBookId = new String[1];
        int[] i = new int[1];
        i[0] = 0;
        ArrayList<DocumentSnapshot>[] vocaBook = new ArrayList[1];

        Thread getVocaIdThread = new Thread("getVocaIdThread") {
            @Override
            public void run() {
                super.run();
                for (DocumentSnapshot ds : vocaBook[0]) {
                    wordBookId[0] = ds.getId();
                    wordBooks[0] = ds.toObject(WordBook.class);
                    Message m = Message.obtain(handler);
                    Bundle bundle = new Bundle();
                    bundle.putString("wordBookId", wordBookId[0]);
                    bundle.putSerializable("wordBooks", wordBooks[0]);
                    bundle.putInt("iData", i[0]);
                    m.setData(bundle);

                    handler.sendMessage(m);
                    i[0]--;
                }
            }

            //처음 시작될 때 화면에 데이터뿌리기


        };
        FirebaseDB.getWordBookList(db, 0, null, null, false, mAuth.getUid(), getVocaIdThread, vocaBook);
        Log.i("SharedVocabulary", "create success complete");
    }


    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


    // 버튼 클릭 이벤트 구현
    @SuppressLint({"LongLogTag", "ClickableViewAccessibility"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.networkChecking:
                isNetWork = isConnected(SharedVocabularyActivity.this);
                if (isNetWork == true) {
                    sharedbackgroundView.setVisibility(View.INVISIBLE);
                    sharedbackgroundView.setOnTouchListener(new View.OnTouchListener() {


                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    Log.i(TAG, "network가 켜짐");
                } else {
                    Toast toast = Toast.makeText(SharedVocabularyActivity.this,
                            "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG);
                    toast.show();
                }
            case R.id.searchButton2: // 검색 버튼 (미완료)
                String searchWindowString = getSearchWindowString();
                //
                break;
            case R.id.addButton2: // 단어장 추가 버튼 클릭시
                addViewWindow.setVisibility(View.VISIBLE);
                break;
            case R.id.acceptButton2: // 단어장 [생성하기] 버튼 클릭시
                LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater2.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
                break;
        }

    }

    // 화면 스와이프를 통한 내 단어장 액티비티로 전환
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x2 + 500 > x1) {
                    addViewWindow.setVisibility(View.GONE);
                    finish();
                }
        }
        return false;
    }


    // 검색창의 값을 리턴. 빈 값이면 null을 리턴.
    public String getSearchWindowString() {
        String str = searchWindow.getText().toString();

        if (str.length() == 0) {
            return null;
        } else {
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
                            Log.d(TAG, "signInAnonymously:success " + mAuth.getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }


}
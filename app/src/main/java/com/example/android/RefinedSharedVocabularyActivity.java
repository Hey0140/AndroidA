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
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.paging.PagingConfig;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RefinedSharedVocabularyActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";


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

    FirestorePagingAdapter<WordBook, WordBookViewHolder> adapter;

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

        Log.i("SharedVocabulary", "create success");

        View slide = findViewById(R.id.slide_view);
        slide.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                addViewWindow.setVisibility(View.GONE);
                finish();
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }
        });

        // 객체 이벤트 리스너 등
        addButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        networkChecking.setOnClickListener(this);

        sharedVocaArrayList = new ArrayList<LocalWordBook>();

        isNetWork = isConnected(RefinedSharedVocabularyActivity.this);
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
        Log.i("SharedVocabulary", "create success complete");
        int sortNum = 0;
        String meanLang = null;
        String wordLang = null;
        boolean like = false;
        Query query = FirebaseDB.getWordBookList(db, sortNum, meanLang, wordLang, like, mAuth.getUid());
        PagingConfig config = new PagingConfig(4, 2, false);
        FirestorePagingOptions<WordBook> options = new FirestorePagingOptions.Builder<WordBook>()
                .setLifecycleOwner(this)
                .setQuery(query, config, WordBook.class)
                .build();
        adapter = new FirestorePagingAdapter<WordBook, WordBookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WordBookViewHolder holder, int position, @NonNull WordBook model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public WordBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_wordbook_list_item, parent, false);
                return new WordBookViewHolder(view);
            }
        };
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
                isNetWork = isConnected(RefinedSharedVocabularyActivity.this);
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
                    Toast toast = Toast.makeText(RefinedSharedVocabularyActivity.this,
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
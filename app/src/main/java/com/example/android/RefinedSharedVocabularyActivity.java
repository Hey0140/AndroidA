package com.example.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class RefinedSharedVocabularyActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";


    // 객체 연결
    EditText searchWindow;
    FrameLayout sharedbackgroundView;
    ImageView networkChecking;
    Button searchOptionButton;
    Button searchButton;
    Button SearchOptionButton;
    ImageButton addButton;
    View backgroundViewOfFull;
    ConstraintLayout sharedRewriteViewWindow;
    EditText sharedVocabularyNameForRewrite;
    Button sharedWordForRewrite;
    Button sharedWordMeanForRewrite;
    Button sharedAcceptButtonForRewrite;
    Button sharedAcceptButtonForDelete;
    ScrollView languagePickerWindowScrollView;
    ConstraintLayout languagePickerWindow;
    ConstraintLayout deleteWindow;
    Button deleteButton;
    TextView confirmQuistion;
    View sharedBackgroundOfFull;


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


    ArrayList<LocalWordBook> sharedVocaArrayList;

    boolean isNetWork;

    FirestorePagingAdapter<WordBook, WordBookViewHolder> adapter;
    RecyclerView recyclerView;

    boolean isForRewrite = false;
    boolean isSecond = true;

    @SuppressLint({"LongLogTag", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refined_shared_vocabulary_activity);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        sharedBackgroundOfFull = findViewById(R.id.sharedBackgroundOfFull);
        recyclerView = findViewById(R.id.sharedVocabularyRecyclerView);
        // 객체 연결
        languagePickerWindowScrollView = findViewById(R.id.languagePickerWindowScrollView);
        deleteWindow = findViewById(R.id.deleteWindow);
        deleteButton = findViewById(R.id.deleteButton);
        confirmQuistion = findViewById(R.id.confirmQuestion);


        korB = findViewById(R.id.koreanPick);
        chiB = findViewById(R.id.chinesePick);
        japB = findViewById(R.id.japanesePick);
        frhB = findViewById(R.id.frenchPick);
        porB = findViewById(R.id.portugalPick);
        grkB = findViewById(R.id.greekPick);
        gerB = findViewById(R.id.germanPick);
        spnB = findViewById(R.id.spanshPick);
        rusB = findViewById(R.id.russianPick);
        engB = findViewById(R.id.englisgPick);
        korB.setOnClickListener(this);
        engB.setOnClickListener(this);
        chiB.setOnClickListener(this);
        japB.setOnClickListener(this);
        frhB.setOnClickListener(this);
        porB.setOnClickListener(this);
        grkB.setOnClickListener(this);
        gerB.setOnClickListener(this);
        spnB.setOnClickListener(this);
        rusB.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        languagePickerWindowScrollView = findViewById(R.id.languagePickerWindowScrollView);
        languagePickerWindow = findViewById(R.id.languagePickerWindow);
        sharedRewriteViewWindow = findViewById(R.id.sharedRewriteViewWindow);
        sharedVocabularyNameForRewrite = findViewById(R.id.sharedVocabularyNameForRewrite);
        sharedWordForRewrite = findViewById(R.id.sharedWordForRewrite);
        sharedWordMeanForRewrite = findViewById(R.id.sharedWordMeanForRewrite);
        sharedAcceptButtonForRewrite = findViewById(R.id.sharedAcceptButtonForRewrite);
        sharedAcceptButtonForDelete = findViewById(R.id.sharedAcceptButtonForDelete);


        languagePickerWindow.setVisibility(View.GONE);
        sharedRewriteViewWindow.setVisibility(View.GONE);
        deleteWindow.setVisibility(View.GONE);
        backgroundViewOfFull = findViewById(R.id.sharedBackgroundOfFull);
        sharedbackgroundView = findViewById(R.id.shearedBackgroundView);
        networkChecking = findViewById(R.id.networkChecking);
        searchWindow = findViewById(R.id.searchWindow2);
        searchOptionButton = findViewById(R.id.searchOptionButton2);
        searchButton = findViewById(R.id.searchButton2);
        sharedbackgroundView.bringToFront();
        sharedBackgroundOfFull.bringToFront();


        sharedRewriteViewWindow = findViewById(R.id.sharedRewriteViewWindow);

        sharedVocabularyNameForRewrite = findViewById(R.id.sharedVocabularyNameForRewrite);
        sharedAcceptButtonForRewrite.setOnClickListener(this);
        sharedAcceptButtonForDelete.setOnClickListener(this);
        sharedWordForRewrite.setOnClickListener(this);
        sharedWordMeanForRewrite.setOnClickListener(this);

        Log.i("SharedVocabulary", "create success");

        //스와이프 부분
        View slide = findViewById(R.id.slide_view);
        slide.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                finish();
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }
        });

        // 객체 이벤트 리스너 등
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
            Log.i(TAG, "network가 꺼진 것을 인식");
        }


        sharedBackgroundOfFull.bringToFront();
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
                holder.id = getItem(position).getId();
                holder.mItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RefinedSharedVocabularyActivity.this, SharedWordBookActivity.class);
                        intent.putExtra("WordBookId", holder.id);
                        startActivity(intent);
                    }
                });
                holder.mItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        sharedBackgroundOfFull.setBackgroundColor(Color.parseColor("#85323232"));
                        sharedBackgroundOfFull.setVisibility(View.VISIBLE);
                        sharedRewriteViewWindow.setVisibility(View.VISIBLE);
                        sharedRewriteViewWindow.bringToFront();
                        sharedWordForRewrite.bringToFront();
                        sharedWordMeanForRewrite.bringToFront();
                        sharedBackgroundOfFull.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        isForRewrite = true;

                        return true;
                    }
                });

            }

            @NonNull
            @Override
            public WordBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_wordbook_list_item, parent, false);
                return new WordBookViewHolder(view);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
            case R.id.sharedWordForRewrite:
            case R.id.sharedWordMeanForRewrite:
                languagePickerWindow.bringToFront();
                languagePickerWindow.setVisibility(View.VISIBLE);
                break;
            case R.id.searchButton2: // 검색 버튼 (미완료)
                String searchString = searchWindow.getText().toString();
                
                //
                break;
            case R.id.koreanPick:
                setLanguageBlank(isSecond, isForRewrite, "한국어");
                break;
            case R.id.chinesePick:
                setLanguageBlank(isSecond, isForRewrite, "중국어");
                break;
            case R.id.englisgPick:
                setLanguageBlank(isSecond, isForRewrite, "영어");
                break;
            case R.id.frenchPick:
                setLanguageBlank(isSecond, isForRewrite, "프랑스어");
                break;
            case R.id.germanPick:
                setLanguageBlank(isSecond, isForRewrite, "독일어");
                break;
            case R.id.portugalPick:
                setLanguageBlank(isSecond, isForRewrite, "포르투갈어");
                break;
            case R.id.spanshPick:
                setLanguageBlank(isSecond, isForRewrite, "스페인어");
                break;
            case R.id.greekPick:
                setLanguageBlank(isSecond, isForRewrite, "그리스어");
                break;
            case R.id.japanesePick:
                setLanguageBlank(isSecond, isForRewrite, "일본어");
                break;
            case R.id.russianPick:
                setLanguageBlank(isSecond, isForRewrite, "러시아어");
                break;
            case R.id.sharedAcceptButtonForDelete:
                deleteWindow.bringToFront();
                deleteWindow.setVisibility(View.VISIBLE);
                sharedRewriteViewWindow.setVisibility(View.GONE);
                confirmQuistion.setText("정말로 단어장 [" + "" + "]을/를 삭제하시겠습니까?");

                Log.d("삭제하기", "삭제하기 버튼 클릭");
                break;
            case R.id.confirmQuestion:
                break;
            case R.id.sharedAcceptButtonForRewrite:
                if (sharedWordForRewrite.equals("") || sharedWordMeanForRewrite.equals("") || sharedVocabularyNameForRewrite.equals("")) {
                    Toast toast = new Toast(RefinedSharedVocabularyActivity.this);
                    Toast.makeText(RefinedSharedVocabularyActivity.this,
                            "빈칸을 전부 입력하세요.", Toast.LENGTH_LONG).show();
                } else {
                    sharedBackgroundOfFull.setBackgroundColor(Color.parseColor("#00000000"));
                    sharedBackgroundOfFull.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }

                    });
                    isForRewrite = false;
                    sharedRewriteViewWindow.setVisibility(View.GONE);
                }
                sharedVocabularyNameForRewrite.setText("");
                sharedWordForRewrite.setText("");
                sharedWordMeanForRewrite.setText("");
                break;
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
                    Log.i(TAG, "network가 켜진 것을 인식");
                } else {
                    Toast toast = Toast.makeText(RefinedSharedVocabularyActivity.this,
                            "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG);
                    toast.show();
                }

        }
    }

    public void setLanguageBlank(boolean isS, boolean isR, String str) {
        if (isR) {
            if (isS) {
                sharedWordForRewrite.setText(str);
            } else {
                sharedWordMeanForRewrite.setText(str);
            }
            isSecond = !isSecond;
            languagePickerWindow.setVisibility(View.INVISIBLE);
            languagePickerWindowScrollView.fullScroll(0);
            return;
        } else {
            if (isS) {
                sharedWordForRewrite.setText(str);
            } else {
                sharedWordMeanForRewrite.setText(str);
            }
            isSecond = !isSecond;
            languagePickerWindow.setVisibility(View.INVISIBLE);
            languagePickerWindowScrollView.fullScroll(0);
            return;
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sharedRewriteViewWindow.getVisibility() == View.VISIBLE) {
                sharedRewriteViewWindow.setVisibility(View.GONE);
                sharedBackgroundOfFull.setBackgroundColor(Color.parseColor("#00000000"));
                sharedBackgroundOfFull.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
//            if (FilterSortWindow.getVisibility() == View.VISIBLE) {
//                FilterSortWindow.setVisibility(View.GONE);
//                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
//                backgroundView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        return false;
//                    }
//                });
//            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
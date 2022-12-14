package com.example.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;

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

    ArrayList<LocalWordBook> sharedVocaArrayList;

    boolean isNetWork;

    FirestorePagingAdapter<WordBook, WordBookViewHolder> adapter;
    RecyclerView recyclerView;

    @SuppressLint({"LongLogTag", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refined_shared_vocabulary_activity);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        recyclerView = findViewById(R.id.sharedVocabularyRecyclerView);
        // 객체 연결
        backgroundViewOfFull = findViewById(R.id.sharedBackgroundOfFull);
        sharedbackgroundView = findViewById(R.id.shearedBackgroundView);
        networkChecking = findViewById(R.id.networkChecking);
        searchWindow = findViewById(R.id.searchWindow2);
        searchOptionButton = findViewById(R.id.searchOptionButton2);
        searchButton = findViewById(R.id.searchButton2);
        addButton = findViewById(R.id.addButton2);
        sharedbackgroundView.bringToFront();

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
                String id = Objects.requireNonNull(getItem(position)).getId();
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
            case R.id.searchButton2: // 검색 버튼 (미완료)
                String searchString = searchWindow.getText().toString();
                //
                break;
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
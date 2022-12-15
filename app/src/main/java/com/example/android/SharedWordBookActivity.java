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
import android.widget.ImageView;
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

public class SharedWordBookActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "WordBook";

    wordDataBaseHelper wordDB;
    vocaDataBaseHelper vocaDB;

    View backgroundView;
    TextView wordBookNameLabel;
    EditText searchWindow;
    Button searchButton;
    Button searchOptionButton;
    ImageView searchNone;
    ImageView downloadButton;
    ConstraintLayout wordRewriteWindow;

    ImageView backbutton;
    ConstraintLayout FilterSortWindow;
    FrameLayout networkingBackground;
    TextView selectedSort;
    TextView selectedFilter;
    ImageView networkingChecking;
    ImageView heartButton;

    boolean isNetWork;
    int voca_id;
    String id;
    FirestorePagingAdapter<Word, WordViewHolder> adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_shared_word_book);

        heartButton = findViewById(R.id.heartButton);
        backgroundView = findViewById(R.id.backgroundViewForWordActivity);
        wordBookNameLabel = findViewById(R.id.wordBookNameLabel);
        searchWindow = findViewById(R.id.searchWindow);
        searchButton = findViewById(R.id.searchButton);
        searchOptionButton = findViewById(R.id.searchOptionButton);
        searchNone = findViewById(R.id.searchNone);

        FilterSortWindow = findViewById(R.id.select_filter_sort);
        selectedFilter = findViewById(R.id.myVocaFilter);
        selectedSort = findViewById(R.id.myVocaSort);
        downloadButton = findViewById(R.id.downloadButton);
        networkingChecking = findViewById(R.id.networkChecking);
        networkingBackground = findViewById(R.id.networkingBackground);
        backbutton = findViewById(R.id.backButton);
        downloadButton.setOnClickListener(this);
        heartButton.setOnClickListener(this);

        backgroundView.bringToFront();
        backgroundView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        searchButton.setOnClickListener(this);
        searchOptionButton.setOnClickListener(this);
        searchNone.setOnClickListener(this);
        selectedFilter.setOnClickListener(this);
        selectedSort.setOnClickListener(this);
        networkingChecking.setOnClickListener(this);
        backbutton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        Intent intent = getIntent();
        id = intent.getStringExtra("WordBookId");

        int sortNum = 0;


        Query query = FirebaseDB.getWordList(db, sortNum, id);
        PagingConfig config = new PagingConfig(4, 2, false);
        FirestorePagingOptions<Word> options = new FirestorePagingOptions.Builder<Word>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Word.class)
                .build();
        adapter = new FirestorePagingAdapter<Word, WordViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WordViewHolder holder, int position, @NonNull Word model) {
                holder.bind(model);
                String id = getItem(position).getId();
            }

            @NonNull
            @Override
            public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_vocabulary_list_item, parent, false);
                return new WordViewHolder(view);
            }
        };

        //네트워크 연경
        backgroundView.bringToFront();
        networkingBackground.bringToFront();
        networkingChecking.bringToFront();
        isNetWork = isConnected(SharedWordBookActivity.this);
        if (isNetWork == false) {
            backgroundView.setVisibility(View.VISIBLE);
            networkingChecking.setVisibility(View.VISIBLE);
            networkingChecking.bringToFront();
            backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
            backgroundView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

        }

        recyclerView = findViewById(R.id.sharedWordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
    }

    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                Log.i(TAG, "backButton");
                break;
            case R.id.networkChecking:
                isNetWork = isConnected(SharedWordBookActivity.this);
                if (isNetWork) {
                    backgroundView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    networkingChecking.setVisibility(View.INVISIBLE);
                    networkingBackground.setVisibility(View.INVISIBLE);
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(SharedWordBookActivity.this,
                            "네트워크 상태를 확인해주세요.", Toast.LENGTH_LONG);
                    toast.show();
                }
            case R.id.acceptButtonForDeleteWord:
                //리사이클러 뷰 롱클릭시 나오는 뷰에 있음
                break;
            case R.id.acceptButtonForRewriteWord:
                //리사이클러 뷰 롱클릭시 나오는 뷰에 있음
                break;
            case R.id.searchButton:
                String searchContent = searchWindow.getText().toString();
                if (searchContent.equals("")) {
                    Toast toast = new Toast(SharedWordBookActivity.this);
                    Toast.makeText(SharedWordBookActivity.this,
                            "검색어를 입력하시오.", Toast.LENGTH_LONG).show();
                } else {
                    searchNone.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.searchNone:
                searchWindow.setText("");
                searchNone.setVisibility(View.GONE);
                break;
            case R.id.searchOptionButton:
                break;
            case R.id.heartButton:
                heartButton.setBackgroundColor(Color.parseColor("#B22222"));
                heartButton.setBackgroundColor(Color.parseColor("0000000"));
                break;
            case R.id.myVocaFilter:
                //필터 레이아웃 제작 필요
                break;
            case R.id.myVocaSort:
                //필터 레이아웃 제작 필요
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wordRewriteWindow.getVisibility() == View.VISIBLE) {
                wordRewriteWindow.setVisibility(View.GONE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            if (FilterSortWindow.getVisibility() == View.VISIBLE) {
                FilterSortWindow.setVisibility(View.GONE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
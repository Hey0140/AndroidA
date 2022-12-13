package com.example.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.FirestorePagingSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class SharedWordBookActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";

    wordDataBaseHelper wordDB;
    vocaDataBaseHelper vocaDB;

    int voca_id;
    FirestorePagingAdapter<Word, ViewHolder> adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_wordbook_activity);

        recyclerView = findViewById(R.id.sharedWordRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        Intent intent = getIntent();
        String id = intent.getStringExtra("WordBookId");

        int sortNum = 0;

        Query query = FirebaseDB.getWordList(db, sortNum, id);
        PagingConfig config = new PagingConfig(4, 2, false);
        FirestorePagingOptions<Word> options = new FirestorePagingOptions.Builder<Word>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Word.class)
                .build();
        adapter = new FirestorePagingAdapter<Word, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Word model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_vocabulary_list_item, parent, false);
                return new ViewHolder(view);
            }
        };

        RecyclerView recyclerView = findViewById(R.id.sharedWordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }



    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView mWord;
        TextView mMean;
        View mWordItem;

        public ViewHolder(@NonNull View itemView) {
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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.uploadButton:
                LocalWordBook lw = vocaDB.showWordBook(voca_id);
                WordBook wordBook = new WordBook(lw.getName(), 0, 0, mAuth.getUid(), lw.getVoca_mean(), lw.getVoca_word(), null);
                LinkedList<String> mean = new LinkedList<>(wordDB.showMean(voca_id));
                LinkedList<String> word = new LinkedList<>(wordDB.showWord(voca_id));
                int size = mean.size();
                String[] docId = new String[1];
                Thread getWordId = new Thread("getWordId") {
                    @Override
                    public void run() {
                        super.run();
                        String sMean, sWord;

                        for (int i = 0; i < size; i++) {
                            sMean = mean.removeFirst();
                            sWord = word.removeFirst();
                            FirebaseDB.addWord(db, docId[0], sWord, sMean, mAuth.getUid(), null);
                        }
                    }
                };
                FirebaseDB.setWordBook(db, wordBook, getWordId, docId);
                Toast t = Toast.makeText(SharedWordBookActivity.this,
                        "uploadButton", Toast.LENGTH_LONG);
                t.show();
                break;
            case R.id.acceptButtonForDeleteWord:
                break;
            case R.id.acceptButtonForRewriteWord:
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
}
package com.example.android;

import static com.example.android.MyVocabularyActivity.myVocaArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SharedWordBookActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "SharedVocabularyActivity";

    Handler handler = new Handler();

    TextView vocaNameLabel;
    ConstraintLayout rewriteWordWindow;
    ConstraintLayout addWordWindow;
    ImageView backButton;
    ImageButton addButton;
    ImageView uploadButton;
    Button quizButton;
    LinearLayout myWordListItemContainer;
    EditText wordNameForAdd;
    EditText wordMeanForAdd;
    EditText wordNameForRewrite;
    EditText wordMeanForRewrite;
    Button acceptButtonForRewriteButton;
    Button acceptButtonForDeleteButton;
    Button acceptButtonForAddWord;

    wordDataBaseHelper wordDB;
    vocaDataBaseHelper vocaDB;

    int wordId;
    String wordIdString;
    int idForRewrite;
    int voca_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book);


        wordDB = new wordDataBaseHelper(SharedWordBookActivity.this);
        vocaDB = new vocaDataBaseHelper(SharedWordBookActivity.this);

        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        vocaNameLabel = findViewById(R.id.wordBookNameLabel);
        rewriteWordWindow = findViewById(R.id.wordRewriteWindow);
        addWordWindow = findViewById(R.id.addWordWindow);
        backButton = findViewById(R.id.backButton);
        myWordListItemContainer = findViewById(R.id.wordListItemContainer);
        wordNameForAdd = findViewById(R.id.EditTextForAddWord);
        wordMeanForAdd = findViewById(R.id.EditTextForAddWordMean);
        addButton = findViewById(R.id.addButton);
        acceptButtonForAddWord = findViewById(R.id.acceptButtonForAddWord);
        uploadButton = findViewById(R.id.uploadButton);
        quizButton = findViewById(R.id.quizButton);
        wordNameForRewrite = findViewById(R.id.EditTextForRewriteWord);
        wordMeanForRewrite = findViewById(R.id.EditTextForRewriteWordMean);
        acceptButtonForRewriteButton = findViewById(R.id.acceptButtonForRewriteWord);
        acceptButtonForDeleteButton = findViewById(R.id.acceptButtonForDeleteWord);

        quizButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        acceptButtonForAddWord.setOnClickListener(this);
        wordNameForRewrite.setOnClickListener(this);
        wordMeanForRewrite.setOnClickListener(this);
        acceptButtonForRewriteButton.setOnClickListener(this);
        acceptButtonForDeleteButton.setOnClickListener(this);

        addWordWindow.setVisibility(View.GONE);
        rewriteWordWindow.setVisibility(View.GONE);

        Intent intent = getIntent();
        String getName = intent.getStringExtra("단어장 data");
        int tempInt = getName.indexOf("@");
        voca_id = intent.getIntExtra("vocaId", 0);


        wordIdString = getName.substring(0, tempInt);
        String temp2 = getName.substring(tempInt + 1);

        wordId = Integer.parseInt(temp2);

        vocaNameLabel.setText(wordIdString);

        List<String> word = new LinkedList<>(wordDB.showWord(voca_id));
        List<String> mean = new LinkedList<>(wordDB.showMean(voca_id));
        String _mean, _word;


        //처음 시작될 때 화면에 데이터뿌리기


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.addButton:
                addWordWindow.setVisibility(View.VISIBLE);
                quizButton.setVisibility(View.GONE);
                break;
            case R.id.acceptButtonForAddWord:
                String str1 = wordNameForAdd.getText().toString();
                String str2 = wordMeanForAdd.getText().toString();
                wordDB.addWordVoca(str1, str2, voca_id);
                Log.d("asdasd", str1);
                Log.d("asdasd", str2);
                initWord(str1, str2);
                Log.d("hello", "123123");
                addWordWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                wordMeanForAdd.setText("");
                wordNameForAdd.setText("");
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
                deleteWord(idForRewrite);
                rewriteWordWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                break;
            case R.id.acceptButtonForRewriteWord:
                rewriteWord(idForRewrite);
                wordNameForRewrite.setText("");
                wordMeanForRewrite.setText("");
                rewriteWordWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                break;
            case R.id.quizButton:
                Log.d("intent 클릭", "vocaId 전송");
                startActivity(new Intent(SharedWordBookActivity.this, MywordQuizActivity.class));
                int idx = wordId / 5 - 1;
                ArrayList<String> wordList = new ArrayList<>(); // 신경X
                ArrayList<String> meanList = new ArrayList<>(); // 신경X
                int stringIndex = 0;
                Intent intent = new Intent(SharedWordBookActivity.this, MywordQuizActivity.class);
                for (int i = 0; i < myVocaArrayList.get(idx).word.size(); i++) {
                    int id = wordId * 1000 + (i + 1) * 5 + 3;
                    CheckBox temp = findViewById(id);
                    TextView tempWord = findViewById(id - 2);
                    TextView tempMean = findViewById(id - 1);
                    if (temp.isChecked()) {
                        wordList.add(tempWord.getText().toString());
                        meanList.add(tempMean.getText().toString());
                    }
                }

                intent.putStringArrayListExtra("wordList", wordList);
                intent.putStringArrayListExtra("meanList", meanList);
                intent.putExtra("VocaId", 3);
                intent.putExtra("size", wordList.size());

                break;
        }
    }


    public void initWord(String word, String wordMean) {
        int idx = wordId / 5 - 1;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_word_listitem, myWordListItemContainer, true);
        myVocaArrayList.get(idx).word.addLast(word);
        myVocaArrayList.get(idx).mean.addLast(wordMean);
        View tempView = myWordListItemContainer.findViewById(R.id.wordView);
        tempView.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5); // 뷰 id
        Log.d("setID : ", Integer.toString(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5));
        tempView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rewriteWordWindow.setVisibility(View.VISIBLE);
                quizButton.setVisibility(View.GONE);
                idForRewrite = v.getId();
                Log.d("idForRewrite : ", Integer.toString(idForRewrite));
                return true;
            }
        });
        myVocaArrayList.get(idx).wordView.addLast(tempView);
        TextView tempWord = myWordListItemContainer.findViewById(R.id.word);
        tempWord.setText(word);
        tempWord.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 1); // 단어 id
        TextView tempMean = myWordListItemContainer.findViewById(R.id.mean);
        tempMean.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 2); // 단어 뜻 id
        tempMean.setText(wordMean);
        CheckBox tempBox = myWordListItemContainer.findViewById(R.id.checkBox);
        tempBox.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 3); // 체크 박스 id
    }

    public void rewriteWord(int reId) {
        int index = wordId / 5 - 1;
        int arrIndex = (reId % 5000) / 5 - 1;

        TextView tempWord = myWordListItemContainer.findViewById(reId + 1);
        TextView tempMean = myWordListItemContainer.findViewById(reId + 2);
        String temp1 = wordNameForRewrite.getText().toString();
        String temp2 = wordMeanForRewrite.getText().toString();
        myVocaArrayList.get(index).word.set(arrIndex, temp1);
        myVocaArrayList.get(index).mean.set(arrIndex, temp2);
        tempWord.setText(temp1);
        tempMean.setText(temp2);

    }

    public void deleteWord(int reId)
    //reid == 5010
    {
        int index = wordId / 5 - 1;
        int arrIndex = (reId % 5000) / 5 - 1; // 1
        int lastId = reId + (myVocaArrayList.get(index).word.size() - 1 - arrIndex) * 5;

        myVocaArrayList.get(index).word.remove(arrIndex);
        myVocaArrayList.get(index).mean.remove(arrIndex);
        myVocaArrayList.get(index).wordView.remove(arrIndex);

        View delView = findViewById(reId);
        myWordListItemContainer.removeView((View) delView.getParent());
        Log.d("reId : ", Integer.toString(reId));
        Log.d("arrIndex : ", Integer.toString(arrIndex));
        for (int i = reId + 5; i <= lastId; i += 5) {
            // i == 5010 |
            View one = myWordListItemContainer.findViewById(i);

            one.setId(i - 5);

            TextView two = myWordListItemContainer.findViewById(i + 1);
            two.setId(i - 4);

            TextView three = myWordListItemContainer.findViewById(i + 2);
            three.setId(i - 3);

            TextView four = myWordListItemContainer.findViewById(i + 3);
            four.setId(i - 2);
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
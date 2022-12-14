package com.example.android;

import static com.example.android.MyVocabularyActivity.myVocaArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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


public class WordBookActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "WordBook";

    Handler handler = new Handler();

    ConstraintLayout wordDeleteViewWindow;
    Button wordAcceptButtonForDeleteConfirm;
    TextView wordDeleteConfirmText;
    ConstraintLayout quizSelectWindow;
    ImageView searchNone;
    EditText searchWindow;
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
    Button acceptButtonForDeleteWord;
    Button acceptButtonForAddWord;
    Button searchButton;
    Button wordDeleteButton;
    Button searchOptionButton;


    Button wordQuizButton;
    Button meanQuizButton;
    View backgroundView;
    View myWordListItem;


    wordDataBaseHelper wordDB;
    vocaDataBaseHelper vocaDB;

    int wordId;
    String wordIdString;
    int idForRewrite;
    int voca_id;
    int word_id;
    boolean isWordQuiz = false;

    LinkedList<LocalWordBook> myWordBookList;
    LinkedList<String> word;
    LinkedList<String> mean;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book);

        wordDB = new wordDataBaseHelper(WordBookActivity.this);
        vocaDB = new vocaDataBaseHelper(WordBookActivity.this);

        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        searchOptionButton = findViewById(R.id.searchOptionButton);
        wordDeleteButton = findViewById(R.id.wordDeleteButton);
        wordDeleteConfirmText = findViewById(R.id.wordConfirmQuestion);
        wordDeleteViewWindow = findViewById(R.id.wordDeleteWindow);
        searchButton = findViewById(R.id.searchButton3);
        searchNone = findViewById(R.id.searchNone3);
        searchWindow = findViewById(R.id.searchWindow);
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
        acceptButtonForDeleteWord = findViewById(R.id.acceptButtonForDeleteWord);
        quizSelectWindow = findViewById(R.id.quizChooseView);
        wordQuizButton = findViewById(R.id.wordQuiz);
        meanQuizButton = findViewById(R.id.meanQuiz);
        backgroundView = findViewById(R.id.backgroundViewForWordActivity);

        acceptButtonForDeleteWord.setOnClickListener(this);


        searchButton.setOnClickListener(this);
        quizButton.setOnClickListener(this);
        searchOptionButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        acceptButtonForAddWord.setOnClickListener(this);
        wordNameForRewrite.setOnClickListener(this);
        wordMeanForRewrite.setOnClickListener(this);
        acceptButtonForRewriteButton.setOnClickListener(this);
        acceptButtonForRewriteButton.setOnClickListener(this);
        wordQuizButton.setOnClickListener(this);
        meanQuizButton.setOnClickListener(this);
        wordQuizButton.setOnClickListener(this);
        wordDeleteButton.setOnClickListener(this);

        addWordWindow.setVisibility(View.GONE);
        rewriteWordWindow.setVisibility(View.GONE);
        wordDeleteViewWindow.setVisibility(View.GONE);


        Intent intent = getIntent();
        String getName = intent.getStringExtra("vocaName");
        int tempInt = getName.indexOf("@");
        voca_id = intent.getIntExtra("vocaId", 0);

        wordIdString = getName.substring(0, tempInt);
        String temp2 = getName.substring(tempInt + 1);

        wordId = Integer.parseInt(temp2);

        vocaNameLabel.setText(wordIdString);

        Log.i(TAG, "????????? ??? ??? ?????? ??? ?????? ?????? ?????????");
        quizButton.setVisibility(View.VISIBLE);

        word = new LinkedList<>(wordDB.showWord(voca_id));
        mean = new LinkedList<>(wordDB.showMean(voca_id));

        int tempidx = wordId / 5 - 1;
        String _mean, _word;
        myVocaArrayList.get(tempidx).word.clear();
        myVocaArrayList.get(tempidx).mean.clear();

        backgroundView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        backgroundView.bringToFront();
        //?????? ????????? ??? ????????? ??????????????????
        for (int i = 0; i < mean.size(); i++) {

            _mean = mean.get(i);
            _word = word.get(i);
            int idx = wordId / 5 - 1;
            int wid = wordDB.showIdOfWord(_word, _mean, voca_id);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_word_listitem, myWordListItemContainer, true);
            myVocaArrayList.get(idx).word.add(i, _word);
            myVocaArrayList.get(idx).mean.add(i, _mean);
            View tempView = myWordListItemContainer.findViewById(R.id.wordView);
            tempView.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5); // ??? id
            Log.d("setID : ", Integer.toString(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5));
            tempView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    backgroundView.bringToFront();
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addWordWindow.setVisibility(View.GONE);
                            return true;
                        }
                    });
                    rewriteWordWindow.bringToFront();
                    rewriteWordWindow.setVisibility(View.VISIBLE);
                    quizButton.setVisibility(View.GONE);
                    idForRewrite = v.getId();
                    word_id = wid;
                    Log.d("idForRewrite : ", Integer.toString(idForRewrite));
                    return true;
                }
            });
            myVocaArrayList.get(idx).wordView.addLast(tempView);
            TextView tempWord = myWordListItemContainer.findViewById(R.id.word);
            tempWord.setText(_word);
            tempWord.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 1); // ?????? id
            TextView tempMean = myWordListItemContainer.findViewById(R.id.mean);
            tempMean.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 2); // ?????? ??? id
            tempMean.setText(_mean);
            CheckBox tempBox = myWordListItemContainer.findViewById(R.id.checkBox);
            tempBox.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 3); // ?????? ?????? id
        }
    }

//    private class VocaAdapter extends RecyclerView.Adapter<ViewHolder> {
//        private final ArrayList<String> word;
//        private final ArrayList<String> mean;
//
//        public VocaAdapter(ArrayList<String> word, ArrayList<String> mean) {
//            this.word = word;
//            this.mean = mean;
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.my_word_listitem, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
//            String temp1 = word.get(position);
//            String temp2 = mean.get(position);
//            assert temp1 != null;
//            assert temp2 != null;
//            viewHolder.mWord.setText(temp1);
//            viewHolder.mMean.setText(temp2);
//        }
//
//        @Override
//        public int getItemCount() {
//            return word.size();
//        }
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton3:
                Log.d(TAG, "MyVoca| search button");
                String searchWindowString = searchWindow.getText().toString();
                if (searchWindowString.equals("")) {
                    Toast toast = new Toast(WordBookActivity.this);
                    Toast.makeText(WordBookActivity.this,
                            "???????????? ???????????????.", Toast.LENGTH_LONG).show();
                } else {
                    searchNone.setVisibility(View.VISIBLE);
                    Search(searchWindowString);
                }
                break;
            case R.id.searchNone3:
                searchWindow.setText("");
                nonSearch();
                searchNone.setVisibility(View.INVISIBLE);
                break;
            case R.id.addButton:
                Log.d(TAG, "MyVoca| add button");
                backgroundView.bringToFront();
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                addWordWindow.setVisibility(View.VISIBLE);
                addWordWindow.bringToFront();
                quizButton.setVisibility(View.GONE);
                break;
            case R.id.acceptButtonForAddWord:
                Log.d(TAG, "MyVoca| accept button");
                String str1 = wordNameForAdd.getText().toString();
                String str2 = wordMeanForAdd.getText().toString();
                if (str1.equals("") || str2.equals("")) {
                    Toast toast = new Toast(WordBookActivity.this);
                    Toast.makeText(WordBookActivity.this,
                            "????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
//                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    wordDB.addWordVoca(str1, str2, voca_id);
                    initWord(str1, str2);
                    vocaDB.updatePlusCount(voca_id);
                    addWordWindow.setVisibility(View.INVISIBLE);
                    quizButton.setVisibility(View.VISIBLE);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    wordMeanForAdd.setText("");
                    wordNameForAdd.setText("");
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                }

                break;
            case R.id.uploadButton:
                Log.d(TAG, "MyVoca| upload button");

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
                Toast t = Toast.makeText(WordBookActivity.this,
                        "uploadButton", Toast.LENGTH_LONG);
                t.show();
                break;
            case R.id.acceptButtonForDeleteWord:
                Log.d(TAG, "MyVoca| accept button");

                backgroundView.bringToFront();
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                wordDeleteViewWindow.bringToFront();
                wordDeleteViewWindow.setVisibility(View.VISIBLE);
                rewriteWordWindow.setVisibility(View.GONE);
                wordDeleteConfirmText.setText("????????? ????????? [" + wordDB.showWordVoca(word_id, voca_id) + "]???/??? ?????????????????????????");
                Log.d("????????????", "???????????? ?????? ??????");
                break;
            case R.id.wordDeleteButton:
                Log.d(TAG, "MyVoca| delete button");

                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                deleteWord(idForRewrite, word_id);
                wordDeleteViewWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                vocaDB.updateMinusCount(voca_id);
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
                break;
            case R.id.acceptButtonForRewriteWord:
                Log.d(TAG, "MyVoca| accept button");

                String temp1 = wordNameForRewrite.getText().toString();
                String temp2 = wordMeanForRewrite.getText().toString();
                if (temp1.equals("") || temp2.equals("")) {
                    Toast toast = new Toast(WordBookActivity.this);
                    Toast.makeText(WordBookActivity.this,
                            "????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
//                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    rewriteWord(idForRewrite, word_id);
                    wordNameForRewrite.setText("");
                    wordMeanForRewrite.setText("");
                    quizButton.setVisibility(View.VISIBLE);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            rewriteWordWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });

                }
                break;
            case R.id.quizButton:
                Log.d(TAG, "MyVoca| quiz button");

                //startActivity(new Intent(WordBookActivity.this,MywordQuizActivity.class));
                quizSelectWindow.setVisibility(View.VISIBLE);
                quizButton.setVisibility(View.GONE);
                backgroundView.bringToFront();
                quizSelectWindow.bringToFront();
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                break;
            case R.id.wordQuiz:
                Log.d(TAG, "MyVoca| word quiz button");

                isWordQuiz = true;
                int idx = wordId / 5 - 1;
                Intent intent = new Intent(WordBookActivity.this, MywordQuizActivity.class);
                ArrayList<String> wordList = new ArrayList<>(); // ??????X
                ArrayList<String> meanList = new ArrayList<>(); // ??????X
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
                if (wordList.size() == 0) {
                    Toast toast = new Toast(WordBookActivity.this);
                    Toast.makeText(WordBookActivity.this,
                            "????????? ????????? ????????? ?????? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    quizSelectWindow.setVisibility(View.GONE);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    quizButton.setVisibility(View.VISIBLE);
                } else {
                    intent.putStringArrayListExtra("wordList", wordList);
                    intent.putStringArrayListExtra("meanList", meanList);
                    int wordsize = wordList.size();
                    intent.putExtra("size", wordsize);
                    Log.i(TAG, Integer.toString(wordsize));
                    intent.putExtra("isWordQuiz", isWordQuiz);
                    Log.i(TAG, "Exta??????");
                    quizSelectWindow.setVisibility(View.GONE);
                    startActivity(intent);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    quizButton.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.meanQuiz:
                Log.d(TAG, "MyVoca| mean quiz button");

                isWordQuiz = false;
                int idx2 = wordId / 5 - 1;
                ArrayList<String> _wordList = new ArrayList<>(); // ??????X
                ArrayList<String> _meanList = new ArrayList<>();
                Intent intent2 = new Intent(WordBookActivity.this, MywordQuizActivity.class);
                for (int i = 0; i < myVocaArrayList.get(idx2).word.size(); i++) {
                    int id = wordId * 1000 + (i + 1) * 5 + 3;
                    CheckBox temp = findViewById(id);
                    TextView tempWord = findViewById(id - 2);
                    TextView tempMean = findViewById(id - 1);
                    if (temp.isChecked()) {
                        _wordList.add(tempWord.getText().toString());
                        _meanList.add(tempMean.getText().toString());
                    }
                }

                if (_wordList.size() == 0) {
                    Toast toast = new Toast(WordBookActivity.this);
                    Toast.makeText(WordBookActivity.this,
                            "????????? ????????? ????????? ?????? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    quizSelectWindow.setVisibility(View.GONE);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    quizButton.setVisibility(View.VISIBLE);
                } else {
                    intent2.putStringArrayListExtra("wordList", _wordList);
                    intent2.putStringArrayListExtra("meanList", _meanList);
                    intent2.putExtra("size", _wordList.size());
                    Log.i(TAG, Integer.toString(_wordList.size()));
                    intent2.putExtra("isWordQuiz", isWordQuiz);
                    Log.i(TAG, "Exta??????");
                    quizSelectWindow.setVisibility(View.GONE);
                    startActivity(intent2);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    quizButton.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.backButton:
                Log.d(TAG, "MyVoca| back button");

                Intent backintent = new Intent(WordBookActivity.this, MyVocabularyActivity.class);
                backintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backintent);
                break;
            case R.id.searchOptionButton:
                break;

        }
    }

    @Override
    protected void onRestart() {
        int idx2 = wordId / 5 - 1;
        for (int i = 0; i < myVocaArrayList.get(idx2).word.size(); i++) {
            int id = wordId * 1000 + (i + 1) * 5 + 3;
            CheckBox temp = findViewById(id);
        }
        super.onRestart();
    }

    private void Search(String searchWindowString) {
        clearVocaView();
        int idex = wordId / 5 - 1;
        myVocaArrayList.get(idex).word.clear();
        myVocaArrayList.get(idex).mean.clear();
        List<String> word = new LinkedList<>(wordDB.showFilterWordOnWord(voca_id, searchWindowString));
        List<String> mean = new LinkedList<>(wordDB.showFilterWordOnMean(voca_id, searchWindowString));
        String _mean, _word;

        //?????? ????????? ??? ????????? ??????????????????
        for (int i = 0; i < mean.size(); i++) {
            _mean = mean.get(i);
            _word = word.get(i);
            int idx = wordId / 5 - 1;
            int wid = wordDB.showIdOfWord(_word, _mean, voca_id);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_word_listitem, myWordListItemContainer, true);
            myVocaArrayList.get(idx).word.addLast(_word);
            myVocaArrayList.get(idx).mean.addLast(_mean);
            View tempView = myWordListItemContainer.findViewById(R.id.wordView);
            tempView.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5); // ??? id

            tempView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "MyVoca| long click");

                    backgroundView.bringToFront();
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    rewriteWordWindow.bringToFront();
                    rewriteWordWindow.setVisibility(View.VISIBLE);
                    quizButton.setVisibility(View.GONE);
                    idForRewrite = v.getId();
                    word_id = wid;
                    return true;
                }
            });
            myVocaArrayList.get(idx).wordView.addLast(tempView);
            TextView tempWord = myWordListItemContainer.findViewById(R.id.word);
            tempWord.setText(_word);
            tempWord.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 1); // ?????? id
            TextView tempMean = myWordListItemContainer.findViewById(R.id.mean);
            tempMean.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 2); // ?????? ??? id
            tempMean.setText(_mean);
            CheckBox tempBox = myWordListItemContainer.findViewById(R.id.checkBox);
            tempBox.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 3); // ?????? ?????? id
        }
    }

    private void nonSearch() {
        clearVocaView();
        int idex = wordId / 5 - 1;
        myVocaArrayList.get(idex).word.clear();
        myVocaArrayList.get(idex).mean.clear();
        List<String> word = new LinkedList<>(wordDB.showWord(voca_id));
        List<String> mean = new LinkedList<>(wordDB.showMean(voca_id));
        String _mean, _word;

        //?????? ????????? ??? ????????? ??????????????????
        for (int i = 0; i < mean.size(); i++) {
            _mean = mean.get(i);
            _word = word.get(i);
            int idx = wordId / 5 - 1;
            int wid = wordDB.showIdOfWord(_word, _mean, voca_id);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_word_listitem, myWordListItemContainer, true);
            myVocaArrayList.get(idx).word.addLast(_word);
            myVocaArrayList.get(idx).mean.addLast(_mean);
            View tempView = myWordListItemContainer.findViewById(R.id.wordView);
            tempView.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5); // ??? id
            Log.d(TAG, "MyVoca| setID : " + (wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5));
            tempView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "MyVoca| long click");
                    backgroundView.bringToFront();
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    rewriteWordWindow.bringToFront();
                    rewriteWordWindow.setVisibility(View.VISIBLE);
                    quizButton.setVisibility(View.GONE);
                    idForRewrite = v.getId();
                    word_id = wid;
                    return true;
                }
            });
            myVocaArrayList.get(idx).wordView.addLast(tempView);
            TextView tempWord = myWordListItemContainer.findViewById(R.id.word);
            tempWord.setText(_word);
            tempWord.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 1); // ?????? id
            TextView tempMean = myWordListItemContainer.findViewById(R.id.mean);
            tempMean.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 2); // ?????? ??? id
            tempMean.setText(_mean);
            CheckBox tempBox = myWordListItemContainer.findViewById(R.id.checkBox);
            tempBox.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 3); // ?????? ?????? id
        }
    }


    public void initWord(String word, String wordMean) {
        int idx = wordId / 5 - 1;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_word_listitem, myWordListItemContainer, true);
        myVocaArrayList.get(idx).word.addLast(word);
        myVocaArrayList.get(idx).mean.addLast(wordMean);
        myVocaArrayList.get(idx).setCount(myVocaArrayList.get(idx).getCount() + 1);
        View tempView = myWordListItemContainer.findViewById(R.id.wordView);
        tempView.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5); // ??? id
        Log.d(TAG, "MyVoca|setID : " + (wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5));
        int wid = wordDB.showIdOfWord(word, wordMean, voca_id);
        tempView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                backgroundView.bringToFront();
                Log.d(TAG, "MyVoca| long click");

                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                rewriteWordWindow.bringToFront();
                rewriteWordWindow.setVisibility(View.VISIBLE);
                quizButton.setVisibility(View.GONE);
                idForRewrite = v.getId();
                word_id = wid;
                return true;
            }
        });
        myVocaArrayList.get(idx).wordView.addLast(tempView);
        TextView tempWord = myWordListItemContainer.findViewById(R.id.word);
        tempWord.setText(word);
        tempWord.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 1); // ?????? id
        TextView tempMean = myWordListItemContainer.findViewById(R.id.mean);
        tempMean.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 2); // ?????? ??? id
        tempMean.setText(wordMean);
        CheckBox tempBox = myWordListItemContainer.findViewById(R.id.checkBox);
        tempBox.setId(wordId * 1000 + myVocaArrayList.get(idx).word.size() * 5 + 3); // ?????? ?????? id
    }

    public void rewriteWord(int reId, int word_id) {
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
        wordDB.updateWordVoca(word_id, temp1, temp2, voca_id);

    }

    public String deleteWord(int reId, int word_id)
    //reid == 5010
    {
        int index = wordId / 5 - 1;
        int arrIndex = (reId % 5000) / 5 - 1; // 1
        int lastId = reId + (myVocaArrayList.get(index).word.size() - 1 - arrIndex) * 5;
        String temp_word = myVocaArrayList.get(index).word.get(arrIndex);
        myVocaArrayList.get(index).word.remove(arrIndex);
        myVocaArrayList.get(index).mean.remove(arrIndex);
        myVocaArrayList.get(index).wordView.remove(arrIndex);
        myVocaArrayList.get(index).setCount(myVocaArrayList.get(index).getCount() - 1);

        View delView = findViewById(reId);
        myWordListItemContainer.removeView((View) delView.getParent());
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
        wordDB.deleteWordVoca(word_id, voca_id);
        return temp_word;
    }


    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "MyVoca| signInAnonymously:success " + mAuth.getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "MyVoca| signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

    public void clearVocaView() {
        myWordListItemContainer.removeAllViews();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (addWordWindow.getVisibility() == View.VISIBLE) {
                addWordWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            } else if (quizSelectWindow.getVisibility() == View.VISIBLE) {
                quizSelectWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            } else if (wordDeleteViewWindow.getVisibility() == View.VISIBLE) {
                wordDeleteViewWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            } else if (rewriteWordWindow.getVisibility() == View.VISIBLE) {
                rewriteWordWindow.setVisibility(View.GONE);
                quizButton.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            } else {
                Intent backintent = new Intent(WordBookActivity.this, MyVocabularyActivity.class);
                backintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backintent);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
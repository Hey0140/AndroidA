package com.example.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ScrollView;
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

import java.util.LinkedList;


public class MyVocabularyActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private final static String TAG = "WordBook";

    // 화면 스와이프를 위한 좌표
    float x1, x2, y1, y2;
    public static LinkedList<LocalWordBook> myVocaArrayList = new LinkedList<>();

    View slide;

    // 객체 연결
    EditText searchWindow;
    Button searchOptionButton;
    Button searchButton;
    ImageButton addButton;
    ImageView searchNone;


    View backgroundView;
    ScrollView myVocaListScrollView;
    // 단어장 뷰 생성을 위한 콘테이너
    LinearLayout myVocaContainer;

    // 단어생성 뷰 연결
    ScrollView languagePickerScrollView;
    Button acceptButtonForAdd;
    EditText vocaNameForAdd;
    Button wordForAdd;
    Button wordMeanForAdd;
    ConstraintLayout addViewWindow;
    ConstraintLayout languagePickerWindow;

    // 단어수정 뷰 연결
    ImageView addButtonBackground;
    ConstraintLayout rewriteViewWindow;
    ConstraintLayout deleteViewWindow;
    EditText vocaNameForRewrite;
    Button acceptButtonForRewrite;
    Button acceptButtonForDelete;
    Button wordForRewrite;
    Button wordMeanForRewrite;
    Button acceptButtonForDeleteConfirm;
    TextView deleteConfirmText;
    ConstraintLayout filterSort;
    TextView myVocaFilter;
    TextView myVocaSort;


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

    //정렬 윈도우
    ConstraintLayout sortWindow;
    CheckBox nameInc;
    CheckBox nameDecs;
    CheckBox dateInc;
    CheckBox dateDesc;
    CheckBox likeInc;
    CheckBox likeDesc;
    Button sortAcceptButton;


    //디비 관련 변수
    public vocaDataBaseHelper vocabularyDB;
    public wordDataBaseHelper wordDataBase;
    public static int vcount = 0;
    int vocaId;


    // 단어 피커 뷰를 위한 변수
    boolean isSecond = true;
    boolean isForRewrite = false;
    boolean isReset = false;
    int idForRewrite;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_vocabulary_activity);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        vocabularyDB = new vocaDataBaseHelper(this);
        wordDataBase = new wordDataBaseHelper(this);

        // 객체 연결
        searchNone = findViewById(R.id.searchNone);
        searchWindow = findViewById(R.id.searchWindow);
        searchOptionButton = findViewById(R.id.searchOptionButton);
        searchButton = findViewById(R.id.searchButton);
        addButton = findViewById(R.id.addButton);
        searchButton = findViewById(R.id.searchButton);
        addViewWindow = findViewById(R.id.addViewWindow);
        acceptButtonForAdd = findViewById(R.id.acceptButton);
        addViewWindow.setVisibility(View.GONE);
        vocaNameForAdd = findViewById(R.id.vocabularyNameForAdd);
        wordForAdd = findViewById(R.id.wordForAdd);
        wordMeanForAdd = findViewById(R.id.wordMeanForAdd);
        myVocaContainer = findViewById(R.id.vocabularyListItemContainer);
        languagePickerWindow = findViewById(R.id.languagePickerWindow);
        deleteViewWindow = findViewById(R.id.deleteWindow);
        acceptButtonForDeleteConfirm = findViewById(R.id.deleteButton);
        deleteConfirmText = findViewById(R.id.confirmQuestion);
        languagePickerScrollView = findViewById(R.id.languagePickerWindowScrollView);
        backgroundView = findViewById(R.id.backgroundView);
        addButtonBackground = findViewById(R.id.addButtonBackground);

        filterSort = findViewById(R.id.select_filter_sort);
        myVocaFilter = findViewById(R.id.myVocaFilter);
        myVocaSort = findViewById(R.id.myVocaSort);

        sortWindow = findViewById(R.id.sortWindow);
        nameInc = findViewById(R.id.nameInc);
        nameDecs = findViewById(R.id.nameDesc);
        dateInc = findViewById(R.id.dateInc);
        dateDesc = findViewById(R.id.dateDesc);
        likeInc = findViewById(R.id.likeInc);
        likeDesc = findViewById(R.id.likeDesc);
        sortAcceptButton = findViewById(R.id.sortAcceptButton);
        sortAcceptButton.setOnClickListener(this);
        filterSort.setOnClickListener(this);
        myVocaSort.setOnClickListener(this);
        myVocaFilter.setOnClickListener(this);
        searchOptionButton.setOnClickListener(this);


        slide = findViewById(R.id.slide_view);
        slide.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                Intent i = new Intent(MyVocabularyActivity.this, RefinedSharedVocabularyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
                addViewWindow.setVisibility(View.GONE);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }
        });


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

        rewriteViewWindow = findViewById(R.id.rewriteViewWindow);
        acceptButtonForRewrite = findViewById(R.id.acceptButtonForRewrite);
        wordForRewrite = findViewById(R.id.wordForRewrite);
        wordMeanForRewrite = findViewById(R.id.wordMeanForRewrite);
        acceptButtonForDelete = findViewById(R.id.acceptButtonForDelete);
        vocaNameForRewrite = findViewById(R.id.vocabularyNameForRewrite);
        myVocaListScrollView = findViewById(R.id.listScrollView);

        // 객체 이벤트 리스너 등록
        addButtonBackground.setOnClickListener(this);
        searchNone.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        acceptButtonForAdd.setOnClickListener(this);
        wordForAdd.setOnClickListener(this);
        wordMeanForAdd.setOnClickListener(this);
        rewriteViewWindow.setOnClickListener(this);
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
        acceptButtonForDelete.setOnClickListener(this);
        wordForRewrite.setOnClickListener(this);
        wordMeanForRewrite.setOnClickListener(this);
        acceptButtonForRewrite.setOnClickListener(this);
        acceptButtonForDeleteConfirm.setOnClickListener(this);


        backgroundView.bringToFront();
        backgroundView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        languagePickerWindow.setVisibility(View.GONE);
        rewriteViewWindow.setVisibility(View.GONE);
        deleteViewWindow.setVisibility(View.GONE);


        myVocaArrayList = vocabularyDB.showVoca();
        for (int i = 0; i < myVocaArrayList.size(); i++) {
            int idEdit = (i + 1) * 5;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
            int _vocaid = myVocaArrayList.get(i).getVocabulary_id();
            int voca_count = myVocaArrayList.get(i).getCount();
            String vocabularyName = myVocaArrayList.get(i).getName();
            String[] relation = myVocaArrayList.get(i).languageRelation.split("/");
            String word;
            String wordMean;
            String date = myVocaArrayList.get(i).getDate();
            //혹시 길이가 0일 경우
            //근데 0일 경우 없도록 밑에서 설정해두기는 함
            if (relation.length == 0) {
                word = "";
                wordMean = "";
            } else {
                word = relation[0];
                wordMean = relation[1];

            }
            backgroundView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });

            myVocaArrayList.get(i).v1 = findViewById(R.id.view);
            myVocaArrayList.get(i).v1.setId(idEdit);
            myVocaArrayList.get(i).v1.bringToFront();
            myVocaArrayList.get(i).v1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //롱클릭 시 발동함
                    Log.d(TAG, "뷰의 아이디 : " + v.getId());
                    rewriteViewWindow.setVisibility(View.VISIBLE);
                    wordForRewrite.setText(word);
                    wordMeanForRewrite.setText(wordMean);
                    vocaNameForRewrite.setText(vocabularyName);
                    idForRewrite = v.getId();
                    rewriteViewWindow.bringToFront();
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return true;
                        }
                    });
                    vocaId = _vocaid;
                    Log.d(TAG, "단어장 SQLite _id : " + vocaId);
                    Log.d(TAG, "단어장 수정" + vocaId);
                    isForRewrite = true;
                    return true;
                }
            });

            //수정
            myVocaArrayList.get(i).v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyVocabularyActivity.this, WordBookActivity.class);
                    vocaId = _vocaid;
                    intent.putExtra("vocaName", getWordBookNameString(v.getId()));
                    intent.putExtra("vocaId", vocaId);
                    Log.d(TAG, "단어장 SQLite _id : " + vocaId);
                    Log.d(TAG, "단어장 단어부분으로 intent 이동");
                    startActivity(intent);
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
            three.setText(date);
            TextView five = myVocaContainer.findViewById(R.id.wordCount);
            five.setId(idEdit + 4);
            Log.d(TAG, "단어장 ArrayList id : " + _vocaid);
            Log.d(TAG, Integer.toString(vocaId));
            five.setText(Integer.toString(voca_count));

            myVocaContainer.bringToFront();
        }
        addButtonBackground.bringToFront();
        addButton.bringToFront();
    }

    // 버튼 클릭 이벤트 구현
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton: // 검색 버튼 클릭시
                String searchWindowString = searchWindow.getText().toString();
                if (searchWindowString.equals("")) {
                    Toast toast = new Toast(MyVocabularyActivity.this);
                    Toast.makeText(MyVocabularyActivity.this,
                            "검색어를 입력하시오.", Toast.LENGTH_LONG).show();
                } else {
                    searchNone.setVisibility(View.VISIBLE);
                    Search(searchWindowString);
                }
                break;
            case R.id.addButton:
            case R.id.addButtonBackground:
                Log.d(TAG, "");
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                vocaNameForAdd.setText("");
                wordForAdd.setText("");
                wordMeanForAdd.setText("");
                addViewWindow.bringToFront();
                addViewWindow.setVisibility(View.VISIBLE);
                Log.d(TAG, "show add View");
                isForRewrite = false;
                break;
            case R.id.searchNone:
                searchWindow.setText("");
                nonSearch();
                searchNone.setVisibility(View.GONE);
                break;
            case R.id.acceptButton: // 단어장 [생성하기] 버튼 클릭시
                String vocaName = vocaNameForAdd.getText().toString();
                String wordLang = wordForAdd.getText().toString();
                String meanLang = wordMeanForAdd.getText().toString();
                //셋 중 빈칸이 하나라도 있을 경우 입력되지 못하게 해둠
                if (vocaName.equals("") || wordLang.equals("") || meanLang.equals("")) {
                    Toast toast = new Toast(MyVocabularyActivity.this);
                    Toast.makeText(MyVocabularyActivity.this,
                            "빈칸을 전부 입력하세요.", Toast.LENGTH_LONG).show();
//                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    addViewWindow.setVisibility(View.GONE);
                    java.util.Date date = new java.util.Date(System.currentTimeMillis());
                    vocabularyDB.addVoca(vocaName, wordLang, meanLang, date);
                    isReset = true;
                    initMyVocabulary(getVocabularyNameForAdd(), wordLang, meanLang);
                    vocaNameForAdd.setText(null);
                    wordForAdd.setText(null);
                    wordMeanForAdd.setText(null);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                }
                break;
            case R.id.wordForAdd:
            case R.id.wordMeanForAdd:
                languagePickerWindow.bringToFront();
                languagePickerWindow.setVisibility(View.VISIBLE);
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
            case R.id.acceptButtonForRewrite:
                if (vocaNameForRewrite.equals("") || wordForRewrite.equals("") || wordMeanForRewrite.equals("")) {
                    Toast toast = new Toast(MyVocabularyActivity.this);
                    Toast.makeText(MyVocabularyActivity.this,
                            "빈칸을 전부 입력하세요.", Toast.LENGTH_LONG).show();
                } else {
                    rewriteVocabulary(idForRewrite, vocaId);
                    backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                    isForRewrite = false;
                }
                wordMeanForRewrite.setText("");
                wordForRewrite.setText("");
                vocaNameForRewrite.setText("");
                break;
            case R.id.wordMeanForRewrite:
            case R.id.wordForRewrite:
                languagePickerWindow.setVisibility(View.VISIBLE);
                break;
            case R.id.acceptButtonForDelete:
                deleteViewWindow.bringToFront();
                deleteViewWindow.setVisibility(View.VISIBLE);
                rewriteViewWindow.setVisibility(View.GONE);
                deleteConfirmText.setText("정말로 단어장 [" + vocabularyDB.showName(vocaId) + "]을/를 삭제하시겠습니까?");
                Log.d("삭제하기", "삭제하기 버튼 클릭");
                break;
            case R.id.deleteButton:
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                deleteVoca(idForRewrite, vocaId);
                deleteViewWindow.setVisibility(View.GONE);
                break;
            case R.id.searchOptionButton:
                filterSort.bringToFront();
                filterSort.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        addViewWindow.setVisibility(View.GONE);
                        return true;
                    }
                });
                break;
            case R.id.myVocaFilter:
                filterSort.setVisibility(View.GONE);
                break;
            case R.id.myVocaSort:
                filterSort.setVisibility(View.GONE);
                sortWindow.setVisibility(View.VISIBLE);
                sortWindow.bringToFront();
                /*
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        addViewWindow.setVisibility(View.GONE);
                        return true;
                    }
                });*/
                break;
            case R.id.sortAcceptButton:
                sortWindow.setVisibility(View.INVISIBLE);
                sortWindow.bringToFront();
                break;
            default: // 단어장 클릭 시
                break;
        }
    }

    public void StartSort() {
        if (nameInc.isChecked()) {
            nameDecs.setChecked(false);
            dateInc.setChecked(false);
            dateDesc.setChecked(false);
            likeInc.
        }
    }

    // 화면 스와이프를 통한 공유 단어장 액티비티로 전환

    // 검색창의 문자열을 얻어오는 함수 (검색버튼 클릭시만 호출)
    public void Search(String _str) {
        String str = _str;
        clearVocaView();
        myVocaArrayList.clear();
        myVocaArrayList = vocabularyDB.showFilterName(str);
        Log.i("MyVocabularyActiviry", "Search부분");
        for (int i = 0; i < myVocaArrayList.size(); i++) {
            int idEdit = (i + 1) * 5;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
            int _vocaid = myVocaArrayList.get(i).getVocabulary_id();
            int voca_count = myVocaArrayList.get(i).getCount();
            String vocabularyName = myVocaArrayList.get(i).getName();
            String[] relation = myVocaArrayList.get(i).languageRelation.split("/");
            String word;
            String wordMean;
            String date = myVocaArrayList.get(i).getDate();
            //혹시 길이가 0일 경우
            //근데 0일 경우 없도록 밑에서 설정해두기는 함
            if (relation.length == 0) {
                word = "";
                wordMean = "";
            } else {
                word = relation[0];
                wordMean = relation[1];
            }

            myVocaArrayList.get(i).v1 = findViewById(R.id.view);
            myVocaArrayList.get(i).v1.setId(idEdit);
            myVocaArrayList.get(i).v1.setOnLongClickListener(new View.OnLongClickListener() {
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
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                    vocaId = _vocaid;
                    Log.i("롱클릭 시 id", Integer.toString(vocaId));
                    isForRewrite = true;
                    return true;
                }
            });

            //수정
            myVocaArrayList.get(i).v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyVocabularyActivity.this, WordBookActivity.class);
                    vocaId = _vocaid;
                    intent.putExtra("단어장 data", getWordBookNameString(v.getId()) + "@" + v.getId());
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
            three.setText(date);
            TextView five = myVocaContainer.findViewById(R.id.wordCount);
            five.setId(idEdit + 4);
            five.setText(Integer.toString(voca_count));
        }
    }

    public void nonSearch() {
        clearVocaView();
        myVocaArrayList.clear();
        myVocaArrayList = vocabularyDB.showVoca();
        for (int i = 0; i < myVocaArrayList.size(); i++) {
            int idEdit = (i + 1) * 5;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
            int _vocaid = myVocaArrayList.get(i).getVocabulary_id();
            int voca_count = myVocaArrayList.get(i).getCount();
            String vocabularyName = myVocaArrayList.get(i).getName();
            String[] relation = myVocaArrayList.get(i).languageRelation.split("/");
            String word;
            String wordMean;
            String date = myVocaArrayList.get(i).getDate();
            //혹시 길이가 0일 경우
            //근데 0일 경우 없도록 밑에서 설정해두기는 함
            if (relation.length == 0) {
                word = "";
                wordMean = "";
            } else {
                word = relation[0];
                wordMean = relation[1];
            }
//            int word_count = myVocaArrayList.get(i).getLikeCount();


            myVocaArrayList.get(i).v1 = findViewById(R.id.view);
            myVocaArrayList.get(i).v1.setId(idEdit);
            myVocaArrayList.get(i).v1.setOnLongClickListener(new View.OnLongClickListener() {
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
                    backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                    backgroundView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            addViewWindow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                    vocaId = _vocaid;
                    Log.i("롱클릭 시 id", Integer.toString(vocaId));
                    isForRewrite = true;
                    return true;
                }
            });

            //수정
            myVocaArrayList.get(i).v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyVocabularyActivity.this, WordBookActivity.class);
                    vocaId = _vocaid;
                    intent.putExtra("단어장 data", getWordBookNameString(v.getId()) + "@" + v.getId());
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
            three.setText(date);
            TextView five = myVocaContainer.findViewById(R.id.wordCount);
            five.setId(idEdit + 4);
            five.setText(Integer.toString(voca_count));
        }
    }


    // 단어장 목록 아이템 inflate 및 생성된 단어장의 롱클릭 리스너 구현
    public void initMyVocabulary(String vocabularyName, String word, String wordMean) {
        int _id = vocabularyDB.showId(vocabularyName, word, wordMean);
        myVocaArrayList.addLast(new LocalWordBook(vocabularyName, word, wordMean, _id));
        int idEdit = myVocaArrayList.size() * 5;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_vocabulary_listitem, myVocaContainer, true);
        myVocaArrayList.getLast().v1 = findViewById(R.id.view);
        myVocaArrayList.getLast().v1.setId(idEdit);
        myVocaArrayList.getLast().v1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("롱클릭", Integer.toString(v.getId()));
                rewriteViewWindow.setVisibility(View.VISIBLE);
                rewriteViewWindow.bringToFront();
                backgroundView.setBackgroundColor(Color.parseColor("#85323232"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        addViewWindow.setVisibility(View.GONE);
                        return false;
                    }
                });
                idForRewrite = v.getId();
                vocaId = _id;
                wordForRewrite.setText(word);
                wordMeanForRewrite.setText(wordMean);
                vocaNameForRewrite.setText(vocabularyName);
                isForRewrite = true;
                return true;
            }
        });
        myVocaArrayList.getLast().v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyVocabularyActivity.this, WordBookActivity.class);
                vocaId = _id;
                intent.putExtra("단어장 data", getWordBookNameString(v.getId()) + "@" + v.getId());
                intent.putExtra("현재 클릭된 단어장 id", vocaId);
                Log.d("intent 클릭", "vocaId 전송");
                startActivity(intent);
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
        three.setText(myVocaArrayList.getLast().getCreateDateToString());
        TextView five = myVocaContainer.findViewById(R.id.wordCount);
        five.setId(idEdit + 4);
        five.setText(Integer.toString(myVocaArrayList.getLast().getCount()));
    }


    // 롱클릭을 통해 선택된 아이템의 삭제 메소드
    public void deleteVoca(int id, int voca_id) {

        int idx = (id / 5) - 1;
        myVocaArrayList.remove(idx);
        View delView = findViewById(id);
        myVocaContainer.removeView((View) delView.getParent());
        for (int i = id + 5; i <= myVocaArrayList.size() * 5 + 5; i += 5) {
            View v = myVocaContainer.findViewById(i);
            v.setId(i - 5);
            TextView one = myVocaContainer.findViewById(i + 1);
            one.setId(i - 4);
            TextView two = myVocaContainer.findViewById(i + 2);
            two.setId(i - 3);
            TextView the = myVocaContainer.findViewById(i + 3);
            the.setId(i - 2);
            TextView fiv = myVocaContainer.findViewById(i + 4);
            fiv.setId(i - 1);
        }
        vocabularyDB.deleteVoca(voca_id);
        Log.d("뷰 삭제", Integer.toString(id));
        rewriteViewWindow.setVisibility(View.GONE);

    }

    // 삭제 확인 창에서 단어장 목록 이름을 가져오는 메소드
    public String getWordBookNameStringForDeleteWindow() {

        TextView temp = findViewById(idForRewrite + 1);
        String str = temp.getText().toString();
        return str;

    }

    public String getWordBookNameString(int id) {
        TextView temp = findViewById(id + 1);
        String str = temp.getText().toString();
        return str;
    }


    public String getVocabularyNameForAdd() {
        String str = vocaNameForAdd.getText().toString();
        return str;
    }

    public void setLanguageBlank(boolean isS, boolean isR, String str) {
        if (isR) {
            if (isS) {
                wordForRewrite.setText(str);
            } else {
                wordMeanForRewrite.setText(str);
            }
            isSecond = !isSecond;
            languagePickerWindow.setVisibility(View.INVISIBLE);
            languagePickerScrollView.fullScroll(0);
            return;
        } else {
            if (isS) {
                wordForAdd.setText(str);
            } else {
                wordMeanForAdd.setText(str);
            }
            isSecond = !isSecond;
            languagePickerWindow.setVisibility(View.INVISIBLE);
            languagePickerScrollView.fullScroll(0);
            return;
        }
    }


    public void rewriteVocabulary(int reId, int id) {

        int idx = (reId / 5) - 1;

        myVocaArrayList.get(idx).name = vocaNameForRewrite.toString();
        myVocaArrayList.get(idx).languageRelation = wordForRewrite.toString() + "/" + wordMeanForRewrite.toString();
        TextView v1 = myVocaContainer.findViewById(reId + 1);
        TextView v2 = myVocaContainer.findViewById(reId + 2);
        Log.i("dd", v1.getText().toString());
        v1.setText(vocaNameForRewrite.getText().toString());
        v2.setText(wordForRewrite.getText().toString() + "/" + wordMeanForRewrite.getText().toString());
        rewriteViewWindow.setVisibility(View.GONE);
        String voca_name = vocaNameForRewrite.getText().toString();
        String word_lang = wordForRewrite.getText().toString();
        String mean_lang = wordMeanForRewrite.getText().toString();
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        vocabularyDB.updateVoca(id, voca_name, word_lang, mean_lang, date);

        Log.i("update", "update");
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rewriteViewWindow.getVisibility() == View.VISIBLE) {
                rewriteViewWindow.setVisibility(View.GONE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            if (addViewWindow.getVisibility() == View.VISIBLE) {
                addViewWindow.setVisibility(View.GONE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            if (deleteViewWindow.getVisibility() == View.VISIBLE) {
                deleteViewWindow.setVisibility(View.GONE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }

            if (filterSort.getVisibility() == View.VISIBLE) {
                filterSort.setVisibility(View.INVISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            if (sortWindow.getVisibility() == View.VISIBLE) {
                sortWindow.setVisibility(View.VISIBLE);
                backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
                backgroundView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void clearVocaView() {
        myVocaContainer.removeAllViews();
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
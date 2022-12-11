package com.example.android;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;

public class FirebaseDB {
    private static final String TAG = "fireStore";

    //wordbook 객체를 db에 추가
    public static String setWordBook(FirebaseFirestore db, WordBook wordBook) {
        final String[] id = {null};
        db.collection("wordbook").add(wordBook)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Success adding document");
                        id[0] = documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled adding document");

                    }
                });
        return id[0];
    }

    //이름을 업데이트
    public static boolean updateName(FirebaseFirestore db, String id, String name) {
        final boolean[] isSuccess = {false};
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("name", name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        Log.d(TAG, "Success updating name");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating name", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating name");
                    }
                });
        return isSuccess[0];
    }

    private static void addLikeId(FirebaseFirestore db, String id, String uId) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeId", FieldValue.arrayUnion(uId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success adding likeId");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding likeId", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled adding likeId");
                    }
                });
    }

    private static void removeLikeId(FirebaseFirestore db, String id, String uId) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeId", FieldValue.arrayRemove(uId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success removing likeId");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error removing likeId", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled removing likeId");
                    }
                });
    }

    //좋아요 수를 플러스
    public static boolean plusLikeCount(FirebaseFirestore db, String id, String uID) {
        final boolean[] isSuccess = {false};
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeCount", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        addLikeId(db, id, uID);
                        Log.d(TAG, "Success updating likeCount+");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating likeCount+", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating likeCount+");
                    }
                });
        return isSuccess[0];
    }

    //좋아요 수를 마이너스
    public static boolean minusLikeCount(FirebaseFirestore db, String id, String uID) {
        final boolean[] isSuccess = {false};
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeCount", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        removeLikeId(db, id, uID);
                        Log.d(TAG, "Success updating likeCount-");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating likeCount-", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating likeCount-");
                    }
                });
        return isSuccess[0];
    }

    //단어언어 업데이트
    public static boolean updateMeanLang(FirebaseFirestore db, String id, String lang) {
        final boolean[] isSuccess = {false};
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("meanLang", lang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        Log.d(TAG, "Success updating meanLang");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating meanLang", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating meanLang");
                    }
                });
        return isSuccess[0];
    }

    //의미언어 업데이트
    public static boolean updateWordLang(FirebaseFirestore db, String id, String lang) {
        final boolean[] isSuccess = {false};
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("wordLang", lang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        Log.d(TAG, "Success updating wordLang");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating wordLang", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating wordLang");
                    }
                });
        return isSuccess[0];
    }

    private static void plusWordCount(FirebaseFirestore db, String id) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("wordCount", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating wordCount+");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating wordCount+", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating wordCount+");
                    }
                });
    }

    private static void minusWordCount(FirebaseFirestore db, String id) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("wordCount", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating wordCount-");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating wordCount-", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled updating wordCount-");
                    }
                });
    }

    //단어 추가
    public static boolean addWord(FirebaseFirestore db, String id, String word, String mean, String uId) {
        final boolean[] isSuccess = {false};
        Word wordClass = new Word(word, mean, uId);
        db.collection("wordbook").document(id).collection("word").add(wordClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        plusWordCount(db, id);
                        isSuccess[0] = true;
                        Log.d(TAG, "Success adding word");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding word", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled adding word");
                    }
                });
        return isSuccess[0];
    }

    //단어 업데이트, 둘중 하나만 하고 싶으면 수정 안 할거 null
    public static boolean[] updateWord(FirebaseFirestore db, String wordBookId, String wordId, String word, String mean) {
        final boolean[] isSuccess = {false, false};
        DocumentReference wordbook = db.collection("wordbook").document(wordBookId).collection("word").document(wordId);
        if (word != null) {
            wordbook.update("word", word)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            isSuccess[0] = true;
                            Log.d(TAG, "Success updating word");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating word", e);
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.w(TAG, "Canceled updating word");
                        }
                    });
        }
        if (mean != null) {
            wordbook.update("mean", mean)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            isSuccess[1] = true;
                            Log.d(TAG, "Success updating mean");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating mean", e);
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.w(TAG, "Canceled updating mean");
                        }
                    });
        }
        return isSuccess;
    }

    //단어장 삭제
    public static boolean deleteWordBook(FirebaseFirestore db, String id) {
        final boolean[] isSuccess = {false};
        db.collection("wordbook").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        Log.d(TAG, "Success deleting document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled deleting document");
                    }
                });
        return isSuccess[0];
    }

    //단어 삭제
    public static boolean deleteWord(FirebaseFirestore db, String id, String wordId) {
        final boolean[] isSuccess = {false};
        db.collection("wordbook").document(id).collection("word").document(wordId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        isSuccess[0] = true;
                        Log.d(TAG, "Success deleting document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled deleting document");
                    }
                });
        return isSuccess[0];
    }

    public static WordBook getWordBookById(FirebaseFirestore db, String id) {
        DocumentReference docRef = db.collection("wordbook").document(id);
        final WordBook[] wordBook = new WordBook[1];
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        wordBook[0] = documentSnapshot.toObject(WordBook.class);
                        Log.d(TAG, "Success getting document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled getting document");
                    }
                });
        return wordBook[0];
    }

    public static ArrayList<WordBook> getWordBookList(FirebaseFirestore db, int sortNum, String wordLang, String meanLang, boolean like, String uId) {
        LinkedList<WordBook> temp = new LinkedList<>();
        CollectionReference wordBookRef = db.collection("wordbook");
        Query wordBookQuery = wordBookRef;
        switch (sortNum) {
            case 0:
                break;
            case 1:
                wordBookQuery = wordBookRef.orderBy("name", Query.Direction.ASCENDING);
                break;
            case 2:
                wordBookQuery = wordBookRef.orderBy("name", Query.Direction.DESCENDING);
                break;
            case 3:
                wordBookQuery = wordBookRef.orderBy("createDate", Query.Direction.ASCENDING);
                break;
            case 4:
                wordBookQuery = wordBookRef.orderBy("createDate", Query.Direction.DESCENDING);
                break;
            case 5:
                wordBookQuery = wordBookRef.orderBy("likeCount", Query.Direction.ASCENDING);
                break;
            case 6:
                wordBookQuery = wordBookRef.orderBy("likeCount", Query.Direction.DESCENDING);
                break;
        }
        if (wordLang != null) {
            wordBookQuery = wordBookQuery.whereEqualTo("wordLang", wordLang);
        }
        if (meanLang != null) {
            wordBookQuery = wordBookQuery.whereEqualTo("meanLang", meanLang);
        }
        if (like) {
            wordBookQuery = wordBookQuery.whereArrayContains("likeId", uId);
        }
        wordBookQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                temp.addLast(documentSnapshot.toObject(WordBook.class));
                            }
                        }
                        Log.d(TAG, "Success getting Task");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting Task", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled getting Task");
                    }
                });
        return new ArrayList<>(temp);
    }

    public static ArrayList<Word> getWordList(FirebaseFirestore db, int sortNum, String id) {
        LinkedList<Word> temp = new LinkedList<>();
        CollectionReference wordRef = db.collection("wordbook").document(id).collection("word");
        Query wordQuery = wordRef;
        switch (sortNum) {
            case 0:
                break;
            case 1:
                wordQuery = wordRef.orderBy("name", Query.Direction.ASCENDING);
                break;
            case 2:
                wordQuery = wordRef.orderBy("name", Query.Direction.DESCENDING);
                break;
        }
        wordQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                temp.addLast(documentSnapshot.toObject(Word.class));
                            }
                        }
                        Log.d(TAG, "Success getting Task");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting Task", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Canceled getting Task");
                    }
                });
        return new ArrayList<>(temp);
    }
}
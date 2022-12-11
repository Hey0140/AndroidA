package com.example.android;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.LinkedList;

public class FirebaseDB {
    private static final String TAG = "fireStore";

    //wordbook 객체를 db에 추가
    public static void setWordBook(FirebaseFirestore db, WordBook wordBook, Thread thread, String[] id) {
        db.collection("wordbook").add(wordBook)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Success adding document : " + documentReference.getId());
                        id[0] = documentReference.getId();
                        if (thread != null) thread.start();
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
    }

    //이름을 업데이트
    public static void updateName(FirebaseFirestore db, String id, String name, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("name", name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating name");
                        if (thread != null) thread.start();
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
    public static void plusLikeCount(FirebaseFirestore db, String id, String uID, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeCount", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addLikeId(db, id, uID);
                        Log.d(TAG, "Success updating likeCount+");
                        if (thread != null) thread.start();
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
    }

    //좋아요 수를 마이너스
    public static void minusLikeCount(FirebaseFirestore db, String id, String uID, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("likeCount", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        removeLikeId(db, id, uID);
                        Log.d(TAG, "Success updating likeCount-");
                        if (thread != null) thread.start();
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
    }

    //단어언어 업데이트
    public static void updateMeanLang(FirebaseFirestore db, String id, String lang, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("meanLang", lang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating meanLang");
                        if (thread != null) thread.start();
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
    }

    //의미언어 업데이트
    public static void updateWordLang(FirebaseFirestore db, String id, String lang, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(id);
        wordbook.update("wordLang", lang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating wordLang");
                        if (thread != null) thread.start();
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
    public static void addWord(FirebaseFirestore db, String id, String word, String mean, String uId, Thread thread) {
        Word wordClass = new Word(word, mean, uId);
        db.collection("wordbook").document(id).collection("word").add(wordClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        plusWordCount(db, id);
                        Log.d(TAG, "Success adding word");
                        if (thread != null) thread.start();
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
    }

    //단어 업데이트, 둘중 하나만 하고 싶으면 수정 안 할거 null
    public static void updateWord(FirebaseFirestore db, String wordBookId, String wordId, @NonNull String word, @NonNull String mean, Thread thread) {
        DocumentReference wordbook = db.collection("wordbook").document(wordBookId).collection("word").document(wordId);
        db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(wordbook);
                        transaction.update(wordbook, "word", word);
                        transaction.update(wordbook, "mean", mean);
                        return null;
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success updating word");
                        if (thread != null) thread.start();
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

    //단어장 삭제
    public static void deleteWordBook(FirebaseFirestore db, String id, Thread thread) {
        db.collection("wordbook").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success deleting document");
                        if (thread != null) thread.start();
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
    }

    //단어 삭제
    public static void deleteWord(FirebaseFirestore db, String id, String wordId, Thread thread) {
        db.collection("wordbook").document(id).collection("word").document(wordId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success deleting document");
                        if (thread != null) thread.start();
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
    }

    public static void getWordBookById(FirebaseFirestore db, String id, Thread thread, WordBook[] wordBook) {
        DocumentReference docRef = db.collection("wordbook").document(id);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        wordBook[0] = documentSnapshot.toObject(WordBook.class);
                        Log.d(TAG, "Success getting document");
                        if (thread != null) thread.start();
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
    }

    public static void getWordBookList(FirebaseFirestore db, int sortNum, String wordLang, String meanLang, boolean like, String uId, Thread thread, ArrayList<WordBook>[] arrayList) {
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
                        arrayList[0] = new ArrayList<>(temp);
                        Log.d(TAG, "Success getting Task");
                        if (thread != null) thread.start();
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
    }

    public static void getWordList(FirebaseFirestore db, int sortNum, String id, Thread thread, ArrayList<Word>[] arrayList) {
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
                        arrayList[0] = new ArrayList<>(temp);
                        Log.d(TAG, "Success getting Task");
                        if (thread != null) thread.start();
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
    }
}
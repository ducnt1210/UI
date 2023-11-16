package com.example.ui.Helper;

import android.util.Log;

import com.example.ui.Model.NewsModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class NewsHelper {
    private static final String COLLECTION_NAME = "News";
    private final FirebaseFirestore db;
    private final CollectionReference newsCollection;

    public NewsHelper() {
            db = FirebaseFirestore.getInstance();
        newsCollection = db.collection(COLLECTION_NAME);

        checkIfCollectionExists();
    }

    private void checkIfCollectionExists() {
        newsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d("NewsHelper", "Collection does not exist.");
                        } else {
                            Log.d("NewsHelper", "Collection exists.");
                        }
                    } else {
                        Log.e("NewsHelper", "Error checking collection existence: ", task.getException());
                    }
                });
    }

    public void addNews(NewsModel news) {
        newsCollection.add(news);
    }

    public void updateNews(String newsId, NewsModel updatedNews) {
        DocumentReference newsRef = newsCollection.document(newsId);
        newsRef.set(updatedNews);
    }

    public void deleteNews(String newsId) {
        DocumentReference newsRef = newsCollection.document(newsId);
        newsRef.delete();
    }

    public DocumentReference getNewsById(String newsId) {
        return newsCollection.document(newsId);
    }

    public void fetchLatestNews(NewsDataCallback callback) {
        newsCollection.orderBy("time", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println(task.getResult().getDocuments());
                        callback.onDataLoaded(task.getResult().getDocuments());
                    } else {
                        System.out.println("Đã có lỗi");
                    }
                });
    }

    public interface NewsDataCallback {
        void onDataLoaded(List<DocumentSnapshot> newsList);
    }
}

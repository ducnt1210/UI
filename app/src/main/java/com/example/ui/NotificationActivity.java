package com.example.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.NotificationAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textViewTime;
    private NotificationAdapter notificationAdapter;
    private SweetAlertDialog sweetAlertDialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        textViewTime = findViewById(R.id.textViewTime);

        Bundle bundle = getIntent().getExtras();

        String time = (String) bundle.get("time");
        textViewTime.setText(time);
        recyclerView = findViewById(R.id.recyclerViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(RecyclerView.DRAWING_CACHE_QUALITY_HIGH);
        if (bundle != null) {
            String docId = (String) bundle.get("id");

            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Notification").document(docId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> description = (List<String>) documentSnapshot.get("description");

                    if (description != null && !description.isEmpty()) {
                        List<String> descriptionList = new ArrayList<>();
                        descriptionList.addAll(description);
                        notificationAdapter = new NotificationAdapter(descriptionList);
                        recyclerView.setAdapter(notificationAdapter);
                    }
                } else {
                    Log.d("NotificationActivity", "No such document");
                }
            }).addOnFailureListener(e -> Log.d("NotificationActivity", "get failed with ", e));


        }


        sweetAlertDialog.dismissWithAnimation();
    }
}
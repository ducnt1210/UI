package com.example.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.NotificationAdapter;
import com.example.ui.Model.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    private boolean update = false;

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
        if (bundle != null) {
            update = bundle.getBoolean("update");
            if (update) {
//                this.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//                    @Override
//                    public void handleOnBackPressed() {
//                        if (isEnabled()) {
//                            setEnabled(false);
////                            NotificationActivity.this.onBackPressed();
//                            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
//                            intent.putExtra("FragmentID", "NotificationFragment");
//                            startActivity(intent);
//                        }
//                    }
//                });

                String id = bundle.getString("id");
                updateNotification(id);
            }
            List<String> description = (List<String>) bundle.get("description");

//            Log.d("description", Integer.toString(description.size()));
//            for (int i = 0; i < description.size(); ++i) {
//                Log.d("des" + Integer.toString(i), description.get(i));
//            }
            String time = (String) bundle.get("time");
            textViewTime.setText(time);
            notificationAdapter = new NotificationAdapter(description);
        } else {
            notificationAdapter = new NotificationAdapter(new ArrayList<>());
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(RecyclerView.DRAWING_CACHE_QUALITY_HIGH);

        recyclerView.setAdapter(notificationAdapter);

        sweetAlertDialog.dismissWithAnimation();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    private void updateNotification(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Notification").document(id);

        reference.get().addOnSuccessListener(documentSnapshot -> {
            NotificationModel notificationModel = new NotificationModel(
                    documentSnapshot.getId(),
                    documentSnapshot.getString("image_path"),
                    (List<String>) documentSnapshot.get("description"),
                    documentSnapshot.getString("user_id"),
//                    documentSnapshot.getBoolean("seen"),
                    true,
                    documentSnapshot.getBoolean("sentNotification"),
                    documentSnapshot.getTimestamp("time")
            );
            reference.set(notificationModel).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Update notification failed", id);
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
//        if (update) {
//            update = false;
////            MainActivity.FragmentID = "NotificationFragment";
//            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
//            intent.putExtra("FragmentID", "NotificationFragment");
//            startActivity(intent);
//        } else {
//            super.onBackPressed();
//        }
        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
        intent.putExtra("FragmentID", "NotificationFragment");
        startActivity(intent);
    }
}
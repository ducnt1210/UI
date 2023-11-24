package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ui.Adapter.NewsEventsAdapter;
import com.example.ui.Adapter.NotificationAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewsEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textViewTime;
    private NewsEventsAdapter newsEventsAdapter;
    private SweetAlertDialog sweetAlertDialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_events);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        textViewTime = findViewById(R.id.textViewTime);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            List<String> description = (List<String>) bundle.get("description");

//            Log.d("description", Integer.toString(description.size()));
//            for (int i = 0; i < description.size(); ++i) {
//                Log.d("des" + Integer.toString(i), description.get(i));
//            }
            String time = (String) bundle.get("time");
            textViewTime.setText(time);
            newsEventsAdapter = new NewsEventsAdapter(description);
        } else {
            newsEventsAdapter = new NewsEventsAdapter(new ArrayList<>());
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNewsEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(RecyclerView.DRAWING_CACHE_QUALITY_HIGH);

        recyclerView.setAdapter(newsEventsAdapter);

        sweetAlertDialog.dismissWithAnimation();
    }
}
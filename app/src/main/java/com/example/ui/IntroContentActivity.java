package com.example.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.IntroContentAdapter;
import com.example.ui.databinding.ActivityIntroContentBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class IntroContentActivity extends AppCompatActivity {

    private static final String VIDEO_POSITION_KEY = "video_position";
    FirebaseFirestore firebaseFirestore;
    ActivityIntroContentBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference introContentRef = db.collection("IntroContent");
    private RecyclerView recyclerView;
    private IntroContentAdapter introContentAdapter;
    private int savedVideoPosition = 0;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setIntroContentView();

        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.rv_intro_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(RecyclerView.DRAWING_CACHE_QUALITY_HIGH);

        videoView = findViewById(R.id.intro_video);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
            // If you have saved video position, start playing the video from the saved position
            if (savedVideoPosition > 0) {
                mp.seekTo(savedVideoPosition);
            }
        });

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.demo_vid_cut);
        videoView.setVideoURI(videoUri);

    }

    protected void showContent(String document) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("IntroContent").document(document);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> dataIntro = (List<String>) documentSnapshot.get("content");

                if (dataIntro != null && !dataIntro.isEmpty()) {
                    List<String> introContentList = new ArrayList<>();
                    introContentList.addAll(dataIntro);

                    introContentAdapter = new IntroContentAdapter(introContentList);
                    recyclerView.setAdapter(introContentAdapter);
                } else {
                    Toast.makeText(this, "No documents found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No documents found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "The server is experiencing an error. Please come back later", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Start playing the video when the Activity is visible
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the current position of the video when the Activity is paused
        savedVideoPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    // Save the video state when the Activity is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(VIDEO_POSITION_KEY, savedVideoPosition);
    }

    // Restore the video state when the Activity is recreated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedVideoPosition = savedInstanceState.getInt(VIDEO_POSITION_KEY);
    }

    protected void setIntroContentView() {
        String heading = getIntent().getStringExtra("heading");
        if (heading.equals("Chienluoc")) {
            binding.introTextHeading.setText("Chiến lược bảo tàng");
            showContent("Chienluoc");
        } else if (heading.equals("Lichsu")) {
            binding.introTextHeading.setText("Lịch sử bảo tàng");
            showContent("Lichsu");
        } else if (heading.equals("Khonggian")) {
            binding.introTextHeading.setText("Các không gian");
            showContent("Khonggian");
        } else if (heading.equals("Nhansu")) {
            binding.introTextHeading.setText("Tổ chức và nhân sự");
            showContent("Nhansu");
        } else if (heading.equals("Hoptac")) {
            binding.introTextHeading.setText("Hợp tác");
            showContent("Hoptac");
        } else {
            binding.introTextHeading.setText("Giới thiệu");
            showContent("Tongquan");
        }
    }
}

package com.example.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.ExhibitDetailsAdapter;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.Quiz.QuizActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ShowExhibitActivity extends AppCompatActivity {

    private TextView artifactNameTextView;
    private PlayerView playerView;
    private RecyclerView recyclerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_exhibit);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.quiz_header);
        ImageView img = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.quiz);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowExhibitActivity.this, QuizActivity.class));
            }
        });

        // Lấy tham chiếu đến các thành phần trong XML
        artifactNameTextView = findViewById(R.id.artifactName);
        playerView = findViewById(R.id.playerView);
        recyclerView = findViewById(R.id.recyclerView);

        // Lấy exhibitModel từ Intent
        ExhibitModel exhibitModel = (ExhibitModel) getIntent().getSerializableExtra("exhibit");

        // Hiển thị thông tin từ exhibitModel lên các thành phần
        if (exhibitModel != null) {
            // Hiển thị video
            String videoPath = exhibitModel.getVideo();
            if (videoPath != null && !videoPath.isEmpty()) {
                initializePlayer(); // Thêm phương thức khởi tạo player
                StorageReference videoRef = FirebaseStorage.getInstance().getReference().child("video/" + videoPath);

                videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            // Uri tải xuống thành công, thiết lập cho ExoPlayer
                            initializePlayer();
                            preparePlayer(uri);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi không thể tải xuống video
                        Log.e("Firebase Storage", "Error downloading video: " + e.getMessage());
                    }
                });
            }

            // Hiển thị nội dung chi tiết bằng cách sử dụng RecyclerView
            List<String> exhibitDetails = exhibitModel.getContent();
            if (exhibitDetails != null && !exhibitDetails.isEmpty()) {
                // Sử dụng LinearLayoutManager cho RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(ShowExhibitActivity.this);
                recyclerView.setLayoutManager(layoutManager);

                // Tạo adapter và đặt adapter cho RecyclerView
                ExhibitDetailsAdapter detailsAdapter = new ExhibitDetailsAdapter(exhibitDetails);
                recyclerView.setAdapter(detailsAdapter);
            }

            // Hiển thị tên hiện vật
            artifactNameTextView.setText(exhibitModel.getName());
        }
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });

    }

    private void preparePlayer(Uri uri) {
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer(); // Thêm phương thức giải phóng player
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }
}

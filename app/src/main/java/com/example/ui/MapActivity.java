package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ScaleGestureDetector;

import com.example.ui.databinding.ActivityMapBinding;


public class MapActivity extends AppCompatActivity {
    ActivityMapBinding binding;
    ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float lastX, lastY;
    private boolean isPanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        binding.button1.setOnClickListener(view -> {
            String url = "https://courses.uet.vnu.edu.vn/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        binding.mapHeader.headerText.setText("Sơ đồ các phòng");
        binding.mapHeader.headerIcon.setImageResource(R.drawable.white_map);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                isPanning = false;
                break;
            case android.view.MotionEvent.ACTION_MOVE:
                if (scaleFactor > 1.0f) {
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;
                    if (!isPanning && (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10)) {
                        isPanning = true;
                    }

                    if (isPanning) {
                        binding.mapLayout.setTranslationX(binding.mapLayout.getTranslationX() + deltaX);
                        binding.mapLayout.setTranslationY(binding.mapLayout.getTranslationY() + deltaY);
                    }


                    lastX = x;
                    lastY = y;
                }
                break;
            case android.view.MotionEvent.ACTION_UP:
                isPanning = false;
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float previousScaleFactor = scaleFactor;
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f)); // Giới hạn tỷ lệ phóng to/thu nhỏ
            if (previousScaleFactor != 1.0f && scaleFactor == 1.0f) {
                binding.mapLayout.setTranslationX(0);
                binding.mapLayout.setTranslationY(0);
            }
            binding.mapLayout.setScaleX(scaleFactor);
            binding.mapLayout.setScaleY(scaleFactor);
            return true;
        }
    }
}
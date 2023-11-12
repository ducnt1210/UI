package com.example.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.databinding.ActivityEditInfoBinding;

public class EditInfoActivity extends AppCompatActivity {
    ActivityEditInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditInfoBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
    }
}
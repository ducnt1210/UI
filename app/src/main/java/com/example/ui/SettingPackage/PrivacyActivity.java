package com.example.ui.SettingPackage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends AppCompatActivity {
    ActivityPrivacyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
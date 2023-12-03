package com.example.ui.SettingPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ui.R;
import com.example.ui.databinding.ActivityInstructionItemContentBinding;

public class InstructionItemContentActivity extends AppCompatActivity {
    ActivityInstructionItemContentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionItemContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.instruction);
    }
}
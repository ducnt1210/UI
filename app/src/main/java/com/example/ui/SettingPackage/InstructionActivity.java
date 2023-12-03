package com.example.ui.SettingPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.R;
import com.example.ui.databinding.ActivityInstructionBinding;

public class InstructionActivity extends AppCompatActivity {
    ActivityInstructionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.instruction);
        itemConstructor();
    }

    private void itemConstructor() {
        binding.qrInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "qr");
                startActivity(intent);
            }
        });
        binding.artifactInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "artifact");
                startActivity(intent);
            }
        });
        binding.mapInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "map");
                startActivity(intent);
            }
        });
        binding.editProfileInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "editProfile");
                startActivity(intent);
            }
        });
        binding.languageInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "language");
                startActivity(intent);
            }
        });
        binding.newsneventsInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "newsnevents");
                startActivity(intent);
            }
        });
        binding.notificationInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "notification");
                startActivity(intent);
            }
        });
        binding.contactAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "contactAdmin");
                startActivity(intent);
            }
        });
        binding.deleteAccountInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "deleteAccount");
                startActivity(intent);
            }
        });

    }
}
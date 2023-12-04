package com.example.ui.SettingPackage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String instruction = (String) bundle.get("instruction");
            if (instruction.equals("qr")) {
                binding.textViewInstructionTitle.setText(R.string.qr_title);
                binding.textContent1.setText(R.string.qr_content1);
                binding.rl1Image1.setImageResource(R.drawable.qr_ins);
                binding.rl1Image2.setImageResource(R.drawable.qr_ins2);
                binding.textContent2.setText(R.string.qr_content2);
                binding.textContent2.setVisibility(View.VISIBLE);
            } else if (instruction.equals("artifact")) {
                binding.textViewInstructionTitle.setText(R.string.artifact_title);
                binding.textContent1.setText(R.string.artifact_content1);
                binding.textContent2.setText(R.string.artifact_content2);
                binding.textContent2.setVisibility(View.VISIBLE);
            } else if (instruction.equals("map")) {
                binding.textViewInstructionTitle.setText(R.string.map_title);
                binding.textContent1.setText(R.string.map_content1);
            } else if (instruction.equals("editProfile")) {
                binding.textViewInstructionTitle.setText(R.string.edit_profile_title);
                binding.textContent1.setText(R.string.edit_profile_content1);
                binding.textContent2.setText(R.string.edit_profile_content2);
                binding.textContent2.setVisibility(View.VISIBLE);
            }
        }
    }
}
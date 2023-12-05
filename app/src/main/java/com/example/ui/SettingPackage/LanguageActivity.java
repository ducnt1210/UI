package com.example.ui.SettingPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ui.MainActivity;
import com.example.ui.NavigationOpeningActivity;
import com.example.ui.databinding.ActivityLanguageBinding;
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;
import com.zeugmasolutions.localehelper.Locales;

import java.util.Locale;

public class LanguageActivity extends LocaleAwareCompatActivity {
    ActivityLanguageBinding binding;
    public static String current_language = "English";
    public static String english = "English";
    public static String vietnamese = "Vietnamese";
    public static String japanese = "Japanese";
    public static String chinese = "Chinese";
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String checkUser = getIntent().getStringExtra("checkUser");
        if (checkUser != null && checkUser.contains("newUser")) {
            binding.openingTextView.setVisibility(View.VISIBLE);
        } else binding.openingTextView.setVisibility(View.GONE);

        binding.setLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.setLanguageButton.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.setLanguageButton.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                }).start();
                setLanguageApp();
                if (checkUser != null && checkUser.contains("newUser")) {
                    Intent intent = new Intent(LanguageActivity.this, NavigationOpeningActivity.class);
                    binding.openingTextView.setVisibility(View.VISIBLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("checkUser", "newUser");
                    startActivity(intent);
                } else {
                    binding.openingTextView.setVisibility(View.GONE);
                    startActivity(new Intent(LanguageActivity.this, MainActivity.class).putExtra("FragmentID", "SettingFragment"));
                }
            }
        });

        locale = getResources().getConfiguration().locale;

        if (locale.getLanguage().equals(new Locale("en").getLanguage())) {
            current_language = english;
        } else if (locale.getLanguage().equals(new Locale("vi").getLanguage())) {
            current_language = vietnamese;
        } else if (locale.getLanguage().equals(new Locale("jp").getLanguage())) {
            current_language = japanese;
        } else if (locale.getLanguage().equals(new Locale("zh").getLanguage())) {
            current_language = chinese;
        }


        if (current_language.equals(vietnamese)) {
            binding.VNRadio.setChecked(true);
        } else if (current_language.equals(english)) {
            binding.ENRadio.setChecked(true);
        } else if (current_language.equals(japanese)) {
            binding.JPRadio.setChecked(true);
        } else if (current_language.equals(chinese)) {
            binding.CNRadio.setChecked(true);
        }
    }

    private void setLanguageApp() {
//        locale = Locales.INSTANCE.getEnglish();
        if (binding.VNRadio.isChecked()) {
            current_language = vietnamese;
            locale = Locales.INSTANCE.getVietnamese();
        } else if (binding.ENRadio.isChecked()) {
            current_language = english;
            locale = Locales.INSTANCE.getEnglish();
        } else if (binding.JPRadio.isChecked()) {
            current_language = japanese;
            locale = Locales.INSTANCE.getJapanese();
        } else if (binding.CNRadio.isChecked()) {
            current_language = chinese;
            locale = Locale.CHINA;
        }
        this.updateLocale(locale);
    }

}
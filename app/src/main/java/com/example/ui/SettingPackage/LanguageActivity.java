package com.example.ui.SettingPackage;

import android.os.Bundle;
import android.view.View;

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

        binding.setLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguageApp();
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
        } else {
            current_language = english;
            locale = Locales.INSTANCE.getEnglish();
        }
        this.updateLocale(locale);
    }

}
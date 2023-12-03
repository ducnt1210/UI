package com.example.ui.Quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ui.R;
import com.example.ui.databinding.ActivityExchangeGiftBinding;

public class ExchangeGiftActivity extends AppCompatActivity {
    ActivityExchangeGiftBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExchangeGiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.coin_layout);
        getSupportActionBar().getCustomView().findViewById(R.id.exchange_gift_title).setVisibility(android.view.View.VISIBLE);
    }
}
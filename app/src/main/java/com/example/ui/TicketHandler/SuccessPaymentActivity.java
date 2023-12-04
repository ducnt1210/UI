package com.example.ui.TicketHandler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.MainActivity;
import com.example.ui.databinding.ActivitySuccessPaymentBinding;

public class SuccessPaymentActivity extends AppCompatActivity {
    ActivitySuccessPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuccessPaymentBinding.inflate(getLayoutInflater());
        String amount = getIntent().getStringExtra("amount");
        String prefix1 = "Amount: ";
        Spannable wordToSpan = new SpannableString(prefix1 + amount + " VND");
        wordToSpan.setSpan(new ForegroundColorSpan(Color.BLUE), prefix1.length(), wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.amount.setText(wordToSpan);

        String transactionID = getIntent().getStringExtra("transactionID");
        String prefix2 = "Transaction ID: ";
        Spannable wordToSpan2 = new SpannableString(prefix2 + transactionID);
        wordToSpan2.setSpan(new ForegroundColorSpan(Color.BLUE), prefix2.length(), wordToSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.transactionID.setText(wordToSpan2);

        String s3 = "Paid with ZaloPay";
        Spannable wordToSpan3 = new SpannableString(s3);
        wordToSpan3.setSpan(new ForegroundColorSpan(Color.BLUE), 10, s3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.zaloPay.setText(wordToSpan3);

        binding.homeScreenButton.setOnClickListener(v -> {
            startActivity(new Intent(SuccessPaymentActivity.this, MainActivity.class));
            finishAffinity();
        });

        setContentView(binding.getRoot());
    }
}
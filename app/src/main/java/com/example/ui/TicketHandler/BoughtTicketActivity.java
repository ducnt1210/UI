package com.example.ui.TicketHandler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Adapter.TicketViewPagerAdapter;
import com.example.ui.R;
import com.example.ui.databinding.ActivityBoughtTicketBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class BoughtTicketActivity extends AppCompatActivity {
    ActivityBoughtTicketBinding binding;
    TicketViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoughtTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPagerAdapter = new TicketViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(viewPagerAdapter);

        setTitle(R.string.booked_tickets);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.unused_ticket);
                            break;
                        case 1:
                            tab.setText(R.string.used_ticket);
                            break;
                    }
                }
        ).attach();

    }
}
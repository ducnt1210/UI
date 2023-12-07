package com.example.ui.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ui.Adapter.GiftViewPagerAdapter;
import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.R;
import com.example.ui.databinding.ActivityGiftBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class GiftActivity extends AppCompatActivity {

    private ActivityGiftBinding binding;
    GiftViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPagerAdapter = new GiftViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(viewPagerAdapter);

        // Get the intent used to start this activity
        Intent intent = getIntent();

        // Check if there's an extra value named "selectedTab" in the intent
        if (intent != null && intent.hasExtra("selectedTab")) {
            int selectedTab = intent.getIntExtra("selectedTab", 0); // Default tab index is 0

            binding.viewPager.setCurrentItem(selectedTab, true);
//            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedTab));
//        }
//
//            TabLayout.Tab tab = binding.tabLayout.getTabAt(selectedTab);
//            if (tab != null) {
//                switch (selectedTab) {
//                    case 0:
//                        tab.setText("Đổi quà");
//                        break;
//                    case 1:
//                        tab.setText("Quà đã đổi");
//                        break;
//                }
//            }
        }


        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.giftTab0);
                            break;
                        case 1:
                            tab.setText(R.string.giftTab1);
                            break;
                    }
                }
        ).attach();

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.coin_layout);
        TextView coin = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.coin);
        coin.setText(Integer.toString(HomeFragment.scoreModel.getScore()));
        getSupportActionBar().getCustomView().findViewById(R.id.exchange_gift_title).setVisibility(android.view.View.VISIBLE);
    }

}
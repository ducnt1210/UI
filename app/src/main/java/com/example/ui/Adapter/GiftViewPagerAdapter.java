package com.example.ui.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ui.Quiz.ui.exchange.ExchangeFragment;
import com.example.ui.Quiz.ui.review.ReviewFragment;

public class GiftViewPagerAdapter extends FragmentStateAdapter {
    public GiftViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ExchangeFragment();
        }
        return new ReviewFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

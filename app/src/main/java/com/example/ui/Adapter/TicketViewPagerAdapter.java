package com.example.ui.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ui.TicketHandler.UnusedTicketFragment;
import com.example.ui.TicketHandler.UsedTicketFragment;

public class TicketViewPagerAdapter extends FragmentStateAdapter {
    public TicketViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UnusedTicketFragment();
            default:
                return new UsedTicketFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

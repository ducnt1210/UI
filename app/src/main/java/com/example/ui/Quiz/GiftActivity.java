package com.example.ui.Quiz;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ui.databinding.ActivityGiftBinding;

public class GiftActivity extends AppCompatActivity {

    private ActivityGiftBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_gift);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("exchanged")) {
            navController.setGraph(R.navigation.mobile_navigation_exchanged);
        }

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.coin_layout);
        TextView coin = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.coin);
        coin.setText(Integer.toString(HomeFragment.scoreModel.getScore()));
        getSupportActionBar().getCustomView().findViewById(R.id.exchange_gift_title).setVisibility(android.view.View.VISIBLE);
    }

}
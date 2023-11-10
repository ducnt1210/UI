package com.example.ui.MainActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.ui.IntroContentActivity;
import com.example.ui.MapActivity;
import com.example.ui.R;
import com.example.ui.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(rootView);
        getIntroContentView();

        return rootView;
    }

    protected void getIntroContentView() {
        binding.homeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        binding.homeIntroMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "");
                startActivity(intent);
            }
        });
        binding.introCardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Chienluoc");
                startActivity(intent);
            }
        });
        binding.introCardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Lichsu");
                startActivity(intent);
            }
        });
        binding.introCardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Khonggian");
                startActivity(intent);
            }
        });
        binding.introCardview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Nhansu");
                startActivity(intent);
            }
        });
        binding.introCardview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IntroContentActivity.class);
                intent.putExtra("heading", "Hoptac");
                startActivity(intent);
            }
        });

    }

}

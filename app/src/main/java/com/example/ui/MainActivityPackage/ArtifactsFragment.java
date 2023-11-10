package com.example.ui.MainActivityPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.ui.R;
import com.example.ui.databinding.FragmentArtifactsBinding;

public class ArtifactsFragment extends Fragment {

    FragmentArtifactsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentArtifactsBinding.inflate(inflater,container,false);


        binding.artifactsHeader.headerText.setText("Danh mục hiện vật");
        binding.artifactsHeader.headerIcon.setImageResource(R.drawable.white_list);
        return binding.getRoot();
    }
}
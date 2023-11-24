package com.example.ui.MainActivityPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.ui.Adapter.AreaAdapter;
import com.example.ui.MainActivity;
import com.example.ui.databinding.FragmentArtifactsBinding;

public class ArtifactsFragment extends Fragment {

    private FragmentArtifactsBinding binding;
    private ExpandableListView expandableListView;
    private AreaAdapter areaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArtifactsBinding.inflate(inflater, container, false);
        expandableListView = binding.elvArtifact;

        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(R.string.artifact_category);

        // Initialize and set the adapter
        System.out.println(1234);
        areaAdapter = new AreaAdapter(getContext());
        expandableListView.setAdapter(areaAdapter);

        return binding.getRoot();
    }
}

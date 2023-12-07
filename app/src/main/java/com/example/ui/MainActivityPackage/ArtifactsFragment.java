package com.example.ui.MainActivityPackage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ViewSwitcher;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapter.AreaAdapter;
import com.example.ui.Adapter.SearchAdapter;
import com.example.ui.MainActivity;
import com.example.ui.Model.ExhibitModel;
import com.example.ui.Model.LocalAreaModel;
import com.example.ui.R;
import com.example.ui.databinding.FragmentArtifactsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class ArtifactsFragment extends Fragment {

    private FragmentArtifactsBinding binding;
    private ExpandableListView expandableListView;
    private RecyclerView recyclerView;
    private AreaAdapter areaAdapter;
    private SearchAdapter searchAdapter;
    private EditText searchBar;
    private ViewSwitcher viewSwitcher;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<ExhibitModel> allExhibits;

    private Stack<String> searchHistory = new Stack<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        allExhibits = new ArrayList<>();
        getAllExhibits();
        binding = FragmentArtifactsBinding.inflate(inflater, container, false);
        expandableListView = binding.elvArtifact;
        recyclerView = binding.artifact;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        viewSwitcher = binding.viewSwitcher;


        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(R.string.artifact_category);

        // Initialize and set the adapter
        areaAdapter = new AreaAdapter(getContext());
        expandableListView.setAdapter(areaAdapter);



        searchBar = binding.searchBar;

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHistory.push(String.valueOf(searchBar.getText()));
                // Chuyển đổi giữa ExpandableListView và RecyclerView khi thực hiện tìm kiếm
                searchAdapter.getFilter().filter(searchBar.getText());
                hideKeyboard();
                toggleViews();
                return true;
            }
            return false;
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!searchHistory.isEmpty()) {
                    // Nếu còn mục trong lịch sử, lấy ra và hiển thị
                    String lastSearch = searchHistory.pop();
                    searchBar.setText(lastSearch);
                    searchAdapter.getFilter().filter(lastSearch);
                    toggleViews();
                } else if(viewSwitcher.getCurrentView() == recyclerView) {
                    // Nếu không còn mục nào trong lịch sử, chuyển về ExpandableListView
                    viewSwitcher.setDisplayedChild(0);
                } else {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                    fragmentTransaction.addToBackStack(null); // Nếu bạn muốn thêm vào back stack
                    fragmentTransaction.commit();
                }
            }
        });


        return binding.getRoot();
    }

    private void toggleViews() {
        // Nếu đang ở ExpandableListView, chuyển sang RecyclerView; ngược lại, chuyển về ExpandableListView
        if (viewSwitcher.getCurrentView() == expandableListView) {
            viewSwitcher.setDisplayedChild(1);
        }
    }


    public void getAllExhibits() {
        CollectionReference exhibitCollection = firestore.collection("Exhibit");

        exhibitCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Lấy tất cả các tài liệu từ kết quả truy vấn
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert each document to a LocalAreaModel
                        ExhibitModel exhibitModel = new ExhibitModel(
                                document.getId(),
                                document.getString("name"),
                                document.getString("description"),
                                document.getString("video"),
                                (ArrayList<String>) document.get("content"),
                                document.getString("image_path")
                        );

                        // Add the LocalAreaModel to the list
                        allExhibits.add(exhibitModel);
                    }
                    searchAdapter = new SearchAdapter(getContext(), allExhibits);
                    recyclerView.setAdapter(searchAdapter);

                    // Xử lý khi dữ liệu đã được tải về
                } else {
                    // Handle errors here
                    Log.e("MapActivity", "Error getting local areas: " + task.getException().getMessage());
                }
            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        }
    }
}

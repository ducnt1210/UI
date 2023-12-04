package com.example.ui.Quiz.ui.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Model.GiftModel;
import com.example.ui.R;
import com.example.ui.databinding.FragmentExchangeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExchangeFragment extends Fragment {

    private FragmentExchangeBinding binding;
    private List<GiftModel> giftModelList;
    private FirebaseFirestore db;
    private RecyclerView giftRecyclerView;
    private ExchangeGiftAdapter exchangeGiftAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExchangeViewModel exchangeViewModel =
                new ViewModelProvider(this).get(ExchangeViewModel.class);

        binding = FragmentExchangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        giftModelList = new ArrayList<>();
        giftRecyclerView = root.findViewById(R.id.gift_recycler_view);
        exchangeGiftAdapter = new ExchangeGiftAdapter(getContext());

        getGiftList();
//
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        giftRecyclerView.setLayoutManager(layoutManager);
        giftRecyclerView.setAdapter(exchangeGiftAdapter);

        return root;
    }

    public void getGiftList() {
        db.collection("Gift")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            GiftModel giftModel = new GiftModel(
                                    doc.getId(),
                                    doc.getString("name"),
                                    doc.getLong("price").intValue(),
                                    doc.getString("image_path")
                            );
                            giftModelList.add(giftModel);
                        }
                        exchangeGiftAdapter.setGiftModelList(giftModelList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
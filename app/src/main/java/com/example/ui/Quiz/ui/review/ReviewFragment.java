package com.example.ui.Quiz.ui.review;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ui.Model.ExchangedGiftModel;
import com.example.ui.Model.GiftModel;
import com.example.ui.Quiz.ui.exchange.ExchangeGiftAdapter;
import com.example.ui.R;
import com.example.ui.databinding.FragmentReviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private List<ExchangedGiftModel> giftModelList;
    private FirebaseFirestore db;
    private ReviewGiftAdapter reviewGiftAdapter;
    private RecyclerView exchangedGiftRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReviewViewModel reviewViewModel =
                new ViewModelProvider(this).get(ReviewViewModel.class);

        binding = FragmentReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        giftModelList = new ArrayList<>();
        exchangedGiftRecyclerView = (RecyclerView) root.findViewById(R.id.review_gift_recycler_view);
        reviewGiftAdapter = new ReviewGiftAdapter(getContext());

        getExchangedGiftList(FirebaseAuth.getInstance().getCurrentUser().getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        exchangedGiftRecyclerView.setLayoutManager(layoutManager);
        exchangedGiftRecyclerView.setAdapter(reviewGiftAdapter);

        return root;
    }

    public void getExchangedGiftList(String user_id) {
        db.collection("ExchangedGift")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc: queryDocumentSnapshots) {
                            ExchangedGiftModel exchangedGiftModel = new ExchangedGiftModel(
                                    doc.getId(),
                                    user_id,
                                    doc.getString("name"),
                                    doc.getLong("price").intValue(),
                                    doc.getString("image_path"),
                                    doc.getString("status"),
                                    doc.getTimestamp("time")
                            );
                            giftModelList.add(exchangedGiftModel);
                        }
                        Collections.sort(giftModelList, new Comparator<ExchangedGiftModel>() {
                            @Override
                            public int compare(ExchangedGiftModel o1, ExchangedGiftModel o2) {
                                return o2.getTime().compareTo(o1.getTime());
                            }
                        });
                        reviewGiftAdapter.setGiftModelList(giftModelList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Get exchanged gift failed", user_id);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
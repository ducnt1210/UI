package com.example.ui.TicketHandler;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ui.Adapter.UsedTicketAdapter;
import com.example.ui.MainActivity;
import com.example.ui.Model.TransactionModel;
import com.example.ui.databinding.FragmentUsedTicketBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Comparator;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UsedTicketFragment extends Fragment {
    FragmentUsedTicketBinding binding;
    SweetAlertDialog sweetAlertDialog;
    private UsedTicketAdapter usedTicketAdapter;
    Calendar calendar;
    Timestamp timestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsedTicketBinding.inflate(inflater, container, false);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timestamp = new Timestamp(calendar.getTime());

        // Ticket RecyclerView
        usedTicketAdapter = new UsedTicketAdapter(getContext());
        binding.rcvTicket.setAdapter(usedTicketAdapter);
        binding.rcvTicket.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    public void onResume() {
        super.onResume();
        usedTicketAdapter.clearData();
        sweetAlertDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//                getTransactionModelData();
//            }
//        }, 1000);
        getTransactionModelData();
    }

    private void getTransactionModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Transaction")
                .whereEqualTo("userID", MainActivity.currentUser.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    usedTicketAdapter.clearData();
                    System.out.println(queryDocumentSnapshots.size());
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                        TransactionModel transactionModel = ds.toObject(TransactionModel.class);
                        assert transactionModel != null;
                        if (transactionModel.getUsedTimestamp().getSeconds() - timestamp.getSeconds() < 0 || transactionModel.isUsed()) {
                            usedTicketAdapter.addData(transactionModel);
                        }
                    }
                    usedTicketAdapter.transactionModelList.sort(Comparator.comparing(TransactionModel::getUsedTimestamp));
                    usedTicketAdapter.notifyDataSetChanged();
                    System.out.println("Get data successfully " + usedTicketAdapter.transactionModelList.size());
                    sweetAlertDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.d("TicketQueryFailed", e.toString());
                    Toast.makeText(getContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                });
    }
}
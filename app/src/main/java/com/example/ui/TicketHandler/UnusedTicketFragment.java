package com.example.ui.TicketHandler;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ui.Adapter.UnusedTicketAdapter;
import com.example.ui.MainActivity;
import com.example.ui.Model.TransactionModel;
import com.example.ui.databinding.FragmentUnusedTicketBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Comparator;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UnusedTicketFragment extends Fragment {
    FragmentUnusedTicketBinding binding;
    SweetAlertDialog sweetAlertDialog;
    private UnusedTicketAdapter unusedTicketAdapter;
    Calendar calendar;
    Timestamp timestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUnusedTicketBinding.inflate(inflater, container, false);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timestamp = new Timestamp(calendar.getTime());

        // Ticket RecyclerView
        unusedTicketAdapter = new UnusedTicketAdapter(getContext());
        binding.rcvTicket.setAdapter(unusedTicketAdapter);
        binding.rcvTicket.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    public void onResume() {
        super.onResume();
        unusedTicketAdapter.clearData();
        sweetAlertDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        getTransactionModelData();
    }

    private void getTransactionModelData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Transaction")
                .whereEqualTo("userID", MainActivity.currentUser.getId())
                .whereEqualTo("used", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    unusedTicketAdapter.clearData();
                    System.out.println(queryDocumentSnapshots.size());
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                        TransactionModel transactionModel = ds.toObject(TransactionModel.class);
                        assert transactionModel != null;
                        if (transactionModel.getUsedTimestamp().getSeconds() - timestamp.getSeconds() >= 0) {
                            unusedTicketAdapter.addData(transactionModel);
                        }
                    }
                    unusedTicketAdapter.transactionModelList.sort(Comparator.comparing(TransactionModel::getUsedTimestamp));
                    unusedTicketAdapter.notifyDataSetChanged();
                    System.out.println("Get data successfully " + unusedTicketAdapter.transactionModelList.size());
                    sweetAlertDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.d("TicketQueryFailed", e.toString());
                    Toast.makeText(getContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                });
    }
}
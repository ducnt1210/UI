package com.example.ui.MainActivityPackage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.Adapters.NotificationModelAdapter;
import com.example.ui.Model.NotificationModel;
import com.example.ui.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NotifcationFragment extends Fragment {
    RecyclerView recyclerView;
    private FirebaseFirestore db;
    private NotificationModelAdapter notificationModelAdapter;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifcation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notificationModelAdapter = new NotificationModelAdapter(getContext());
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);
//        sweetAlertDialog.show();
        getNotificationModelList();
        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationModelAdapter);
    }

    private void getNotificationModelList() {
        db = FirebaseFirestore.getInstance();
        List<NotificationModel> notificationModelList = new ArrayList<>();
//        DocumentReference documentReference = db.collection("Notification").document(document);
//        documentReference.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                String description = (String) documentSnapshot.get("decription");
//                String image_path = (String) documentSnapshot.get("image_path");
//                boolean seen = (boolean) documentSnapshot.get("seen");
//                Timestamp time = (Timestamp) documentSnapshot.get("time");
//                String user_id = (String) documentSnapshot.get("user_id");
//
//            }
//        })

        db.collection("Notification")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc: task.getResult()) {
                            NotificationModel notificationModel =
                                    new NotificationModel(doc.getId(),
                                            doc.getString("image_path"),
                                            doc.getString("description"),
                                            doc.getString("user_id"),
                                            doc.getBoolean("seen"),
                                            doc.getTimestamp("time"));
                            notificationModelList.add(notificationModel);
                        }
//                        Log.d("templist", Integer.toString(notificationModels.size()));
//                        notificationModelAdapter =
//                                new NotificationModelAdapter(getContext(), notificationModelList);
//                        Log.d("lenghtlist", Integer.toString(notificationModelList.size()));
                        notificationModelAdapter.setNotificationModelList(notificationModelList);
                        Log.d("lenghtlist", Integer.toString(notificationModelAdapter.notificationModelList.size()));
//                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismissWithAnimation();
                        Toast.makeText(getActivity(), "The server is experiencing an error. Please come back later", Toast.LENGTH_SHORT).show();
                    }
                });
//        Log.d("templist", Integer.toString(notificationModels.size()));
    }
}
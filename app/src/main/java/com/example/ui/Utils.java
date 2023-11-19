package com.example.ui;

import static java.lang.System.exit;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ui.Model.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Utils {
    public static List<NotificationModel> notSentNotification(String user_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<NotificationModel> notSentNotifications = new ArrayList<>();
//        List<NotificationModel> des;

        db.collection("Notification")
                .whereEqualTo("user_id", user_id)
                .whereEqualTo("sentNotification", false)
                .orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc: task.getResult()) {
                            NotificationModel notificationModel =
                                    new NotificationModel(doc.getId(),
                                            doc.getString("image_path"),
                                            (List<String>) doc.get("description"),
                                            doc.getString("user_id"),
                                            doc.getBoolean("seen"),
                                            doc.getBoolean("sentNotification"),
                                            doc.getTimestamp("time"));
                            if (notificationModel.getSentNotification() == false) {
                                notSentNotifications.add(notificationModel);
                            }
                        }
//                        des.addAll(notSentNotifications);
//                        Collections.sort(des, new Comparator<NotificationModel>() {
//                            @Override
//                            public int compare(NotificationModel o1, NotificationModel o2) {
//                                return o1.getTime().compareTo(o2.getTime());
//                            }
//                        });
                        Log.d("length1", Integer.toString(notSentNotifications.size()));
                    }
                });
        Log.d("length2", Integer.toString(notSentNotifications.size()));
        return notSentNotifications;
    }

    public static void updateSentNotification(NotificationModel notificationModel) {
        notificationModel.setSentNotification(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification")
                .document(notificationModel.getId())
                .set(notificationModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Update sent notification", notificationModel.getId());
                    }
                });
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return date1.getDate() == date2.getDate()
                && date1.getMonth() == date2.getMonth()
                && date1.getYear() == date2.getYear();
    }

    public static String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();
        String pattern = "hh:mm dd/MM/yyyy";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//        SimpleDateFormat sdf =
        DateFormat formatter = new SimpleDateFormat(pattern);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }
}

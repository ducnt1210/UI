package com.example.ui;

import static java.lang.System.exit;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.Model.NotificationModel;
import com.example.ui.Model.ScoreModel;
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
import java.util.concurrent.TimeUnit;

public class Utils {
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

    public static void updateScore(ScoreModel scoreModel) {
        FirebaseFirestore.getInstance().collection("Score")
                .document(scoreModel.getId())
                .set(scoreModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Update score failed", scoreModel.getId());
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
        String pattern = "hh:mm a dd/MM/yyyy";
        DateFormat formatter = new SimpleDateFormat(pattern);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public static String formatTime(long timeInMilliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds))
        );
    }
}

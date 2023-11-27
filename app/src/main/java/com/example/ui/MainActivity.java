package com.example.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ui.MainActivityPackage.ArtifactsFragment;
import com.example.ui.MainActivityPackage.HomeFragment;
import com.example.ui.MainActivityPackage.NotificationFragment;
import com.example.ui.MainActivityPackage.SettingFragment;
import com.example.ui.Model.NotificationModel;
import com.example.ui.Model.UserModel;
import com.example.ui.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static UserModel currentUser;
    private String FragmentID = "HomeFragment";
    public static Uri profilePicture = null;
    FirebaseUser user;
    ActivityMainBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        notSentNotification(user.getUid());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUser = new UserModel(user.getUid(), user.getDisplayName(), null, user.getEmail()); // temp fix for current user not loaded when from Main info to setting fragment

        FragmentID = getIntent().getStringExtra("FragmentID");
        if (FragmentID != null) {
            switch (FragmentID) {
                case "HomeFragment":
                    replaceFragment(new HomeFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                    break;
                case "ArtifactsFragment":
                    replaceFragment(new ArtifactsFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.none);
                    break;
                case "NotificationFragment":
                    replaceFragment(new NotificationFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.notification);
                    break;
                case "SettingFragment":
                    replaceFragment(new SettingFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.setting);
                    break;
            }
        } else {
            FragmentID = "HomeFragment";
            replaceFragment(new HomeFragment());
        }

        getUser();

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    FragmentID = "HomeFragment";
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.none:
                    FragmentID = "ArtifactsFragment";
                    replaceFragment(new ArtifactsFragment());
                    break;
                case R.id.notification:
                    FragmentID = "NotificationFragment";
                    replaceFragment(new NotificationFragment());
                    break;
                case R.id.setting:
                    FragmentID = "SettingFragment";
                    replaceFragment(new SettingFragment());
                    break;
            }
            notSentNotification(user.getUid());
            return true;
        });

        binding.qrScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QRActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Log.e("FragmentID", FragmentID);
        if (FragmentID == "HomeFragment") {
            finish();
        } else {
            FragmentID = "HomeFragment";
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    protected void getUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (user != null) {
            String Uid = user.getUid();
//            Comment this to fix bug of current user not loaded when from edit info to setting fragment
//            currentUser = new UserModel(Uid, null, null, null);
            FirebaseFirestore.getInstance().collection("User").document(Uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            currentUser = documentSnapshot.toObject(UserModel.class);
                            assert currentUser != null;
                            currentUser.setId(Uid);
                            getProfilePicture();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "User Failed!", Toast.LENGTH_SHORT).show();
//                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
        }
    }

    public void getProfilePicture() {
        FirebaseStorage.getInstance().getReference().child("images/" + currentUser.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profilePicture = uri;
//                        sweetAlertDialog.dismissWithAnimation();
                        Log.d("FinancialApp", "Get profile picture");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        sweetAlertDialog.dismissWithAnimation();
                        Log.d("FinancialApp", e.getMessage());
                    }
                });
    }

    private void notSentNotification(String user_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<NotificationModel> notSentNotifications = new ArrayList<>();
//        List<NotificationModel> des;

        db.collection("Notification")
                .whereEqualTo("user_id", user.getUid())
                .whereEqualTo("sentNotification", false)
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

//                            notSentNotifications.add(notificationModel);
                            if (notificationModel.getSentNotification() == false) {
                                notSentNotifications.add(notificationModel);
                            }
                        }
//                        des.addAll(notSentNotifications);
                        Collections.sort(notSentNotifications, new Comparator<NotificationModel>() {
                            @Override
                            public int compare(NotificationModel o1, NotificationModel o2) {
                                return o1.getTime().compareTo(o2.getTime());
                            }
                        });
                        for (NotificationModel notificationModel: notSentNotifications) {
                            Log.d("notification", Boolean.toString(notificationModel.getSentNotification()));
                            sendNotification(notificationModel);
                            Utils.updateSentNotification(notificationModel);
                        }
//                        Log.d("length1", Integer.toString(notSentNotifications.size()));
                    }
                });
//        Log.d("length2", Integer.toString(notSentNotifications.size()));
    }

    private void sendNotification(NotificationModel notificationModel) {
        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("description", (ArrayList<String>) notificationModel.getDescription());
        bundle.putString("time", notificationModel.formatDate());
        bundle.putBoolean("update", true);
        bundle.putString("id", notificationModel.getId());
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int) new Date().getTime(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID);
//        builder.setContentTitle(notificationModel.heading());
//        builder.setSmallIcon(R.drawable.gg_signin);
//        builder.setContentText(notificationModel.fullDescription());
//        builder.setAutoCancel(true);
//        builder.setContentIntent(pendingIntent);

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(notificationModel.heading())
                .setContentText(notificationModel.fullDescription())
                .setSmallIcon(R.drawable.gg_signin)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        managerCompat.notify((int) new Date().getTime(), notification);
    }
}
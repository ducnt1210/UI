package com.example.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.zeugmasolutions.localehelper.LocaleAwareApplication;

public class MyApplication extends LocaleAwareApplication {
    public static final String CHANNEL_ID = "1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Notification";
            String description = "VMuseum Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

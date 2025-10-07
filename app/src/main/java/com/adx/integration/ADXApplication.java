package com.adx.integration;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Main Application class for ADX Integration
 * Initializes dependency injection and notification channels
 */
@HiltAndroidApp
public class ADXApplication extends Application {

    public static final String CHANNEL_ID_ADS = "adx_ads_channel";
    public static final String CHANNEL_ID_LOCATION = "adx_location_channel";
    public static final String CHANNEL_ID_CREDITS = "adx_credits_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    /**
     * Creates notification channels for different types of notifications
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Ads notification channel
            NotificationChannel adsChannel = new NotificationChannel(
                    CHANNEL_ID_ADS,
                    "ADX Ads",
                    NotificationManager.IMPORTANCE_HIGH
            );
            adsChannel.setDescription("Notifications for new advertisements");
            adsChannel.enableLights(true);
            adsChannel.enableVibration(true);

            // Location notification channel
            NotificationChannel locationChannel = new NotificationChannel(
                    CHANNEL_ID_LOCATION,
                    "ADX Location",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            locationChannel.setDescription("Location-based notifications");
            locationChannel.setVibrationPattern(new long[]{0, 250, 250, 250});

            // Credits notification channel
            NotificationChannel creditsChannel = new NotificationChannel(
                    CHANNEL_ID_CREDITS,
                    "ADX Credits",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            creditsChannel.setDescription("Credit balance and transaction notifications");

            notificationManager.createNotificationChannel(adsChannel);
            notificationManager.createNotificationChannel(locationChannel);
            notificationManager.createNotificationChannel(creditsChannel);
        }
    }
}
package com.adx.integration.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.adx.integration.data.repository.AdRepository;

import java.util.concurrent.TimeUnit;

/**
 * Service for synchronizing advertisements from ADX server
 * Runs every 2 minutes as specified in requirements
 */
public class AdSyncService extends Service {

    private static final String WORK_TAG = "adx_ad_sync";
    private static final long SYNC_INTERVAL_MINUTES = 2; // 2 minutes as specified

    private AdRepository adRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        adRepository = new AdRepository(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleAdSync();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Schedule periodic ad synchronization using WorkManager
     */
    private void scheduleAdSync() {
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                AdSyncWorker.class,
                SYNC_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        ).addTag(WORK_TAG)
         .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    /**
     * Cancel scheduled ad synchronization
     */
    public void cancelAdSync() {
        WorkManager.getInstance(this).cancelUniqueWork(WORK_TAG);
    }

    public static class AdSyncWorker extends androidx.work.Worker {

        public AdSyncWorker(@NonNull Context context, @NonNull androidx.work.WorkerParameters params) {
            super(context, params);
        }

        @NonNull
        @Override
        public androidx.work.Result doWork() {
            try {
                AdRepository repository = new AdRepository(getApplicationContext());
                
                // Fetch ads from ADX server
                repository.syncAdsFromServer();
                
                return androidx.work.Result.success();
            } catch (Exception e) {
                return androidx.work.Result.failure();
            }
        }
    }
}
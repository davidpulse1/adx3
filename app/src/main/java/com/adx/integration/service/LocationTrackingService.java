package com.adx.integration.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.adx.integration.ADXApplication;
import com.adx.integration.R;
import com.adx.integration.data.model.Store;
import com.adx.integration.data.repository.LocationRepository;
import com.adx.integration.ui.main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background service for continuous location tracking
 * Implements radius-based credit system with 10-minute timeout
 */
public class LocationTrackingService extends Service {

    private static final String CHANNEL_ID = "adx_location_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; // 5 seconds
    private static final long FASTEST_UPDATE_INTERVAL = 2000; // 2 seconds
    private static final float MINIMUM_DISPLACEMENT = 10; // 10 meters

    private final IBinder binder = new LocalBinder();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRepository locationRepository;
    private ExecutorService executorService;
    
    private Location lastLocation;
    private boolean isTracking = false;

    // Radius tracking
    private static final long RADIUS_TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes
    private java.util.Map<String, Long> radiusEntryTimes = new java.util.HashMap<>();

    public class LocalBinder extends Binder {
        public LocationTrackingService getService() {
            return LocationTrackingService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeService();
    }

    private void initializeService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRepository = new LocationRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        
        createNotificationChannel();
        setupLocationCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        startLocationTracking();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationTracking();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Start continuous location tracking
     */
    public void startTracking() {
        if (!isTracking) {
            startLocationTracking();
        }
    }

    /**
     * Stop location tracking
     */
    public void stopTracking() {
        if (isTracking) {
            stopLocationTracking();
        }
    }

    private void startLocationTracking() {
        if (isTracking) return;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(MINIMUM_DISPLACEMENT);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            isTracking = true;
        } catch (SecurityException e) {
            // Handle permission error
            stopSelf();
        }
    }

    private void stopLocationTracking() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        isTracking = false;
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    handleLocationUpdate(location);
                }
            }
        };
    }

    private void handleLocationUpdate(Location location) {
        lastLocation = location;
        
        // Update location in repository
        executorService.execute(() -> {
            try {
                locationRepository.updateLocation(location.getLatitude(), location.getLongitude());
                
                // Check for nearby stores and radius entries
                checkNearbyStores(location);
                
                // Check for radius timeouts
                checkRadiusTimeouts();
                
            } catch (Exception e) {
                // Handle error
            }
        });
    }

    private void checkNearbyStores(Location userLocation) {
        try {
            List<Store> nearbyStores = locationRepository.getNearbyStores(
                userLocation.getLatitude(), 
                userLocation.getLongitude(), 
                1000 // 1km radius
            );

            for (Store store : nearbyStores) {
                if (store.isRadiusEnabled()) {
                    boolean isInRadius = isUserInStoreRadius(userLocation, store);
                    handleRadiusChange(store, isInRadius);
                }
            }
        } catch (Exception e) {
            // Handle error
        }
    }

    private boolean isUserInStoreRadius(Location userLocation, Store store) {
        if (!store.isLocationValid()) return false;
        
        float[] results = new float[1];
        android.location.Location.distanceBetween(
            userLocation.getLatitude(), userLocation.getLongitude(),
            store.getLatitude(), store.getLongitude(),
            results
        );
        
        return results[0] <= store.getRadius();
    }

    private void handleRadiusChange(Store store, boolean isInRadius) {
        String storeId = store.getId();
        boolean wasInRadius = radiusEntryTimes.containsKey(storeId);
        
        if (isInRadius && !wasInRadius) {
            // User entered radius
            enterStoreRadius(store);
        } else if (!isInRadius && wasInRadius) {
            // User exited radius
            exitStoreRadius(store);
        }
    }

    private void enterStoreRadius(Store store) {
        String storeId = store.getId();
        long currentTime = System.currentTimeMillis();
        
        // Deduct credits immediately
        boolean creditsDeducted = locationRepository.deductRadiusCredits(storeId, store.getCreditDeduction());
        
        if (creditsDeducted) {
            radiusEntryTimes.put(storeId, currentTime);
            
            // Send notification
            sendRadiusNotification(store.getName(), "You entered the store radius. " + 
                                 store.getCreditDeduction() + " credits deducted.");
        }
    }

    private void exitStoreRadius(Store store) {
        String storeId = store.getId();
        radiusEntryTimes.remove(storeId);
        
        // User left before timeout - credits are kept
        sendRadiusNotification(store.getName(), "You left the store radius.");
    }

    private void checkRadiusTimeouts() {
        long currentTime = System.currentTimeMillis();
        List<String> timedOutStores = new ArrayList<>();
        
        for (java.util.Map.Entry<String, Long> entry : radiusEntryTimes.entrySet()) {
            long timeInRadius = currentTime - entry.getValue();
            
            if (timeInRadius >= RADIUS_TIMEOUT_MS) {
                timedOutStores.add(entry.getKey());
            }
        }
        
        // Return credits for timed-out stores
        for (String storeId : timedOutStores) {
            returnRadiusCredits(storeId);
            radiusEntryTimes.remove(storeId);
        }
    }

    private void returnRadiusCredits(String storeId) {
        try {
            locationRepository.returnRadiusCredits(storeId);
            
            // Send notification
            Store store = locationRepository.getStoreById(storeId);
            if (store != null) {
                sendRadiusNotification(store.getName(), 
                    "Radius timeout: credits returned for not visiting the store.");
            }
        } catch (Exception e) {
            // Handle error
        }
    }

    private void sendRadiusNotification(String storeName, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ADX - " + storeName)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(storeName.hashCode(), notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ADX Location Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Location tracking for store radius detection");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ADX Location Service")
                .setContentText("Tracking your location for optimal ad experience")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public boolean isTracking() {
        return isTracking;
    }
}
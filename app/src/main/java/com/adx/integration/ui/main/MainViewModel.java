package com.adx.integration.ui.main;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adx.integration.data.model.User;
import com.adx.integration.data.model.Advertisement;
import com.adx.integration.data.model.CreditBalance;
import com.adx.integration.data.model.CreditTransaction;
import com.adx.integration.data.model.Store;
import com.adx.integration.data.repository.AdRepository;
import com.adx.integration.data.repository.CreditRepository;
import com.adx.integration.data.repository.UserRepository;
import com.adx.integration.data.repository.LocationRepository;
import com.adx.integration.service.AdSyncService;
import com.adx.integration.service.LocationTrackingService;
import com.adx.integration.utils.NetworkUtils;
import com.adx.integration.utils.PreferencesManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for MainActivity managing UI-related data
 */
@HiltViewModel
public class MainViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final CreditRepository creditRepository;
    private final LocationRepository locationRepository;
    private final PreferencesManager preferencesManager;
    private final ExecutorService executorService;

    // LiveData for UI observation
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Integer> availableAdCount = new MutableLiveData<>(0);
    private final MutableLiveData<CreditBalance> creditBalance = new MutableLiveData<>();
    private final MutableLiveData<List<Store>> nearbyStores = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // Services
    private AdSyncService adSyncService;
    private LocationTrackingService locationTrackingService;

    @Inject
    public MainViewModel(@NonNull Application application,
                        UserRepository userRepository,
                        AdRepository adRepository,
                        CreditRepository creditRepository,
                        LocationRepository locationRepository,
                        PreferencesManager preferencesManager) {
        super(application);
        this.userRepository = userRepository;
        this.adRepository = adRepository;
        this.creditRepository = creditRepository;
        this.locationRepository = locationRepository;
        this.preferencesManager = preferencesManager;
        this.executorService = Executors.newFixedThreadPool(4);
        
        initializeServices();
    }

    private void initializeServices() {
        adSyncService = new AdSyncService(getApplication(), adRepository);
        locationTrackingService = new LocationTrackingService(getApplication(), locationRepository);
    }

    /**
     * Check if user is authenticated and load user data
     */
    public void checkAuthentication() {
        executorService.execute(() -> {
            try {
                String token = preferencesManager.getAuthToken();
                if (token == null || token.isEmpty()) {
                    user.postValue(null);
                    return;
                }

                User currentUser = userRepository.getCurrentUserSync();
                if (currentUser != null) {
                    user.postValue(currentUser);
                    loadUserData();
                } else {
                    user.postValue(null);
                }
            } catch (Exception e) {
                error.postValue("Authentication check failed: " + e.getMessage());
                user.postValue(null);
            }
        });
    }

    /**
     * Load all user-related data
     */
    private void loadUserData() {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadCachedData();
            return;
        }

        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                
                // Load user dashboard data
                loadDashboardData();
                
                // Load available ads
                loadAvailableAds();
                
                // Load credit balance
                loadCreditBalance();
                
                // Load nearby stores if location is available
                loadNearbyStores();
                
            } catch (Exception e) {
                error.postValue("Failed to load user data: " + e.getMessage());
                loadCachedData();
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    private void loadDashboardData() {
        try {
            // This would load comprehensive dashboard data from API
            // For now, we'll load individual components
        } catch (Exception e) {
            error.postValue("Failed to load dashboard: " + e.getMessage());
        }
    }

    private void loadAvailableAds() {
        try {
            List<Advertisement> ads = adRepository.getAvailableAdsSync();
            availableAdCount.postValue(ads != null ? ads.size() : 0);
        } catch (Exception e) {
            error.postValue("Failed to load ads: " + e.getMessage());
            loadCachedAds();
        }
    }

    private void loadCreditBalance() {
        try {
            CreditBalance balance = creditRepository.getCreditBalanceSync();
            creditBalance.postValue(balance);
        } catch (Exception e) {
            error.postValue("Failed to load credit balance: " + e.getMessage());
            loadCachedCreditBalance();
        }
    }

    private void loadNearbyStores() {
        Location lastLocation = locationRepository.getLastLocation();
        if (lastLocation != null) {
            try {
                List<Store> stores = adRepository.getNearbyStoresSync(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude(),
                    1000 // 1km radius
                );
                nearbyStores.postValue(stores);
            } catch (Exception e) {
                error.postValue("Failed to load nearby stores: " + e.getMessage());
                loadCachedStores();
            }
        }
    }

    private void loadCachedData() {
        loadCachedAds();
        loadCachedCreditBalance();
        loadCachedStores();
    }

    private void loadCachedAds() {
        executorService.execute(() -> {
            try {
                List<Advertisement> cachedAds = adRepository.getCachedAds();
                availableAdCount.postValue(cachedAds.size());
            } catch (Exception e) {
                availableAdCount.postValue(0);
            }
        });
    }

    private void loadCachedCreditBalance() {
        executorService.execute(() -> {
            try {
                CreditBalance cachedBalance = creditRepository.getCachedCreditBalance();
                creditBalance.postValue(cachedBalance);
            } catch (Exception e) {
                // Use default balance
                CreditBalance defaultBalance = new CreditBalance();
                defaultBalance.setCredits(0);
                defaultBalance.setUsdValue(0.0);
                creditBalance.postValue(defaultBalance);
            }
        });
    }

    private void loadCachedStores() {
        executorService.execute(() -> {
            try {
                List<Store> cachedStores = adRepository.getCachedStores();
                nearbyStores.postValue(cachedStores);
            } catch (Exception e) {
                nearbyStores.postValue(null);
            }
        });
    }

    /**
     * Start location tracking service
     */
    public void startLocationTracking() {
        if (locationTrackingService != null) {
            locationTrackingService.startTracking();
        }
    }

    /**
     * Stop location tracking service
     */
    public void stopLocationTracking() {
        if (locationTrackingService != null) {
            locationTrackingService.stopTracking();
        }
    }

    /**
     * Start ad synchronization service
     */
    public void startAdSync() {
        if (adSyncService != null) {
            adSyncService.startSync();
        }
    }

    /**
     * Stop ad synchronization service
     */
    public void stopAdSync() {
        if (adSyncService != null) {
            adSyncService.stopSync();
        }
    }

    /**
     * Refresh all data
     */
    public void refreshData() {
        loadUserData();
    }

    /**
     * Handle ad interaction
     */
    public void interactWithAd(Advertisement ad, String interactionType) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                
                switch (interactionType) {
                    case "receive":
                        adRepository.receiveAd(ad.getAdId());
                        break;
                    case "click":
                        adRepository.clickAd(ad.getAdId());
                        break;
                }
                
                // Refresh data after interaction
                loadAvailableAds();
                loadCreditBalance();
                
            } catch (Exception e) {
                error.postValue("Ad interaction failed: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Handle store visit
     */
    public void visitStore(Store store) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                
                adRepository.visitStore(store.getId());
                
                // Refresh data after visit
                loadCreditBalance();
                loadNearbyStores();
                
            } catch (Exception e) {
                error.postValue("Store visit failed: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Purchase credits
     */
    public void purchaseCredits(int amount) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                
                creditRepository.purchaseCredits(amount);
                
                // Refresh credit balance
                loadCreditBalance();
                
            } catch (Exception e) {
                error.postValue("Credit purchase failed: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Logout user
     */
    public void logout() {
        executorService.execute(() -> {
            try {
                preferencesManager.clearAuthToken();
                userRepository.clearUserData();
                user.postValue(null);
            } catch (Exception e) {
                error.postValue("Logout failed: " + e.getMessage());
            }
        });
    }

    // LiveData getters
    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Integer> getAvailableAdCount() {
        return availableAdCount;
    }

    public LiveData<CreditBalance> getCreditBalance() {
        return creditBalance;
    }

    public LiveData<List<Store>> getNearbyStores() {
        return nearbyStores;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
        if (adSyncService != null) {
            adSyncService.stopSync();
        }
        if (locationTrackingService != null) {
            locationTrackingService.stopTracking();
        }
    }
}
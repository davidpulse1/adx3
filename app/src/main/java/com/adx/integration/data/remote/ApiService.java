package com.adx.integration.data.remote;

import com.adx.integration.data.model.Advertisement;
import com.adx.integration.data.model.CreditTransaction;
import com.adx.integration.data.model.Store;
import com.adx.integration.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API service interface for ADX backend communication
 */
public interface ApiService {

    // Authentication endpoints
    @POST("auth/github/callback")
    Call<ApiResponse<User>> githubAuthCallback(@Body Map<String, Object> authData);

    @POST("auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken();

    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();

    // Advertisement endpoints
    @GET("ads/available")
    Call<ApiResponse<List<Advertisement>>> getAvailableAds(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("category") String category,
            @Query("limit") Integer limit
    );

    @GET("ads/{adId}")
    Call<ApiResponse<Advertisement>> getAdById(@Path("adId") String adId);

    @POST("ads/{adId}/receive")
    Call<ApiResponse<AdInteractionResponse>> receiveAd(
            @Path("adId") String adId,
            @Body Map<String, Object> locationData
    );

    @POST("ads/{adId}/click")
    Call<ApiResponse<AdInteractionResponse>> clickAd(
            @Path("adId") String adId,
            @Body Map<String, Object> locationData
    );

    @POST("ads/store/{storeId}/visit")
    Call<ApiResponse<StoreVisitResponse>> visitStore(
            @Path("storeId") String storeId,
            @Body Map<String, Object> locationData
    );

    @GET("ads/history/user")
    Call<ApiResponse<List<AdInteraction>>> getUserAdHistory(
            @Query("limit") Integer limit
    );

    @GET("ads/category/{category}")
    Call<ApiResponse<List<Advertisement>>> getAdsByCategory(
            @Path("category") String category,
            @Query("limit") Integer limit
    );

    @GET("ads/sync/status")
    Call<ApiResponse<SyncStatus>> getAdSyncStatus();

    @POST("ads/sync/trigger")
    Call<ApiResponse<SyncResult>> triggerAdSync();

    // Credit management endpoints
    @GET("credits/balance")
    Call<ApiResponse<CreditBalance>> getCreditBalance();

    @GET("credits/history")
    Call<ApiResponse<List<CreditTransaction>>> getTransactionHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("type") String type,
            @Query("reason") String reason,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("credits/stats")
    Call<ApiResponse<CreditStats>> getCreditStats();

    @POST("credits/purchase")
    Call<ApiResponse<CreditPurchaseResponse>> purchaseCredits(
            @Body Map<String, Object> purchaseData
    );

    @GET("credits/convert")
    Call<ApiResponse<CreditConversion>> convertCredits(
            @Query("credits") Integer credits
    );

    @GET("credits/opportunities")
    Call<ApiResponse<CreditOpportunities>> getCreditOpportunities();

    // Wallet endpoints
    @POST("wallet/google-pay/initialize")
    Call<ApiResponse<GooglePayConfig>> initializeGooglePay();

    @POST("wallet/google-pay/payment-request")
    Call<ApiResponse<PaymentRequest>> createPaymentRequest(
            @Body Map<String, Object> paymentData
    );

    @POST("wallet/process-payment")
    Call<ApiResponse<PaymentResponse>> processPayment(
            @Body Map<String, Object> paymentData
    );

    // User management endpoints
    @PUT("users/location")
    Call<ApiResponse<LocationUpdateResponse>> updateLocation(
            @Body Map<String, Object> locationData
    );

    @GET("users/nearby-stores")
    Call<ApiResponse<List<Store>>> getNearbyStores(
            @Query("radius") Integer radius
    );

    @GET("users/store-recommendations")
    Call<ApiResponse<List<Store>>> getStoreRecommendations(
            @Query("limit") Integer limit
    );

    @GET("users/location-history")
    Call<ApiResponse<LocationHistory>> getLocationHistory(
            @Query("limit") Integer limit
    );

    @PUT("users/preferences")
    Call<ApiResponse<UserPreferences>> updatePreferences(
            @Body Map<String, Object> preferences
    );

    @GET("users/dashboard")
    Call<ApiResponse<DashboardData>> getDashboardData();

    @GET("users/profile")
    Call<ApiResponse<User>> getUserProfile();

    // Health check
    @GET("health")
    Call<ApiResponse<HealthStatus>> getHealthStatus();

    /**
     * Generic API response wrapper
     */
    public static class ApiResponse<T> {
        @SerializedName("success")
        private boolean success;
        
        @SerializedName("data")
        private T data;
        
        @SerializedName("error")
        private String error;
        
        @SerializedName("message")
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Authentication response
     */
    public static class AuthResponse {
        @SerializedName("token")
        private String token;
        
        @SerializedName("user")
        private User user;

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * Ad interaction response
     */
    public static class AdInteractionResponse {
        @SerializedName("transaction")
        private CreditTransaction transaction;
        
        @SerializedName("remainingCredits")
        private int remainingCredits;
        
        @SerializedName("redirectUrl")
        private String redirectUrl;

        public CreditTransaction getTransaction() {
            return transaction;
        }

        public int getRemainingCredits() {
            return remainingCredits;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }
    }

    /**
     * Store visit response
     */
    public static class StoreVisitResponse {
        @SerializedName("transaction")
        private CreditTransaction transaction;
        
        @SerializedName("remainingCredits")
        private int remainingCredits;

        public CreditTransaction getTransaction() {
            return transaction;
        }

        public int getRemainingCredits() {
            return remainingCredits;
        }
    }

    /**
     * Credit balance
     */
    public static class CreditBalance {
        @SerializedName("credits")
        private int credits;
        
        @SerializedName("usdValue")
        private double usdValue;
        
        @SerializedName("totalEarned")
        private int totalEarned;
        
        @SerializedName("totalSpent")
        private int totalSpent;

        public int getCredits() {
            return credits;
        }

        public double getUsdValue() {
            return usdValue;
        }

        public int getTotalEarned() {
            return totalEarned;
        }

        public int getTotalSpent() {
            return totalSpent;
        }
    }

    /**
     * Credit statistics
     */
    public static class CreditStats {
        @SerializedName("currentBalance")
        private int currentBalance;
        
        @SerializedName("totalEarned")
        private int totalEarned;
        
        @SerializedName("totalSpent")
        private int totalSpent;
        
        @SerializedName("usdValue")
        private double usdValue;
        
        @SerializedName("statsByReason")
        private List<ReasonStats> statsByReason;
        
        @SerializedName("monthlyStats")
        private MonthlyStats monthlyStats;

        public int getCurrentBalance() {
            return currentBalance;
        }

        public int getTotalEarned() {
            return totalEarned;
        }

        public int getTotalSpent() {
            return totalSpent;
        }

        public double getUsdValue() {
            return usdValue;
        }

        public List<ReasonStats> getStatsByReason() {
            return statsByReason;
        }

        public MonthlyStats getMonthlyStats() {
            return monthlyStats;
        }
    }

    /**
     * Reason statistics
     */
    public static class ReasonStats {
        @SerializedName("_id")
        private String reason;
        
        @SerializedName("count")
        private int count;
        
        @SerializedName("totalAmount")
        private int totalAmount;

        public String getReason() {
            return reason;
        }

        public int getCount() {
            return count;
        }

        public int getTotalAmount() {
            return totalAmount;
        }
    }

    /**
     * Monthly statistics
     */
    public static class MonthlyStats {
        @SerializedName("totalEarned")
        private int totalEarned;
        
        @SerializedName("totalSpent")
        private int totalSpent;
        
        @SerializedName("transactionCount")
        private int transactionCount;

        public int getTotalEarned() {
            return totalEarned;
        }

        public int getTotalSpent() {
            return totalSpent;
        }

        public int getTransactionCount() {
            return transactionCount;
        }
    }

    /**
     * Credit conversion
     */
    public static class CreditConversion {
        @SerializedName("credits")
        private int credits;
        
        @SerializedName("usdValue")
        private double usdValue;
        
        @SerializedName("conversionRate")
        private int conversionRate;

        public int getCredits() {
            return credits;
        }

        public double getUsdValue() {
            return usdValue;
        }

        public int getConversionRate() {
            return conversionRate;
        }
    }

    /**
     * Credit purchase response
     */
    public static class CreditPurchaseResponse {
        @SerializedName("transaction")
        private CreditTransaction transaction;
        
        @SerializedName("newBalance")
        private int newBalance;
        
        @SerializedName("creditsPurchased")
        private int creditsPurchased;

        public CreditTransaction getTransaction() {
            return transaction;
        }

        public int getNewBalance() {
            return newBalance;
        }

        public int getCreditsPurchased() {
            return creditsPurchased;
        }
    }

    /**
     * Google Pay configuration
     */
    public static class GooglePayConfig {
        @SerializedName("config")
        private Object config;

        public Object getConfig() {
            return config;
        }
    }

    /**
     * Payment request
     */
    public static class PaymentRequest {
        @SerializedName("paymentRequest")
        private Object paymentRequest;

        public Object getPaymentRequest() {
            return paymentRequest;
        }
    }

    /**
     * Payment response
     */
    public static class PaymentResponse {
        @SerializedName("transaction")
        private CreditTransaction transaction;
        
        @SerializedName("paymentIntent")
        private Object paymentIntent;

        public CreditTransaction getTransaction() {
            return transaction;
        }

        public Object getPaymentIntent() {
            return paymentIntent;
        }
    }

    /**
     * Location update response
     */
    public static class LocationUpdateResponse {
        @SerializedName("location")
        private LocationData location;
        
        @SerializedName("nearbyStores")
        private List<Store> nearbyStores;
        
        @SerializedName("storesInRadius")
        private int storesInRadius;

        public LocationData getLocation() {
            return location;
        }

        public List<Store> getNearbyStores() {
            return nearbyStores;
        }

        public int getStoresInRadius() {
            return storesInRadius;
        }
    }

    /**
     * Location data
     */
    public static class LocationData {
        @SerializedName("latitude")
        private double latitude;
        
        @SerializedName("longitude")
        private double longitude;
        
        @SerializedName("lastUpdated")
        private Date lastUpdated;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public Date getLastUpdated() {
            return lastUpdated;
        }
    }

    /**
     * Ad interaction
     */
    public static class AdInteraction {
        @SerializedName("adId")
        private Advertisement adId;
        
        @SerializedName("interactionType")
        private String interactionType;
        
        @SerializedName("credits")
        private int credits;
        
        @SerializedName("timestamp")
        private Date timestamp;

        public Advertisement getAdId() {
            return adId;
        }

        public String getInteractionType() {
            return interactionType;
        }

        public int getCredits() {
            return credits;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Sync status
     */
    public static class SyncStatus {
        @SerializedName("lastSyncTime")
        private Date lastSyncTime;
        
        @SerializedName("syncInterval")
        private int syncInterval;
        
        @SerializedName("serverUrl")
        private String serverUrl;
        
        @SerializedName("isConfigured")
        private boolean isConfigured;

        public Date getLastSyncTime() {
            return lastSyncTime;
        }

        public int getSyncInterval() {
            return syncInterval;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public boolean isConfigured() {
            return isConfigured;
        }
    }

    /**
     * Sync result
     */
    public static class SyncResult {
        @SerializedName("success")
        private boolean success;
        
        @SerializedName("adsSynced")
        private int adsSynced;
        
        @SerializedName("storesSynced")
        private int storesSynced;
        
        @SerializedName("lastSyncTime")
        private Date lastSyncTime;

        public boolean isSuccess() {
            return success;
        }

        public int getAdsSynced() {
            return adsSynced;
        }

        public int getStoresSynced() {
            return storesSynced;
        }

        public Date getLastSyncTime() {
            return lastSyncTime;
        }
    }

    /**
     * Credit opportunities
     */
    public static class CreditOpportunities {
        @SerializedName("ads")
        private OpportunityCategory ads;
        
        @SerializedName("stores")
        private OpportunityCategory stores;
        
        @SerializedName("totalPotentialEarnings")
        private int totalPotentialEarnings;

        public OpportunityCategory getAds() {
            return ads;
        }

        public OpportunityCategory getStores() {
            return stores;
        }

        public int getTotalPotentialEarnings() {
            return totalPotentialEarnings;
        }
    }

    /**
     * Opportunity category
     */
    public static class OpportunityCategory {
        @SerializedName("available")
        private int available;
        
        @SerializedName("potentialEarnings")
        private int potentialEarnings;
        
        @SerializedName("items")
        private List<OpportunityItem> items;

        public int getAvailable() {
            return available;
        }

        public int getPotentialEarnings() {
            return potentialEarnings;
        }

        public List<OpportunityItem> getItems() {
            return items;
        }
    }

    /**
     * Opportunity item
     */
    public static class OpportunityItem {
        @SerializedName("id")
        private String id;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("type")
        private String type;
        
        @SerializedName("reward")
        private int reward;
        
        @SerializedName("cost")
        private int cost;
        
        @SerializedName("category")
        private String category;
        
        @SerializedName("distance")
        private Integer distance;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public int getReward() {
            return reward;
        }

        public int getCost() {
            return cost;
        }

        public String getCategory() {
            return category;
        }

        public Integer getDistance() {
            return distance;
        }
    }

    /**
     * Location history
     */
    public static class LocationHistory {
        @SerializedName("currentLocation")
        private LocationData currentLocation;
        
        @SerializedName("history")
        private List<LocationEntry> history;

        public LocationData getCurrentLocation() {
            return currentLocation;
        }

        public List<LocationEntry> getHistory() {
            return history;
        }
    }

    /**
     * Location entry
     */
    public static class LocationEntry {
        @SerializedName("type")
        private String type;
        
        @SerializedName("store")
        private Store store;
        
        @SerializedName("enteredAt")
        private Date enteredAt;
        
        @SerializedName("returned")
        private boolean returned;
        
        @SerializedName("creditsDeducted")
        private int creditsDeducted;
        
        @SerializedName("location")
        private LocationData location;

        public String getType() {
            return type;
        }

        public Store getStore() {
            return store;
        }

        public Date getEnteredAt() {
            return enteredAt;
        }

        public boolean isReturned() {
            return returned;
        }

        public int getCreditsDeducted() {
            return creditsDeducted;
        }

        public LocationData getLocation() {
            return location;
        }
    }

    /**
     * Dashboard data
     */
    public static class DashboardData {
        @SerializedName("user")
        private User user;
        
        @SerializedName("creditBalance")
        private CreditBalance creditBalance;
        
        @SerializedName("nearbyStores")
        private List<Store> nearbyStores;
        
        @SerializedName("availableAds")
        private List<Advertisement> availableAds;
        
        @SerializedName("creditStats")
        private CreditStats creditStats;
        
        @SerializedName("radiusEntries")
        private List<RadiusEntry> radiusEntries;
        
        @SerializedName("adCount")
        private int adCount;
        
        @SerializedName("storesInRadius")
        private int storesInRadius;

        public User getUser() {
            return user;
        }

        public CreditBalance getCreditBalance() {
            return creditBalance;
        }

        public List<Store> getNearbyStores() {
            return nearbyStores;
        }

        public List<Advertisement> getAvailableAds() {
            return availableAds;
        }

        public CreditStats getCreditStats() {
            return creditStats;
        }

        public List<RadiusEntry> getRadiusEntries() {
            return radiusEntries;
        }

        public int getAdCount() {
            return adCount;
        }

        public int getStoresInRadius() {
            return storesInRadius;
        }
    }

    /**
     * Radius entry
     */
    public static class RadiusEntry {
        @SerializedName("storeId")
        private Store storeId;
        
        @SerializedName("enteredAt")
        private Date enteredAt;
        
        @SerializedName("creditsDeducted")
        private int creditsDeducted;
        
        @SerializedName("returned")
        private boolean returned;

        public Store getStoreId() {
            return storeId;
        }

        public Date getEnteredAt() {
            return enteredAt;
        }

        public int getCreditsDeducted() {
            return creditsDeducted;
        }

        public boolean isReturned() {
            return returned;
        }
    }

    /**
     * User preferences
     */
    public static class UserPreferences {
        @SerializedName("notifications")
        private boolean notifications;
        
        @SerializedName("autoPlayVideos")
        private boolean autoPlayVideos;
        
        @SerializedName("maxRadius")
        private int maxRadius;

        public boolean isNotifications() {
            return notifications;
        }

        public boolean isAutoPlayVideos() {
            return autoPlayVideos;
        }

        public int getMaxRadius() {
            return maxRadius;
        }
    }

    /**
     * Health status
     */
    public static class HealthStatus {
        @SerializedName("status")
        private String status;
        
        @SerializedName("timestamp")
        private Date timestamp;
        
        @SerializedName("version")
        private String version;

        public String getStatus() {
            return status;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getVersion() {
            return version;
        }
    }
}
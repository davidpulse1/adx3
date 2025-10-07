package com.adx.integration.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.adx.integration.data.converter.DateTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Credit transaction entity for tracking all credit movements
 */
@Entity(tableName = "credit_transactions")
@TypeConverters(DateTypeConverter.class)
public class CreditTransaction {
    
    @PrimaryKey
    @SerializedName("transactionId")
    private String transactionId;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("type")
    private String type; // earn, deduct, refund, purchase
    
    @SerializedName("amount")
    private int amount;
    
    @SerializedName("balanceBefore")
    private int balanceBefore;
    
    @SerializedName("balanceAfter")
    private int balanceAfter;
    
    @SerializedName("reason")
    private String reason;
    
    @SerializedName("reference")
    private Reference reference;
    
    @SerializedName("metadata")
    private Metadata metadata;
    
    @SerializedName("status")
    private String status; // pending, completed, failed, cancelled
    
    @SerializedName("createdAt")
    private Date createdAt;

    // Constructors
    public CreditTransaction() {
        this.status = "completed";
        this.createdAt = new Date();
    }

    public CreditTransaction(String userId, String type, int amount, int balanceBefore, 
                           int balanceAfter, String reason) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.reason = reason;
        this.status = "completed";
        this.createdAt = new Date();
        generateTransactionId();
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(int balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = "TXN-" + System.currentTimeMillis() + "-" + 
                               (int)(Math.random() * 100000);
        }
    }

    public double getUsdValue() {
        return amount / 180.0; // $1 = 180 credits
    }

    public boolean isCreditOperation() {
        return "deduct".equals(type) || "earn".equals(type);
    }

    public boolean isPurchase() {
        return "purchase".equals(type);
    }

    public boolean isRefund() {
        return "refund".equals(type);
    }

    public String getDisplayAmount() {
        if (amount > 0) {
            return "+" + amount;
        } else {
            return String.valueOf(amount);
        }
    }

    public String getDisplayReason() {
        switch (reason) {
            case "ad_received":
                return "Ad Received";
            case "ad_clicked":
                return "Ad Clicked";
            case "store_visited":
                return "Store Visited";
            case "radius_timeout":
                return "Radius Timeout Refund";
            case "credit_purchase":
                return "Credit Purchase";
            case "bonus":
                return "Bonus Credits";
            case "correction":
                return "Credit Correction";
            case "refund":
                return "Refund";
            default:
                return reason;
        }
    }

    public String getDisplayType() {
        switch (type) {
            case "earn":
                return "Earned";
            case "deduct":
                return "Deducted";
            case "purchase":
                return "Purchased";
            case "refund":
                return "Refunded";
            default:
                return type;
        }
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isFailed() {
        return "failed".equals(status);
    }

    @Override
    public String toString() {
        return "CreditTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Inner classes for nested objects
    public static class Reference {
        private String adId;
        private String storeId;
        private String purchaseId;
        private String radiusEntryId;

        // Getters and Setters
        public String getAdId() { return adId; }
        public void setAdId(String adId) { this.adId = adId; }
        public String getStoreId() { return storeId; }
        public void setStoreId(String storeId) { this.storeId = storeId; }
        public String getPurchaseId() { return purchaseId; }
        public void setPurchaseId(String purchaseId) { this.purchaseId = purchaseId; }
        public String getRadiusEntryId() { return radiusEntryId; }
        public void setRadiusEntryId(String radiusEntryId) { this.radiusEntryId = radiusEntryId; }
    }

    public static class Metadata {
        private Location location;
        private Double distance;
        private Integer duration;
        private String paymentMethod;
        private Double transactionFee;

        // Getters and Setters
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public Double getTransactionFee() { return transactionFee; }
        public void setTransactionFee(Double transactionFee) { this.transactionFee = transactionFee; }
    }

    public static class Location {
        private Double latitude;
        private Double longitude;

        // Getters and Setters
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}
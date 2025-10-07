package com.adx.integration.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.adx.integration.data.converter.DateTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * User entity representing a GitHub authenticated user
 */
@Entity(tableName = "users")
@TypeConverters(DateTypeConverter.class)
public class User {
    
    @PrimaryKey
    @SerializedName("id")
    private String id;
    
    @SerializedName("githubId")
    private String githubId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("credits")
    private int credits;
    
    @SerializedName("totalEarned")
    private int totalEarned;
    
    @SerializedName("totalSpent")
    private int totalSpent;
    
    @SerializedName("latitude")
    private Double latitude;
    
    @SerializedName("longitude")
    private Double longitude;
    
    @SerializedName("lastLogin")
    private Date lastLogin;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("updatedAt")
    private Date updatedAt;
    
    @SerializedName("preferences")
    private UserPreferences preferences;

    // Constructors
    public User() {
        this.preferences = new UserPreferences();
    }

    public User(String id, String githubId, String username, String email) {
        this.id = id;
        this.githubId = githubId;
        this.username = username;
        this.email = email;
        this.credits = 0;
        this.totalEarned = 0;
        this.totalSpent = 0;
        this.preferences = new UserPreferences();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(int totalEarned) {
        this.totalEarned = totalEarned;
    }

    public int getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(int totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    // Utility methods
    public double getCreditValue() {
        return credits / 180.0; // $1 = 180 credits
    }

    public boolean hasSufficientCredits(int required) {
        return credits >= required;
    }

    public void addCredits(int amount) {
        this.credits += amount;
        this.totalEarned += amount;
    }

    public boolean deductCredits(int amount) {
        if (hasSufficientCredits(amount)) {
            this.credits -= amount;
            this.totalSpent += amount;
            return true;
        }
        return false;
    }

    public boolean isLocationValid() {
        return latitude != null && longitude != null &&
               latitude >= -90 && latitude <= 90 &&
               longitude >= -180 && longitude <= 180;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", credits=" + credits +
                ", location=" + (isLocationValid() ? latitude + "," + longitude : "invalid") +
                '}';
    }
}
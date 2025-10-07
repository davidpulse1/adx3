package com.adx.integration.data.model;

import androidx.room.Embedded;

/**
 * User preferences for customization
 */
@Embedded
public class UserPreferences {
    
    private boolean notificationsEnabled;
    private boolean autoPlayVideos;
    private int maxRadius; // in meters
    private boolean locationSharingEnabled;
    private String preferredLanguage;
    private String theme; // light, dark, system

    public UserPreferences() {
        this.notificationsEnabled = true;
        this.autoPlayVideos = true;
        this.maxRadius = 1000; // 1km default
        this.locationSharingEnabled = true;
        this.preferredLanguage = "en";
        this.theme = "system";
    }

    // Getters and Setters
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isAutoPlayVideos() {
        return autoPlayVideos;
    }

    public void setAutoPlayVideos(boolean autoPlayVideos) {
        this.autoPlayVideos = autoPlayVideos;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    public boolean isLocationSharingEnabled() {
        return locationSharingEnabled;
    }

    public void setLocationSharingEnabled(boolean locationSharingEnabled) {
        this.locationSharingEnabled = locationSharingEnabled;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    // Utility methods
    public boolean isValidRadius() {
        return maxRadius >= 100 && maxRadius <= 10000; // 100m to 10km
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "notificationsEnabled=" + notificationsEnabled +
                ", autoPlayVideos=" + autoPlayVideos +
                ", maxRadius=" + maxRadius +
                ", locationSharingEnabled=" + locationSharingEnabled +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", theme='" + theme + '\'' +
                '}';
    }
}
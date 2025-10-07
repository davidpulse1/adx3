package com.adx.integration.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.adx.integration.data.converter.DateTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Store entity representing physical stores with ads
 */
@Entity(tableName = "stores")
@TypeConverters(DateTypeConverter.class)
public class Store {
    
    @PrimaryKey
    @SerializedName("id")
    private String id;
    
    @SerializedName("storeId")
    private String storeId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("location")
    private Location location;
    
    @SerializedName("contact")
    private Contact contact;
    
    @SerializedName("hours")
    private BusinessHours hours;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("tags")
    private List<String> tags;
    
    @SerializedName("ads")
    private List<String> ads;
    
    @SerializedName("radiusSettings")
    private RadiusSettings radiusSettings;
    
    @SerializedName("stats")
    private Stats stats;
    
    @SerializedName("images")
    private List<StoreImage> images;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("updatedAt")
    private Date updatedAt;

    // Transient fields (not stored in database)
    private double distance; // Distance from user in meters
    private boolean isInRadius;
    private Date enteredAt;
    private long timeInRadius;

    // Constructors
    public Store() {
        this.radiusSettings = new RadiusSettings();
        this.stats = new Stats();
        this.isActive = true;
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public BusinessHours getHours() {
        return hours;
    }

    public void setHours(BusinessHours hours) {
        this.hours = hours;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getAds() {
        return ads;
    }

    public void setAds(List<String> ads) {
        this.ads = ads;
    }

    public RadiusSettings getRadiusSettings() {
        return radiusSettings;
    }

    public void setRadiusSettings(RadiusSettings radiusSettings) {
        this.radiusSettings = radiusSettings;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<StoreImage> getImages() {
        return images;
    }

    public void setImages(List<StoreImage> images) {
        this.images = images;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isInRadius() {
        return isInRadius;
    }

    public void setInRadius(boolean inRadius) {
        isInRadius = inRadius;
    }

    public Date getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Date enteredAt) {
        this.enteredAt = enteredAt;
    }

    public long getTimeInRadius() {
        return timeInRadius;
    }

    public void setTimeInRadius(long timeInRadius) {
        this.timeInRadius = timeInRadius;
    }

    // Utility methods
    public boolean isLocationValid() {
        return location != null && location.getCoordinates() != null &&
               location.getCoordinates().isValid();
    }

    public double getLatitude() {
        return location != null && location.getCoordinates() != null ? 
               location.getCoordinates().getLatitude() : 0.0;
    }

    public double getLongitude() {
        return location != null && location.getCoordinates() != null ? 
               location.getCoordinates().getLongitude() : 0.0;
    }

    public int getRadius() {
        return radiusSettings != null ? radiusSettings.getRadius() : 10;
    }

    public boolean isRadiusEnabled() {
        return radiusSettings != null && radiusSettings.isEnabled();
    }

    public int getCreditDeduction() {
        return radiusSettings != null ? radiusSettings.getCreditDeduction() : 3;
    }

    public boolean isOpen() {
        if (hours == null) return true;
        
        // Simple implementation - would need proper timezone handling in production
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        String dayName = getDayName(dayOfWeek);
        
        BusinessHours.DayHours dayHours = hours.getDayHours(dayName);
        if (dayHours == null || dayHours.isClosed()) return false;
        
        // This is a simplified check - production would need proper time parsing
        return true;
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case java.util.Calendar.MONDAY: return "monday";
            case java.util.Calendar.TUESDAY: return "tuesday";
            case java.util.Calendar.WEDNESDAY: return "wednesday";
            case java.util.Calendar.THURSDAY: return "thursday";
            case java.util.Calendar.FRIDAY: return "friday";
            case java.util.Calendar.SATURDAY: return "saturday";
            case java.util.Calendar.SUNDAY: return "sunday";
            default: return "monday";
        }
    }

    public String getDisplayDistance() {
        if (distance < 1000) {
            return String.format("%.0f m", distance);
        } else {
            return String.format("%.1f km", distance / 1000.0);
        }
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId='" + storeId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", distance=" + getDisplayDistance() +
                ", inRadius=" + isInRadius +
                '}';
    }

    // Inner classes for nested objects
    public static class Location {
        private String address;
        private Coordinates coordinates;
        private String timezone;

        // Getters and Setters
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Coordinates getCoordinates() { return coordinates; }
        public void setCoordinates(Cordinates coordinates) { this.coordinates = coordinates; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class Coordinates {
        private double latitude;
        private double longitude;

        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public boolean isValid() {
            return latitude >= -90 && latitude <= 90 &&
                   longitude >= -180 && longitude <= 180;
        }
    }

    public static class Contact {
        private String phone;
        private String email;
        private String website;

        // Getters and Setters
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
    }

    public static class BusinessHours {
        private DayHours monday;
        private DayHours tuesday;
        private DayHours wednesday;
        private DayHours thursday;
        private DayHours friday;
        private DayHours saturday;
        private DayHours sunday;

        // Getters and Setters
        public DayHours getMonday() { return monday; }
        public void setMonday(DayHours monday) { this.monday = monday; }
        public DayHours getTuesday() { return tuesday; }
        public void setTuesday(DayHours tuesday) { this.tuesday = tuesday; }
        public DayHours getWednesday() { return wednesday; }
        public void setWednesday(DayHours wednesday) { this.wednesday = wednesday; }
        public DayHours getThursday() { return thursday; }
        public void setThursday(DayHours thursday) { this.thursday = thursday; }
        public DayHours getFriday() { return friday; }
        public void setFriday(DayHours friday) { this.friday = friday; }
        public DayHours getSaturday() { return saturday; }
        public void setSaturday(DayHours saturday) { this.saturday = saturday; }
        public DayHours getSunday() { return sunday; }
        public void setSunday(DayHours sunday) { this.sunday = sunday; }

        public DayHours getDayHours(String day) {
            switch (day.toLowerCase()) {
                case "monday": return monday;
                case "tuesday": return tuesday;
                case "wednesday": return wednesday;
                case "thursday": return thursday;
                case "friday": return friday;
                case "saturday": return saturday;
                case "sunday": return sunday;
                default: return null;
            }
        }
    }

    public static class DayHours {
        private String open;
        private String close;
        private boolean closed;

        // Getters and Setters
        public String getOpen() { return open; }
        public void setOpen(String open) { this.open = open; }
        public String getClose() { return close; }
        public void setClose(String close) { this.close = close; }
        public boolean isClosed() { return closed; }
        public void setClosed(boolean closed) { this.closed = closed; }
    }

    public static class RadiusSettings {
        private boolean enabled = true;
        private int radius = 10; // meters
        private int creditDeduction = 3;
        private int timeoutMinutes = 10;

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getRadius() { return radius; }
        public void setRadius(int radius) { this.radius = radius; }
        public int getCreditDeduction() { return creditDeduction; }
        public void setCreditDeduction(int creditDeduction) { this.creditDeduction = creditDeduction; }
        public int getTimeoutMinutes() { return timeoutMinutes; }
        public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    }

    public static class Stats {
        private int totalVisits = 0;
        private List<String> uniqueVisitors;
        private double averageVisitDuration = 0; // in minutes

        // Getters and Setters
        public int getTotalVisits() { return totalVisits; }
        public void setTotalVisits(int totalVisits) { this.totalVisits = totalVisits; }
        public List<String> getUniqueVisitors() { return uniqueVisitors; }
        public void setUniqueVisitors(List<String> uniqueVisitors) { this.uniqueVisitors = uniqueVisitors; }
        public double getAverageVisitDuration() { return averageVisitDuration; }
        public void setAverageVisitDuration(double averageVisitDuration) { this.averageVisitDuration = averageVisitDuration; }
    }

    public static class StoreImage {
        private String url;
        private String alt;
        private String type;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getAlt() { return alt; }
        public void setAlt(String alt) { this.alt = alt; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
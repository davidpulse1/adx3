package com.adx.integration.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.adx.integration.data.converter.DateTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Advertisement entity representing available ads
 */
@Entity(tableName = "advertisements")
@TypeConverters(DateTypeConverter.class)
public class Advertisement {
    
    @PrimaryKey
    @SerializedName("id")
    private String id;
    
    @SerializedName("adId")
    private String adId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("advertiser")
    private Advertiser advertiser;
    
    @SerializedName("media")
    private Media media;
    
    @SerializedName("creditCost")
    private CreditCost creditCost;
    
    @SerializedName("rewards")
    private Rewards rewards;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("priority")
    private int priority;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("schedule")
    private Schedule schedule;
    
    @SerializedName("stats")
    private Stats stats;
    
    @SerializedName("targeting")
    private Targeting targeting;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("updatedAt")
    private Date updatedAt;
    
    @SerializedName("isAvailable")
    private boolean isAvailable;

    // Constructors
    public Advertisement() {
        this.creditCost = new CreditCost();
        this.rewards = new Rewards();
        this.stats = new Stats();
        this.schedule = new Schedule();
        this.targeting = new Targeting();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(Advertiser advertiser) {
        this.advertiser = advertiser;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public CreditCost getCreditCost() {
        return creditCost;
    }

    public void setCreditCost(CreditCost creditCost) {
        this.creditCost = creditCost;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public void setRewards(Rewards rewards) {
        this.rewards = rewards;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Utility methods
    public int getReceiveCost() {
        return creditCost != null ? creditCost.getReceive() : 1;
    }

    public int getClickCost() {
        return creditCost != null ? creditCost.getClick() : 2;
    }

    public int getVisitCost() {
        return creditCost != null ? creditCost.getVisit() : 3;
    }

    public boolean hasVideoContent() {
        return media != null && "video".equals(media.getType()) && 
               media.getContent() != null && !media.getContent().isEmpty();
    }

    public boolean hasImageContent() {
        return media != null && "images".equals(media.getType()) && 
               media.getContent() != null && !media.getContent().isEmpty();
    }

    public String getFirstMediaUrl() {
        if (media != null && media.getContent() != null && !media.getContent().isEmpty()) {
            return media.getContent().get(0).getUrl();
        }
        return null;
    }

    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isWithinSchedule() {
        if (schedule == null) return false;
        Date now = new Date();
        return now.after(schedule.getStartDate()) && now.before(schedule.getEndDate());
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "adId='" + adId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", available=" + isAvailable +
                '}';
    }

    // Inner classes for nested objects
    public static class Advertiser {
        private String name;
        private String logo;
        private String website;
        private String storeId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getStoreId() { return storeId; }
        public void setStoreId(String storeId) { this.storeId = storeId; }
    }

    public static class Media {
        private String type;
        private List<MediaContent> content;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<MediaContent> getContent() { return content; }
        public void setContent(List<MediaContent> content) { this.content = content; }
    }

    public static class MediaContent {
        private String url;
        private String type;
        private Integer duration;
        private Size size;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public Size getSize() { return size; }
        public void setSize(Size size) { this.size = size; }
    }

    public static class Size {
        private Integer width;
        private Integer height;

        // Getters and Setters
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
    }

    public static class CreditCost {
        private int receive = 1;
        private int click = 2;
        private int visit = 3;

        // Getters and Setters
        public int getReceive() { return receive; }
        public void setReceive(int receive) { this.receive = receive; }
        public int getClick() { return click; }
        public void setClick(int click) { this.click = click; }
        public int getVisit() { return visit; }
        public void setVisit(int visit) { this.visit = visit; }
    }

    public static class Rewards {
        private int receive = 0;
        private int click = 0;
        private int visit = 0;

        // Getters and Setters
        public int getReceive() { return receive; }
        public void setReceive(int receive) { this.receive = receive; }
        public int getClick() { return click; }
        public void setClick(int click) { this.click = click; }
        public int getVisit() { return visit; }
        public void setVisit(int visit) { this.visit = visit; }
    }

    public static class Schedule {
        private Date startDate;
        private Date endDate;
        private String timezone = "UTC";

        // Getters and Setters
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class Stats {
        private int impressions = 0;
        private int clicks = 0;
        private int visits = 0;
        private List<String> uniqueUsers;

        // Getters and Setters
        public int getImpressions() { return impressions; }
        public void setImpressions(int impressions) { this.impressions = impressions; }
        public int getClicks() { return clicks; }
        public void setClicks(int clicks) { this.clicks = clicks; }
        public int getVisits() { return visits; }
        public void setVisits(int visits) { this.visits = visits; }
        public List<String> getUniqueUsers() { return uniqueUsers; }
        public void setUniqueUsers(List<String> uniqueUsers) { this.uniqueUsers = uniqueUsers; }
    }

    public static class Targeting {
        private List<Location> locations;
        private Demographics demographics;
        private Integer maxImpressions;
        private Integer maxClicks;

        // Getters and Setters
        public List<Location> getLocations() { return locations; }
        public void setLocations(List<Location> locations) { this.locations = locations; }
        public Demographics getDemographics() { return demographics; }
        public void setDemographics(Demographics demographics) { this.demographics = demographics; }
        public Integer getMaxImpressions() { return maxImpressions; }
        public void setMaxImpressions(Integer maxImpressions) { this.maxImpressions = maxImpressions; }
        public Integer getMaxClicks() { return maxClicks; }
        public void setMaxClicks(Integer maxClicks) { this.maxClicks = maxClicks; }
    }

    public static class Location {
        private double latitude;
        private double longitude;
        private int radius;

        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public int getRadius() { return radius; }
        public void setRadius(int radius) { this.radius = radius; }
    }

    public static class Demographics {
        private AgeRange ageRange;
        private String gender;
        private List<String> interests;

        // Getters and Setters
        public AgeRange getAgeRange() { return ageRange; }
        public void setAgeRange(AgeRange ageRange) { this.ageRange = ageRange; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public List<String> getInterests() { return interests; }
        public void setInterests(List<String> interests) { this.interests = interests; }
    }

    public static class AgeRange {
        private int min;
        private int max;

        // Getters and Setters
        public int getMin() { return min; }
        public void setMin(int min) { this.min = min; }
        public int getMax() { return max; }
        public void setMax(int max) { this.max = max; }
    }
}
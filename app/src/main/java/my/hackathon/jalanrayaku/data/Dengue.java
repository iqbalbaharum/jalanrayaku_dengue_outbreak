package my.hackathon.jalanrayaku.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MuhammadIqbal on 26/10/2016.
 */

public class Dengue {

    @SerializedName("_id")
    private String id;
    private int year;
    private int week;
    private String state;
    private String district;
    private String location;

    @SerializedName("total_case")
    private int totalCase;

    @SerializedName("outbreak_duration")
    private int outbreakDuration;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    @SerializedName("distance")
    private double distanceFromPoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalCase() {
        return totalCase;
    }

    public void setTotalCase(int totalCase) {
        this.totalCase = totalCase;
    }

    public int getOutbreakDuration() {
        return outbreakDuration;
    }

    public void setOutbreakDuration(int outbreakDuration) {
        this.outbreakDuration = outbreakDuration;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistanceFromPoint() {
        return distanceFromPoint;
    }

    public void setDistanceFromPoint(double distanceFromPoint) {
        this.distanceFromPoint = distanceFromPoint;
    }
}

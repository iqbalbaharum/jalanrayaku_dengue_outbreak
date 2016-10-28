package my.hackathon.jalanrayaku.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MuhammadIqbal on 27/10/2016.
 */

public class Weather {

    @SerializedName("temp_max")
    private int tempMax;

    @SerializedName("temp_mean")
    private int teamMean;

    @SerializedName("team_min")
    private int teamMin;

    private String events;

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getTeamMean() {
        return teamMean;
    }

    public void setTeamMean(int teamMean) {
        this.teamMean = teamMean;
    }

    public int getTeamMin() {
        return teamMin;
    }

    public void setTeamMin(int teamMin) {
        this.teamMin = teamMin;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }
}

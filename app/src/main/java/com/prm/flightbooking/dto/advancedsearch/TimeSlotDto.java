package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;

public class TimeSlotDto {
    @SerializedName("label")
    private String label;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("flightCount")
    private int flightCount;

    public TimeSlotDto() {
    }

    public TimeSlotDto(String label, String from, String to, int flightCount) {
        this.label = label;
        this.from = from;
        this.to = to;
        this.flightCount = flightCount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getFlightCount() {
        return flightCount;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }
}
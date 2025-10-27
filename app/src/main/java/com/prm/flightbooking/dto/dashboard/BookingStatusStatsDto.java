package com.prm.flightbooking.dto.dashboard;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class BookingStatusStatsDto {
    @SerializedName("status")
    private String status;

    @SerializedName("count")
    private int count;

    @SerializedName("percentage")
    private BigDecimal percentage;

    public BookingStatusStatsDto() {
    }

    public BookingStatusStatsDto(String status, int count, BigDecimal percentage) {
        this.status = status;
        this.count = count;
        this.percentage = percentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}

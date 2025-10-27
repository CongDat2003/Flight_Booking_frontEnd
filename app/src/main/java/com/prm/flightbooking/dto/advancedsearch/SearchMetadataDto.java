package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchMetadataDto {
    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("minPrice")
    private BigDecimal minPrice;

    @SerializedName("maxPrice")
    private BigDecimal maxPrice;

    @SerializedName("availableAirlines")
    private List<String> availableAirlines;

    @SerializedName("departureTimeSlots")
    private List<TimeSlotDto> departureTimeSlots;

    public SearchMetadataDto() {
        availableAirlines = new ArrayList<>();
        departureTimeSlots = new ArrayList<>();
    }

    public SearchMetadataDto(int totalResults, BigDecimal minPrice, BigDecimal maxPrice, List<String> availableAirlines, List<TimeSlotDto> departureTimeSlots) {
        this.totalResults = totalResults;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.availableAirlines = availableAirlines;
        this.departureTimeSlots = departureTimeSlots;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getAvailableAirlines() {
        return availableAirlines;
    }

    public void setAvailableAirlines(List<String> availableAirlines) {
        this.availableAirlines = availableAirlines != null ? availableAirlines : new ArrayList<>();
    }

    public List<TimeSlotDto> getDepartureTimeSlots() {
        return departureTimeSlots;
    }

    public void setDepartureTimeSlots(List<TimeSlotDto> departureTimeSlots) {
        this.departureTimeSlots = departureTimeSlots != null ? departureTimeSlots : new ArrayList<>();
    }
}

package com.prm.flightbooking.dto.airline;

import com.google.gson.annotations.SerializedName;

public class AirlineStatsDto {
    @SerializedName("airlineId")
    private int airlineId;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("totalFlights")
    private int totalFlights;

    @SerializedName("scheduledFlights")
    private int scheduledFlights;

    @SerializedName("completedFlights")
    private int completedFlights;

    @SerializedName("cancelledFlights")
    private int cancelledFlights;

    @SerializedName("averagePrice")
    private double averagePrice;

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public int getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(int totalFlights) {
        this.totalFlights = totalFlights;
    }

    public int getScheduledFlights() {
        return scheduledFlights;
    }

    public void setScheduledFlights(int scheduledFlights) {
        this.scheduledFlights = scheduledFlights;
    }

    public int getCompletedFlights() {
        return completedFlights;
    }

    public void setCompletedFlights(int completedFlights) {
        this.completedFlights = completedFlights;
    }

    public int getCancelledFlights() {
        return cancelledFlights;
    }

    public void setCancelledFlights(int cancelledFlights) {
        this.cancelledFlights = cancelledFlights;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
}

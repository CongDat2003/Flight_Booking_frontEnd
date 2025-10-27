package com.prm.flightbooking.dto.airport;

import com.google.gson.annotations.SerializedName;

public class AirportStatsDto {
    @SerializedName("airportId")
    private int airportId;

    @SerializedName("airportName")
    private String airportName;

    @SerializedName("city")
    private String city;

    @SerializedName("totalDepartureFlights")
    private int totalDepartureFlights;

    @SerializedName("totalArrivalFlights")
    private int totalArrivalFlights;

    @SerializedName("scheduledDepartures")
    private int scheduledDepartures;

    @SerializedName("scheduledArrivals")
    private int scheduledArrivals;

    @SerializedName("completedDepartures")
    private int completedDepartures;

    @SerializedName("completedArrivals")
    private int completedArrivals;

    @SerializedName("averageDeparturePrice")
    private double averageDeparturePrice;

    @SerializedName("averageArrivalPrice")
    private double averageArrivalPrice;

    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTotalDepartureFlights() {
        return totalDepartureFlights;
    }

    public void setTotalDepartureFlights(int totalDepartureFlights) {
        this.totalDepartureFlights = totalDepartureFlights;
    }

    public int getTotalArrivalFlights() {
        return totalArrivalFlights;
    }

    public void setTotalArrivalFlights(int totalArrivalFlights) {
        this.totalArrivalFlights = totalArrivalFlights;
    }

    public int getScheduledDepartures() {
        return scheduledDepartures;
    }

    public void setScheduledDepartures(int scheduledDepartures) {
        this.scheduledDepartures = scheduledDepartures;
    }

    public int getScheduledArrivals() {
        return scheduledArrivals;
    }

    public void setScheduledArrivals(int scheduledArrivals) {
        this.scheduledArrivals = scheduledArrivals;
    }

    public int getCompletedDepartures() {
        return completedDepartures;
    }

    public void setCompletedDepartures(int completedDepartures) {
        this.completedDepartures = completedDepartures;
    }

    public int getCompletedArrivals() {
        return completedArrivals;
    }

    public void setCompletedArrivals(int completedArrivals) {
        this.completedArrivals = completedArrivals;
    }

    public double getAverageDeparturePrice() {
        return averageDeparturePrice;
    }

    public void setAverageDeparturePrice(double averageDeparturePrice) {
        this.averageDeparturePrice = averageDeparturePrice;
    }

    public double getAverageArrivalPrice() {
        return averageArrivalPrice;
    }

    public void setAverageArrivalPrice(double averageArrivalPrice) {
        this.averageArrivalPrice = averageArrivalPrice;
    }
}

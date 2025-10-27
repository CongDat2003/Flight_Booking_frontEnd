package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Airline {
    @SerializedName("AirlineId")
    private int airlineId;

    @SerializedName("AirlineCode")
    private String airlineCode;

    @SerializedName("AirlineName")
    private String airlineName;

    @SerializedName("LogoUrl")
    private String logoUrl;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("Flights")
    private List<Flight> flights;

    public Airline() {
    }

    public Airline(int airlineId, String airlineCode, String airlineName, String logoUrl, Date createdAt, List<Flight> flights) {
        this.airlineId = airlineId;
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.logoUrl = logoUrl;
        this.createdAt = createdAt;
        this.flights = flights;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "airlineId=" + airlineId +
                ", airlineCode='" + airlineCode + '\'' +
                ", airlineName='" + airlineName + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", createdAt=" + createdAt +
                ", flights=" + flights +
                '}';
    }
}

package com.prm.flightbooking.dto.airline;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.dto.aircrafttype.FlightSummaryDto;

import java.util.Date;
import java.util.List;

public class AirlineDto {
    @SerializedName("airlineId")
    private int airlineId;

    @SerializedName("airlineCode")
    private String airlineCode;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("logoUrl")
    private String logoUrl;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("flights")
    private List<FlightSummaryDto> flights;

    public AirlineDto() {
    }

    public AirlineDto(int airlineId, String airlineCode, String airlineName, String logoUrl, Date createdAt, List<FlightSummaryDto> flights) {
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

    public List<FlightSummaryDto> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightSummaryDto> flights) {
        this.flights = flights;
    }
}

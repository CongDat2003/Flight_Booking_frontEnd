package com.prm.flightbooking.dto.airport;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.dto.aircrafttype.FlightSummaryDto;

import java.util.Date;
import java.util.List;

public class AirportDto {
    @SerializedName("airportId")
    private int airportId;

    @SerializedName("airportCode")
    private String airportCode;

    @SerializedName("airportName")
    private String airportName;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("departureFlights")
    private List<FlightSummaryDto> departureFlights;

    @SerializedName("arrivalFlights")
    private List<FlightSummaryDto> arrivalFlights;

    @SerializedName("totalDepartureFlights")
    private int totalDepartureFlights;

    @SerializedName("totalArrivalFlights")
    private int totalArrivalFlights;

    public AirportDto() {
    }

    public AirportDto(int airportId, String airportCode, String airportName, String city, String country, String timezone, Date createdAt, List<FlightSummaryDto> departureFlights, List<FlightSummaryDto> arrivalFlights, int totalDepartureFlights, int totalArrivalFlights) {
        this.airportId = airportId;
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
        this.createdAt = createdAt;
        this.departureFlights = departureFlights;
        this.arrivalFlights = arrivalFlights;
        this.totalDepartureFlights = totalDepartureFlights;
        this.totalArrivalFlights = totalArrivalFlights;
    }

    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<FlightSummaryDto> getDepartureFlights() {
        return departureFlights;
    }

    public void setDepartureFlights(List<FlightSummaryDto> departureFlights) {
        this.departureFlights = departureFlights;
    }

    public List<FlightSummaryDto> getArrivalFlights() {
        return arrivalFlights;
    }

    public void setArrivalFlights(List<FlightSummaryDto> arrivalFlights) {
        this.arrivalFlights = arrivalFlights;
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
}

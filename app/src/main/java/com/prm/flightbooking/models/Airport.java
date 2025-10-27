package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Airport {
    @SerializedName("AirportId")
    private int airportId;

    @SerializedName("AirportCode")
    private String airportCode;

    @SerializedName("AirportName")
    private String airportName;

    @SerializedName("City")
    private String city;

    @SerializedName("Country")
    private String country;

    @SerializedName("Timezone")
    private String timezone;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("FlightArrivalAirports")
    private List<Flight> flightArrivalAirports;

    @SerializedName("FlightDepartureAirports")
    private List<Flight> flightDepartureAirports;

    public Airport() {
    }

    public Airport(int airportId, String airportCode, String airportName, String city, String country, String timezone, Date createdAt, List<Flight> flightArrivalAirports, List<Flight> flightDepartureAirports) {
        this.airportId = airportId;
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
        this.createdAt = createdAt;
        this.flightArrivalAirports = flightArrivalAirports;
        this.flightDepartureAirports = flightDepartureAirports;
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

    public List<Flight> getFlightArrivalAirports() {
        return flightArrivalAirports;
    }

    public void setFlightArrivalAirports(List<Flight> flightArrivalAirports) {
        this.flightArrivalAirports = flightArrivalAirports;
    }

    public List<Flight> getFlightDepartureAirports() {
        return flightDepartureAirports;
    }

    public void setFlightDepartureAirports(List<Flight> flightDepartureAirports) {
        this.flightDepartureAirports = flightDepartureAirports;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportId=" + airportId +
                ", airportCode='" + airportCode + '\'' +
                ", airportName='" + airportName + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", timezone='" + timezone + '\'' +
                ", createdAt=" + createdAt +
                ", flightArrivalAirports=" + flightArrivalAirports +
                ", flightDepartureAirports=" + flightDepartureAirports +
                '}';
    }
}

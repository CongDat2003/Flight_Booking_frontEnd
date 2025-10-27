package com.prm.flightbooking.dto.airport;

import com.google.gson.annotations.SerializedName;

public class CreateAirportDto {
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

    public CreateAirportDto() {
    }

    public CreateAirportDto(String airportCode, String airportName, String city, String country, String timezone) {
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
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
}

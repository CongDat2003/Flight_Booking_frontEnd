package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FlightSearchDto {
    @SerializedName("departureAirportCode")
    private String departureAirportCode;

    @SerializedName("arrivalAirportCode")
    private String arrivalAirportCode;

    @SerializedName("departureDate")
    private Date departureDate;

    @SerializedName("passengers")
    private Integer passengers = 1;

    @SerializedName("seatClass")
    private String seatClass;

    public FlightSearchDto() {
    }

    public FlightSearchDto(String departureAirportCode, String arrivalAirportCode, Date departureDate, Integer passengers, String seatClass) {
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.departureDate = departureDate;
        this.passengers = passengers;
        this.seatClass = seatClass;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }
}

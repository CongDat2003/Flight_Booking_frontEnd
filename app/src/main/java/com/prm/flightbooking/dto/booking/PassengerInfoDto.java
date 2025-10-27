package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PassengerInfoDto implements Serializable {
    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("passengerIdNumber")
    private String passengerIdNumber;

    public PassengerInfoDto() {
    }

    public PassengerInfoDto(String passengerName, String passengerIdNumber) {
        this.passengerName = passengerName;
        this.passengerIdNumber = passengerIdNumber;
    }

    // Getters and Setters
    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerIdNumber() {
        return passengerIdNumber;
    }

    public void setPassengerIdNumber(String passengerIdNumber) {
        this.passengerIdNumber = passengerIdNumber;
    }
}

package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

public class BookingSeatDto {
    @SerializedName("seatId")
    private int seatId;

    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("passengerIdNumber")
    private String passengerIdNumber;

    public BookingSeatDto() {
    }

    public BookingSeatDto(int seatId, String passengerName, String passengerIdNumber) {
        this.seatId = seatId;
        this.passengerName = passengerName;
        this.passengerIdNumber = passengerIdNumber;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

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

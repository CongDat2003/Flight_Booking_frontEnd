package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class AdminBookingSeatDto {
    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("seatClass")
    private String seatClass;

    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("passengerIdNumber")
    private String passengerIdNumber;

    @SerializedName("seatPrice")
    private BigDecimal seatPrice;

    public AdminBookingSeatDto() {
    }

    public AdminBookingSeatDto(String seatNumber, String seatClass, String passengerName, String passengerIdNumber, BigDecimal seatPrice) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.passengerName = passengerName;
        this.passengerIdNumber = passengerIdNumber;
        this.seatPrice = seatPrice;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
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

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }
}

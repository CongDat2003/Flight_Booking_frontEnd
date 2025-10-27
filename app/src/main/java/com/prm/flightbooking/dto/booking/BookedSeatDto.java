package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class BookedSeatDto {
    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("seatClassName")
    private String seatClassName;

    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("seatPrice")
    private BigDecimal seatPrice;

    public BookedSeatDto() {
    }

    public BookedSeatDto(String seatNumber, String seatClassName, String passengerName, BigDecimal seatPrice) {
        this.seatNumber = seatNumber;
        this.seatClassName = seatClassName;
        this.passengerName = passengerName;
        this.seatPrice = seatPrice;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClassName() {
        return seatClassName;
    }

    public void setSeatClassName(String seatClassName) {
        this.seatClassName = seatClassName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }
}

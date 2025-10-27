package com.prm.flightbooking.dto.seat;

import java.io.Serializable;
import java.math.BigDecimal;

public class SelectedSeatInfo implements Serializable {
    private int seatId;
    private String seatNumber;
    private String seatClassName;
    private BigDecimal totalPrice;
    private String passengerName;
    private String passengerIdNumber;

    public SelectedSeatInfo(int seatId, String seatNumber, String seatClassName, BigDecimal totalPrice) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatClassName = seatClassName;
        this.totalPrice = totalPrice;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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

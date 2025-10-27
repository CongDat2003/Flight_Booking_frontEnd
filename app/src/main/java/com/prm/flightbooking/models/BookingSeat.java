package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class BookingSeat {
    @SerializedName("BookingSeatId")
    private int bookingSeatId;

    @SerializedName("BookingId")
    private int bookingId;

    @SerializedName("SeatId")
    private int seatId;

    @SerializedName("PassengerName")
    private String passengerName;

    @SerializedName("PassengerIdNumber")
    private String passengerIdNumber;

    @SerializedName("SeatPrice")
    private BigDecimal seatPrice;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("Booking")
    private Booking booking;

    @SerializedName("Seat")
    private Seat seat;

    public BookingSeat() {
    }

    public BookingSeat(int bookingSeatId, int bookingId, int seatId, String passengerName, String passengerIdNumber, BigDecimal seatPrice, Date createdAt, Booking booking, Seat seat) {
        this.bookingSeatId = bookingSeatId;
        this.bookingId = bookingId;
        this.seatId = seatId;
        this.passengerName = passengerName;
        this.passengerIdNumber = passengerIdNumber;
        this.seatPrice = seatPrice;
        this.createdAt = createdAt;
        this.booking = booking;
        this.seat = seat;
    }

    public int getBookingSeatId() {
        return bookingSeatId;
    }

    public void setBookingSeatId(int bookingSeatId) {
        this.bookingSeatId = bookingSeatId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "BookingSeat{" +
                "bookingSeatId=" + bookingSeatId +
                ", bookingId=" + bookingId +
                ", seatId=" + seatId +
                ", passengerName='" + passengerName + '\'' +
                ", passengerIdNumber='" + passengerIdNumber + '\'' +
                ", seatPrice=" + seatPrice +
                ", createdAt=" + createdAt +
                ", booking=" + booking +
                ", seat=" + seat +
                '}';
    }
}

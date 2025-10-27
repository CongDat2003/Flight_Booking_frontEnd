package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Booking {
    @SerializedName("BookingId")
    private int bookingId;

    @SerializedName("BookingReference")
    private String bookingReference;

    @SerializedName("UserId")
    private int userId;

    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("BookingStatus")
    private String bookingStatus;

    @SerializedName("TotalAmount")
    private BigDecimal totalAmount;

    @SerializedName("PaymentStatus")
    private String paymentStatus;

    @SerializedName("BookingDate")
    private Date bookingDate;

    @SerializedName("Notes")
    private String notes;

    @SerializedName("BookingSeats")
    private List<BookingSeat> bookingSeats;

    @SerializedName("Flight")
    private Flight flight;

    @SerializedName("User")
    private User user;

    public Booking() {
    }

    public Booking(int bookingId, String bookingReference, int userId, int flightId, String bookingStatus, BigDecimal totalAmount, String paymentStatus, Date bookingDate, String notes, List<BookingSeat> bookingSeats, Flight flight, User user) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingStatus = bookingStatus;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.notes = notes;
        this.bookingSeats = bookingSeats;
        this.flight = flight;
        this.user = user;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<BookingSeat> getBookingSeats() {
        return bookingSeats;
    }

    public void setBookingSeats(List<BookingSeat> bookingSeats) {
        this.bookingSeats = bookingSeats;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", bookingReference='" + bookingReference + '\'' +
                ", userId=" + userId +
                ", flightId=" + flightId +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", bookingDate=" + bookingDate +
                ", notes='" + notes + '\'' +
                ", bookingSeats=" + bookingSeats +
                ", flight=" + flight +
                ", user=" + user +
                '}';
    }
}

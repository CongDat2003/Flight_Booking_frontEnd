package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BookingDetailDto {
    @SerializedName("bookingId")
    private int bookingId;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("flight")
    private FlightDetailDto flight;

    @SerializedName("bookingStatus")
    private String bookingStatus;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("bookingDate")
    private Date bookingDate;

    @SerializedName("notes")
    private String notes;

    @SerializedName("passengers")
    private List<PassengerSeatDto> passengers;

    public BookingDetailDto() {
    }

    public BookingDetailDto(int bookingId, String bookingReference, FlightDetailDto flight, String bookingStatus, String paymentStatus, BigDecimal totalAmount, Date bookingDate, String notes, List<PassengerSeatDto> passengers) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.flight = flight;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.notes = notes;
        this.passengers = passengers;
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

    public FlightDetailDto getFlight() {
        return flight;
    }

    public void setFlight(FlightDetailDto flight) {
        this.flight = flight;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public List<PassengerSeatDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerSeatDto> passengers) {
        this.passengers = passengers;
    }
}

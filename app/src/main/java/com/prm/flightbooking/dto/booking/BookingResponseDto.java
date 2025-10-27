package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BookingResponseDto {
    @SerializedName("bookingId")
    private int bookingId;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("bookingStatus")
    private String bookingStatus;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("bookingDate")
    private Date bookingDate;

    @SerializedName("flight")
    private FlightResponseDto flight;

    @SerializedName("seats")
    private List<BookedSeatDto> seats;

    public BookingResponseDto() {
    }

    public BookingResponseDto(int bookingId, String bookingReference, String bookingStatus, BigDecimal totalAmount, String paymentStatus, Date bookingDate, FlightResponseDto flight, List<BookedSeatDto> seats) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.bookingStatus = bookingStatus;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.flight = flight;
        this.seats = seats;
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

    public FlightResponseDto getFlight() {
        return flight;
    }

    public void setFlight(FlightResponseDto flight) {
        this.flight = flight;
    }

    public List<BookedSeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<BookedSeatDto> seats) {
        this.seats = seats;
    }
}

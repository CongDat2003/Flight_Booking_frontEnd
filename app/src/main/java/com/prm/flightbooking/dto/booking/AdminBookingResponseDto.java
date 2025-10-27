package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdminBookingResponseDto {
    @SerializedName("bookingId")
    private int bookingId;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("route")
    private String route;

    @SerializedName("flightDate")
    private Date flightDate;

    @SerializedName("bookingStatus")
    private String bookingStatus;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("bookingDate")
    private Date bookingDate;

    @SerializedName("passengerCount")
    private int passengerCount;

    @SerializedName("seats")
    private List<AdminBookingSeatDto> seats;

    public AdminBookingResponseDto() {
    }

    public AdminBookingResponseDto(int bookingId, String bookingReference, String userName, String userEmail, String flightNumber, String route, Date flightDate, String bookingStatus, String paymentStatus, BigDecimal totalAmount, Date bookingDate, int passengerCount, List<AdminBookingSeatDto> seats) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userName = userName;
        this.userEmail = userEmail;
        this.flightNumber = flightNumber;
        this.route = route;
        this.flightDate = flightDate;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.passengerCount = passengerCount;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
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

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public List<AdminBookingSeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<AdminBookingSeatDto> seats) {
        this.seats = seats;
    }
}

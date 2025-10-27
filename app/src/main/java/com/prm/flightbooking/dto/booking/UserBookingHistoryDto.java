package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class UserBookingHistoryDto {
    @SerializedName("bookingId")
    private int bookingId;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("route")
    private String route;

    @SerializedName("departureTime")
    private Date departureTime;

    @SerializedName("arrivalTime")
    private Date arrivalTime;

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

    @SerializedName("canCancel")
    private boolean canCancel;

    public UserBookingHistoryDto() {
    }

    public UserBookingHistoryDto(int bookingId, String bookingReference, String flightNumber, String airlineName, String route, Date departureTime, Date arrivalTime, String bookingStatus, String paymentStatus, BigDecimal totalAmount, Date bookingDate, int passengerCount, boolean canCancel) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.route = route;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.passengerCount = passengerCount;
        this.canCancel = canCancel;
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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }
}

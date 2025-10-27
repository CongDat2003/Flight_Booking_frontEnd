package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Seat {
    @SerializedName("SeatId")
    private int seatId;

    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("SeatNumber")
    private String seatNumber;

    @SerializedName("SeatRow")
    private int seatRow;

    @SerializedName("SeatColumn")
    private String seatColumn;

    @SerializedName("ClassId")
    private int classId;

    @SerializedName("IsWindow")
    private Boolean isWindow;

    @SerializedName("IsAisle")
    private Boolean isAisle;

    @SerializedName("IsEmergencyExit")
    private Boolean isEmergencyExit;

    @SerializedName("ExtraFee")
    private BigDecimal extraFee;

    @SerializedName("IsAvailable")
    private Boolean isAvailable;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("BookingSeats")
    private List<BookingSeat> bookingSeats;

    @SerializedName("Class")
    private SeatClass seatClass;

    @SerializedName("Flight")
    private Flight flight;

    public Seat() {
    }

    public Seat(int seatId, int flightId, String seatNumber, int seatRow, String seatColumn, int classId, Boolean isWindow, Boolean isAisle, Boolean isEmergencyExit, BigDecimal extraFee, Boolean isAvailable, Date createdAt, List<BookingSeat> bookingSeats, SeatClass seatClass, Flight flight) {
        this.seatId = seatId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.classId = classId;
        this.isWindow = isWindow;
        this.isAisle = isAisle;
        this.isEmergencyExit = isEmergencyExit;
        this.extraFee = extraFee;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.bookingSeats = bookingSeats;
        this.seatClass = seatClass;
        this.flight = flight;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public String getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(String seatColumn) {
        this.seatColumn = seatColumn;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Boolean getWindow() {
        return isWindow;
    }

    public void setWindow(Boolean window) {
        isWindow = window;
    }

    public Boolean getAisle() {
        return isAisle;
    }

    public void setAisle(Boolean aisle) {
        isAisle = aisle;
    }

    public Boolean getEmergencyExit() {
        return isEmergencyExit;
    }

    public void setEmergencyExit(Boolean emergencyExit) {
        isEmergencyExit = emergencyExit;
    }

    public BigDecimal getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(BigDecimal extraFee) {
        this.extraFee = extraFee;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<BookingSeat> getBookingSeats() {
        return bookingSeats;
    }

    public void setBookingSeats(List<BookingSeat> bookingSeats) {
        this.bookingSeats = bookingSeats;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", flightId=" + flightId +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatRow=" + seatRow +
                ", seatColumn='" + seatColumn + '\'' +
                ", classId=" + classId +
                ", isWindow=" + isWindow +
                ", isAisle=" + isAisle +
                ", isEmergencyExit=" + isEmergencyExit +
                ", extraFee=" + extraFee +
                ", isAvailable=" + isAvailable +
                ", createdAt=" + createdAt +
                ", bookingSeats=" + bookingSeats +
                ", seatClass=" + seatClass +
                ", flight=" + flight +
                '}';
    }
}

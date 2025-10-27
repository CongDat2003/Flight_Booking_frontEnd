package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.models.FlightInfo;

import java.math.BigDecimal;
import java.util.Date;

public class AdminFlightResponseDto implements FlightInfo {
    @SerializedName("flightId")
    private int flightId;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("aircraftModel")
    private String aircraftModel;

    @SerializedName("departureAirport")
    private String departureAirport;

    @SerializedName("arrivalAirport")
    private String arrivalAirport;

    @SerializedName("departureTime")
    private Date departureTime;

    @SerializedName("arrivalTime")
    private Date arrivalTime;

    @SerializedName("basePrice")
    private BigDecimal basePrice;

    @SerializedName("status")
    private String status;

    @SerializedName("gate")
    private String gate;

    @SerializedName("totalSeats")
    private int totalSeats;

    @SerializedName("bookedSeats")
    private int bookedSeats;

    @SerializedName("availableSeats")
    private int availableSeats;

    @SerializedName("revenue")
    private BigDecimal revenue;

    @SerializedName("createdAt")
    private Date createdAt;

    public AdminFlightResponseDto() {
    }

    public AdminFlightResponseDto(int flightId, String flightNumber, String airlineName, String aircraftModel,
                                  String departureAirport, String arrivalAirport, Date departureTime, Date arrivalTime,
                                  BigDecimal basePrice, String status, String gate, int totalSeats, int bookedSeats,
                                  int availableSeats, BigDecimal revenue, Date createdAt) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.aircraftModel = aircraftModel;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = status;
        this.gate = gate;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
        this.availableSeats = availableSeats;
        this.revenue = revenue;
        this.createdAt = createdAt;
    }

    @Override
    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    @Override
    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    @Override
    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    @Override
    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public double getBasePrice() {
        return basePrice != null ? basePrice.doubleValue() : 0.0;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
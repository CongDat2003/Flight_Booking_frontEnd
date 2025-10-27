package com.prm.flightbooking.dto.flight;

import com.google.gson.annotations.SerializedName;
import com.prm.flightbooking.models.FlightInfo;

import java.math.BigDecimal;
import java.util.Date;

public class FlightResponseDto implements FlightInfo {
    @SerializedName("flightId")
    private int flightId;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("airlineCode")
    private String airlineCode;

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

    @SerializedName("availableSeats")
    private int availableSeats;

    public FlightResponseDto() {
    }

    public FlightResponseDto(int flightId, String flightNumber, String airlineName, String airlineCode, String departureAirport, String arrivalAirport, Date departureTime, Date arrivalTime, BigDecimal basePrice, String status, String gate, int availableSeats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.airlineCode = airlineCode;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = status;
        this.gate = gate;
        this.availableSeats = availableSeats;
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

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
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

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
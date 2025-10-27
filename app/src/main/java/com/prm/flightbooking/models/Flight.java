package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Flight {
    @SerializedName("FlightId")
    private int flightId;

    @SerializedName("FlightNumber")
    private String flightNumber;

    @SerializedName("AirlineId")
    private int airlineId;

    @SerializedName("AircraftTypeId")
    private int aircraftTypeId;

    @SerializedName("DepartureAirportId")
    private int departureAirportId;

    @SerializedName("ArrivalAirportId")
    private int arrivalAirportId;

    @SerializedName("DepartureTime")
    private Date departureTime;

    @SerializedName("ArrivalTime")
    private Date arrivalTime;

    @SerializedName("BasePrice")
    private BigDecimal basePrice;

    @SerializedName("Status")
    private String status;

    @SerializedName("Gate")
    private String gate;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("AircraftType")
    private AircraftType aircraftType;

    @SerializedName("Airline")
    private Airline airline;

    @SerializedName("ArrivalAirport")
    private Airport arrivalAirport;

    @SerializedName("DepartureAirport")
    private Airport departureAirport;

    @SerializedName("Bookings")
    private List<Booking> bookings;

    @SerializedName("Seats")
    private List<Seat> seats;

    public Flight() {
    }

    public Flight(int flightId, String flightNumber, int airlineId, int aircraftTypeId, int departureAirportId, int arrivalAirportId, Date departureTime, Date arrivalTime, BigDecimal basePrice, String status, String gate, Date createdAt, AircraftType aircraftType, Airline airline, Airport arrivalAirport, Airport departureAirport, List<Booking> bookings, List<Seat> seats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineId = airlineId;
        this.aircraftTypeId = aircraftTypeId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.status = status;
        this.gate = gate;
        this.createdAt = createdAt;
        this.aircraftType = aircraftType;
        this.airline = airline;
        this.arrivalAirport = arrivalAirport;
        this.departureAirport = departureAirport;
        this.bookings = bookings;
        this.seats = seats;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public int getAircraftTypeId() {
        return aircraftTypeId;
    }

    public void setAircraftTypeId(int aircraftTypeId) {
        this.aircraftTypeId = aircraftTypeId;
    }

    public int getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(int departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public int getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(int arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightNumber='" + flightNumber + '\'' +
                ", airlineId=" + airlineId +
                ", aircraftTypeId=" + aircraftTypeId +
                ", departureAirportId=" + departureAirportId +
                ", arrivalAirportId=" + arrivalAirportId +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", basePrice=" + basePrice +
                ", status='" + status + '\'' +
                ", gate='" + gate + '\'' +
                ", createdAt=" + createdAt +
                ", aircraftType=" + aircraftType +
                ", airline=" + airline +
                ", arrivalAirport=" + arrivalAirport +
                ", departureAirport=" + departureAirport +
                ", bookings=" + bookings +
                ", seats=" + seats +
                '}';
    }
}

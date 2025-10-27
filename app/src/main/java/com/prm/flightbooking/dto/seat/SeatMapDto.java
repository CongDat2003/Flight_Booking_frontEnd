package com.prm.flightbooking.dto.seat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeatMapDto {
    @SerializedName("flightId")
    private int flightId;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("aircraftModel")
    private String aircraftModel;

    @SerializedName("seatMapLayout")
    private String seatMapLayout;

    @SerializedName("seats")
    private List<SeatDto> seats;

    public SeatMapDto() {
    }

    public SeatMapDto(int flightId, String flightNumber, String aircraftModel, String seatMapLayout, List<SeatDto> seats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.aircraftModel = aircraftModel;
        this.seatMapLayout = seatMapLayout;
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

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public String getSeatMapLayout() {
        return seatMapLayout;
    }

    public void setSeatMapLayout(String seatMapLayout) {
        this.seatMapLayout = seatMapLayout;
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seats = seats;
    }
}

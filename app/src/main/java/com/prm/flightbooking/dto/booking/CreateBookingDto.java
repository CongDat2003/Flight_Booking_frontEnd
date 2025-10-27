package com.prm.flightbooking.dto.booking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateBookingDto {
    @SerializedName("userId")
    private int userId;

    @SerializedName("flightId")
    private int flightId;

    @SerializedName("seatClassId")
    private int seatClassId;

    @SerializedName("passengers")
    private int passengers;

    @SerializedName("passengerDetails")
    private List<PassengerInfoDto> passengerDetails;

    @SerializedName("notes")
    private String notes;

    public CreateBookingDto() {
    }

    public CreateBookingDto(int userId, int flightId, int seatClassId, int passengers, List<PassengerInfoDto> passengerDetails, String notes) {
        this.userId = userId;
        this.flightId = flightId;
        this.seatClassId = seatClassId;
        this.passengers = passengers;
        this.passengerDetails = passengerDetails;
        this.notes = notes;
    }

    // Getters and Setters
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

    public int getSeatClassId() {
        return seatClassId;
    }

    public void setSeatClassId(int seatClassId) {
        this.seatClassId = seatClassId;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public List<PassengerInfoDto> getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails(List<PassengerInfoDto> passengerDetails) {
        this.passengerDetails = passengerDetails;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
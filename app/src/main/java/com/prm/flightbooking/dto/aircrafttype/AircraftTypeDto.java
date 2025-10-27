package com.prm.flightbooking.dto.aircrafttype;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AircraftTypeDto {
    @SerializedName("aircraftTypeId")
    private int aircraftTypeId;

    @SerializedName("aircraftModel")
    private String aircraftModel;

    @SerializedName("manufacturer")
    private String manufacturer;

    @SerializedName("totalSeats")
    private int totalSeats;

    @SerializedName("economySeats")
    private int economySeats;

    @SerializedName("businessSeats")
    private int businessSeats;

    @SerializedName("firstClassSeats")
    private int firstClassSeats;

    @SerializedName("seatMapLayout")
    private String seatMapLayout;

    @SerializedName("flights")
    private List<FlightSummaryDto> flights;

    public AircraftTypeDto() {
    }

    public AircraftTypeDto(int aircraftTypeId, String aircraftModel, String manufacturer, int totalSeats, int economySeats, int businessSeats, int firstClassSeats, String seatMapLayout, List<FlightSummaryDto> flights) {
        this.aircraftTypeId = aircraftTypeId;
        this.aircraftModel = aircraftModel;
        this.manufacturer = manufacturer;
        this.totalSeats = totalSeats;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.seatMapLayout = seatMapLayout;
        this.flights = flights;
    }

    public int getAircraftTypeId() {
        return aircraftTypeId;
    }

    public void setAircraftTypeId(int aircraftTypeId) {
        this.aircraftTypeId = aircraftTypeId;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(int economySeats) {
        this.economySeats = economySeats;
    }

    public int getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(int businessSeats) {
        this.businessSeats = businessSeats;
    }

    public int getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(int firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public String getSeatMapLayout() {
        return seatMapLayout;
    }

    public void setSeatMapLayout(String seatMapLayout) {
        this.seatMapLayout = seatMapLayout;
    }

    public List<FlightSummaryDto> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightSummaryDto> flights) {
        this.flights = flights;
    }
}

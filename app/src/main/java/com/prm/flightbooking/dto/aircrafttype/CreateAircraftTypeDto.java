package com.prm.flightbooking.dto.aircrafttype;

import com.google.gson.annotations.SerializedName;

public class CreateAircraftTypeDto {
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

    @SerializedName("FirstClassSeats")
    private int firstClassSeats;

    @SerializedName("seatMapLayout")
    private String seatMapLayout;

    public CreateAircraftTypeDto() {
    }

    public CreateAircraftTypeDto(String aircraftModel, String manufacturer, int totalSeats, int economySeats, int businessSeats, int firstClassSeats, String seatMapLayout) {
        this.aircraftModel = aircraftModel;
        this.manufacturer = manufacturer;
        this.totalSeats = totalSeats;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.seatMapLayout = seatMapLayout;
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
}

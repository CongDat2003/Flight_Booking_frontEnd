package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AircraftType {
    @SerializedName("AircraftTypeId")
    private int aircraftTypeId;

    @SerializedName("AircraftModel")
    private String aircraftModel;

    @SerializedName("Manufacturer")
    private String manufacturer;

    @SerializedName("TotalSeats")
    private int totalSeats;

    @SerializedName("EconomySeats")
    private int economySeats;

    @SerializedName("BusinessSeats")
    private int businessSeats;

    @SerializedName("FirstClassSeats")
    private int firstClassSeats;

    @SerializedName("SeatMapLayout")
    private String seatMapLayout;

    @SerializedName("Flights")
    private List<Flight> flights;

    public AircraftType() {
    }

    public AircraftType(int aircraftTypeId, String aircraftModel, String manufacturer, int totalSeats, int economySeats, int businessSeats, int firstClassSeats, String seatMapLayout, List<Flight> flights) {
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

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "AircraftType{" +
                "aircraftTypeId=" + aircraftTypeId +
                ", aircraftModel='" + aircraftModel + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", totalSeats=" + totalSeats +
                ", economySeats=" + economySeats +
                ", businessSeats=" + businessSeats +
                ", firstClassSeats=" + firstClassSeats +
                ", seatMapLayout='" + seatMapLayout + '\'' +
                ", flights=" + flights +
                '}';
    }
}

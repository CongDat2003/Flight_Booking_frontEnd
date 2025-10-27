package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdvancedFlightSearchDto {
    @SerializedName("departureAirportCode")
    private String departureAirportCode;

    @SerializedName("arrivalAirportCode")
    private String arrivalAirportCode;

    @SerializedName("departureDate")
    private Date departureDate;

    @SerializedName("returnDate")
    private Date returnDate;

    @SerializedName("passengers")
    private int passengers = 1;

    @SerializedName("seatClass")
    private String seatClass;

    @SerializedName("airlines")
    private List<String> airlines;

    @SerializedName("departureTimeFrom")
    private String departureTimeFrom; // dùng String định dạng "HH:mm:ss"

    @SerializedName("departureTimeTo")
    private String departureTimeTo; // dùng String định dạng "HH:mm:ss"

    @SerializedName("minPrice")
    private BigDecimal minPrice;

    @SerializedName("maxPrice")
    private BigDecimal maxPrice;

    @SerializedName("maxStops")
    private Integer maxStops = 0;

    @SerializedName("sortBy")
    private String sortBy = "PRICE"; // PRICE, DURATION, DEPARTURE_TIME

    @SerializedName("sortOrder")
    private String sortOrder = "ASC"; // ASC, DESC

    public AdvancedFlightSearchDto() {
    }

    public AdvancedFlightSearchDto(String departureAirportCode, String arrivalAirportCode, Date departureDate, Date returnDate, int passengers, String seatClass, List<String> airlines, String departureTimeFrom, String departureTimeTo, BigDecimal minPrice, BigDecimal maxPrice, Integer maxStops, String sortBy, String sortOrder) {
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.passengers = passengers;
        this.seatClass = seatClass;
        this.airlines = airlines;
        this.departureTimeFrom = departureTimeFrom;
        this.departureTimeTo = departureTimeTo;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.maxStops = maxStops;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public List<String> getAirlines() {
        return airlines;
    }

    public void setAirlines(List<String> airlines) {
        this.airlines = airlines;
    }

    public String getDepartureTimeFrom() {
        return departureTimeFrom;
    }

    public void setDepartureTimeFrom(String departureTimeFrom) {
        this.departureTimeFrom = departureTimeFrom;
    }

    public String getDepartureTimeTo() {
        return departureTimeTo;
    }

    public void setDepartureTimeTo(String departureTimeTo) {
        this.departureTimeTo = departureTimeTo;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMaxStops() {
        return maxStops;
    }

    public void setMaxStops(Integer maxStops) {
        this.maxStops = maxStops;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}

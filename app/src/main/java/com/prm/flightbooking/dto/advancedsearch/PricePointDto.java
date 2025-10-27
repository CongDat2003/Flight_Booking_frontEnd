package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class PricePointDto {
    @SerializedName("date")
    private Date date;

    @SerializedName("minPrice")
    private BigDecimal minPrice;

    @SerializedName("maxPrice")
    private BigDecimal maxPrice;

    @SerializedName("avgPrice")
    private BigDecimal avgPrice;

    @SerializedName("flightCount")
    private int flightCount;

    public PricePointDto() {
    }

    public PricePointDto(Date date, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal avgPrice, int flightCount) {
        this.date = date;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.avgPrice = avgPrice;
        this.flightCount = flightCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getFlightCount() {
        return flightCount;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }
}

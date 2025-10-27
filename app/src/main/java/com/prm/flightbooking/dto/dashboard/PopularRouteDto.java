package com.prm.flightbooking.dto.dashboard;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PopularRouteDto {
    @SerializedName("route")
    private String route;

    @SerializedName("bookingCount")
    private int bookingCount;

    @SerializedName("revenue")
    private BigDecimal revenue;

    public PopularRouteDto() {
    }

    public PopularRouteDto(String route, int bookingCount, BigDecimal revenue) {
        this.route = route;
        this.bookingCount = bookingCount;
        this.revenue = revenue;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}

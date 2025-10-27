package com.prm.flightbooking.dto.dashboard;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class RevenueByMonthDto {
    @SerializedName("year")
    private int year;

    @SerializedName("month")
    private int month;

    @SerializedName("revenue")
    private BigDecimal revenue;

    @SerializedName("bookingCount")
    private int bookingCount;

    public RevenueByMonthDto() {
    }

    public RevenueByMonthDto(int year, int month, BigDecimal revenue, int bookingCount) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
        this.bookingCount = bookingCount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }
}

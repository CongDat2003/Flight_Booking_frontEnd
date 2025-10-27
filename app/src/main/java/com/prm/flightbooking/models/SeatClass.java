package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class SeatClass {
    @SerializedName("ClassId")
    private int classId;

    @SerializedName("ClassName")
    private String className;

    @SerializedName("ClassDescription")
    private String classDescription;

    @SerializedName("PriceMultiplier")
    private BigDecimal priceMultiplier;

    @SerializedName("Seats")
    private List<Seat> seats;

    public SeatClass() {
    }

    public SeatClass(int classId, String className, String classDescription, BigDecimal priceMultiplier, List<Seat> seats) {
        this.classId = classId;
        this.className = className;
        this.classDescription = classDescription;
        this.priceMultiplier = priceMultiplier;
        this.seats = seats;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "SeatClass{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", classDescription='" + classDescription + '\'' +
                ", priceMultiplier=" + priceMultiplier +
                ", seats=" + seats +
                '}';
    }
}

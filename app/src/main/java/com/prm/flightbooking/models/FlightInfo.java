package com.prm.flightbooking.models;

import java.util.Date;

public interface FlightInfo {
    int getFlightId();
    String getFlightNumber();
    String getAirlineName();
    String getDepartureAirport();
    String getArrivalAirport();
    Date getDepartureTime();
    Date getArrivalTime();
    double getBasePrice();
    String getStatus();
}
package com.prm.flightbooking.dto.airline;

import com.google.gson.annotations.SerializedName;

public class CreateAirlineDto {
    @SerializedName("airlineCode")
    private String airlineCode;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("logoUrl")
    private String logoUrl;

    public CreateAirlineDto() {
    }

    public CreateAirlineDto(String airlineCode, String airlineName, String logoUrl) {
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.logoUrl = logoUrl;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

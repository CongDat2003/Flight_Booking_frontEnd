package com.prm.flightbooking.dto.advancedsearch;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PriceTrendDto {
    @SerializedName("route")
    private String route;

    @SerializedName("priceHistory")
    private List<PricePointDto> priceHistory;

    @SerializedName("recommendation")
    private String recommendation;

    public PriceTrendDto() {
        priceHistory = new ArrayList<>();
    }

    public PriceTrendDto(String route, List<PricePointDto> priceHistory, String recommendation) {
        this.route = route;
        this.priceHistory = priceHistory;
        this.recommendation = recommendation;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<PricePointDto> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PricePointDto> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

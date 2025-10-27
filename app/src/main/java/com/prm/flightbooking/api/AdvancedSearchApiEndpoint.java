package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.advancedsearch.AdvancedFlightSearchDto;
import com.prm.flightbooking.dto.advancedsearch.FlightSearchResultDto;
import com.prm.flightbooking.dto.advancedsearch.PriceTrendDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface AdvancedSearchApiEndpoint {
    @POST("AdvancedSearch/flights")
    Call<FlightSearchResultDto> advancedSearch(@Body AdvancedFlightSearchDto searchDto);

    @GET("AdvancedSearch/popular-destinations/{fromAirport}")
    Call<List<String>> getPopularDestinations(@Path("fromAirport") String fromAirport);

    @GET("AdvancedSearch/recommendations/{userId}")
    Call<List<FlightResponseDto>> getRecommendations(@Path("userId") int userId);

    @GET("AdvancedSearch/price-trend/{route}")
    Call<PriceTrendDto> getPriceTrend(@Path("route") String route, @Query("days") int days);
}

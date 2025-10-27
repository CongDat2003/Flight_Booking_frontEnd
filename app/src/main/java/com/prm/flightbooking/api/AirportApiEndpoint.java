package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.airport.AirportDto;
import com.prm.flightbooking.dto.airport.AirportStatsDto;
import com.prm.flightbooking.dto.airport.CreateAirportDto;
import com.prm.flightbooking.dto.airport.UpdateAirportDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AirportApiEndpoint {
    @GET("Airports")
    Call<List<AirportDto>> getAllAirports();

    @GET("Airports/{airportId}")
    Call<AirportDto> getAirportById(@Path("airportId") int airportId);

    @GET("Airports/search")
    Call<List<AirportDto>> searchAirports(@Query("query") String query);

    @GET("Airports/by-country/{country}")
    Call<List<AirportDto>> getAirportsByCountry(@Path("country") String country);

    @POST("Airports")
    Call<AirportDto> createAirport(@Body CreateAirportDto createDto);

    @PUT("Airports/{airportId}")
    Call<AirportDto> updateAirport(@Path("airportId") int airportId, @Body UpdateAirportDto updateDto);

    @DELETE("Airports/{airportId}")
    Call<Void> deleteAirport(@Path("airportId") int airportId);

    @GET("Airports/{airportId}/stats")
    Call<AirportStatsDto> getAirportStats(@Path("airportId") int airportId);
}

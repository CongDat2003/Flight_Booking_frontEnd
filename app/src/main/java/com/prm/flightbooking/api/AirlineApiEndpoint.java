package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.airline.AirlineDto;
import com.prm.flightbooking.dto.airline.AirlineStatsDto;
import com.prm.flightbooking.dto.airline.CreateAirlineDto;
import com.prm.flightbooking.dto.airline.UpdateAirlineDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface AirlineApiEndpoint {
    @GET("Airlines")
    Call<List<AirlineDto>> getAllAirlines();

    @GET("Airlines/{airlineId}")
    Call<AirlineDto> getAirlineById(@Path("airlineId") int airlineId);

    @POST("Airlines")
    Call<AirlineDto> createAirline(@Body CreateAirlineDto createDto);

    @PUT("Airlines/{airlineId}")
    Call<AirlineDto> updateAirline(@Path("airlineId") int airlineId, @Body UpdateAirlineDto updateDto);

    @DELETE("Airlines/{airlineId}")
    Call<Void> deleteAirline(@Path("airlineId") int airlineId);

    @GET("Airlines/{airlineId}/stats")
    Call<AirlineStatsDto> getAirlineStats(@Path("airlineId") int airlineId);
}

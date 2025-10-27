package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.flight.AdminFlightResponseDto;
import com.prm.flightbooking.dto.flight.CreateFlightDto;
import com.prm.flightbooking.dto.flight.FlightResponseDto;
import com.prm.flightbooking.dto.flight.FlightSearchDto;
import com.prm.flightbooking.dto.flight.UpdateFlightDto;
import com.prm.flightbooking.dto.seat.SeatMapDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FlightApiEndpoint {
    @POST("Flights/search")
    Call<List<FlightResponseDto>> searchFlights(@Body FlightSearchDto searchDto);

    @GET("Flights/{flightId}/seats")
    Call<SeatMapDto> getSeatMap(@Path("flightId") int flightId, @Query("userId") int userId);

    @GET("admin/Flights")
    Call<List<AdminFlightResponseDto>> getAllFlights(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("admin/Flights/{flightId}")
    Call<AdminFlightResponseDto> getFlightById(@Path("flightId") int flightId);

    @POST("admin/Flights")
    Call<AdminFlightResponseDto> createFlight(@Body CreateFlightDto flightDto);

    @PUT("admin/Flights/{flightId}")
    Call<AdminFlightResponseDto> updateFlight(@Path("flightId") int flightId, @Body UpdateFlightDto flightDto);

    @DELETE("admin/Flights/{flightId}")
    Call<Void> deleteFlight(@Path("flightId") int flightId);

    @POST("admin/Flights/{flightId}/generate-seats")
    Call<Void> generateSeats(@Path("flightId") int flightId);
}

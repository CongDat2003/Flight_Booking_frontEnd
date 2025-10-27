package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.aircrafttype.AircraftTypeDto;
import com.prm.flightbooking.dto.aircrafttype.CreateAircraftTypeDto;
import com.prm.flightbooking.dto.aircrafttype.UpdateAircraftTypeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AircraftTypeApiEndpoint {
    @GET("AircraftTypes")
    Call<List<AircraftTypeDto>> getAllAircraftTypes();

    @GET("AircraftTypes/{id}")
    Call<AircraftTypeDto> getAircraftTypeById(@Path("id") int id);

    @POST("AircraftTypes")
    Call<AircraftTypeDto> createAircraftType(@Body CreateAircraftTypeDto dto);

    @PUT("AircraftTypes/{id}")
    Call<AircraftTypeDto> updateAircraftType(@Path("id") int id, @Body UpdateAircraftTypeDto dto);

    @DELETE("AircraftTypes/{id}")
    Call<Void> deleteAircraftType(@Path("id") int id);
}

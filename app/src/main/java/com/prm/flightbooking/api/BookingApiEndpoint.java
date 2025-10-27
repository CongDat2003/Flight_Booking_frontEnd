package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.booking.AdminBookingResponseDto;
import com.prm.flightbooking.dto.booking.BookingDetailDto;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.booking.UpdateBookingStatusDto;
import com.prm.flightbooking.dto.booking.UserBookingHistoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiEndpoint {
    @POST("Bookings")
    Call<BookingResponseDto> createBooking(@Body CreateBookingDto bookingDto);

    @GET("Bookings/user/{userId}")
    Call<List<BookingResponseDto>> getUserBookings(@Path("userId") int userId);

    @GET("user/Bookings/{userId}/history")
    Call<List<UserBookingHistoryDto>> getBookingHistory(@Path("userId") int userId, @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("user/Bookings/{userId}/{bookingId}")
    Call<BookingDetailDto> getBookingDetail(@Path("userId") int userId, @Path("bookingId") int bookingId);

    @POST("user/Bookings/{userId}/{bookingId}/cancel")
    Call<Void> cancelBookingUser(@Path("userId") int userId, @Path("bookingId") int bookingId);

    @GET("admin/Bookings")
    Call<List<AdminBookingResponseDto>> getAllBookings(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("admin/Bookings/{bookingId}")
    Call<AdminBookingResponseDto> getBookingById(@Path("bookingId") int bookingId);

    @PUT("admin/Bookings/{bookingId}/status")
    Call<AdminBookingResponseDto> updateBookingStatus(@Path("bookingId") int bookingId, @Body UpdateBookingStatusDto statusDto);

    @POST("admin/Bookings/{bookingId}/cancel")
    Call<Void> cancelBookingAdmin(@Path("bookingId") int bookingId);
}

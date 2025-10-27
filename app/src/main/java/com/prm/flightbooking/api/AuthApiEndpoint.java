package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.user.ChangePasswordDto;
import com.prm.flightbooking.dto.user.DeleteAccountDto;
import com.prm.flightbooking.dto.user.ForgotPasswordRequestDto;
import com.prm.flightbooking.dto.user.LoginDto;
import com.prm.flightbooking.dto.user.RegisterUserDto;
import com.prm.flightbooking.dto.user.ResetPasswordDto;
import com.prm.flightbooking.dto.user.UpdateProfileDto;
import com.prm.flightbooking.dto.user.UserProfileDto;
import com.prm.flightbooking.dto.user.VerifyOtpDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthApiEndpoint {
    @POST("Auth/register")
    Call<UserProfileDto> register(@Body RegisterUserDto registerDto);

    @POST("Auth/login")
    Call<UserProfileDto> login(@Body LoginDto loginDto);

    @GET("Auth/profile/{userId}")
    Call<UserProfileDto> getProfile(@Path("userId") int userId);

    @PUT("Auth/profile/{userId}")
    Call<UserProfileDto> updateProfile(@Path("userId") int userId, @Body UpdateProfileDto updateDto);

    @POST("Auth/change-password/{userId}")
    Call<Map<String, String>> changePassword(@Path("userId") int userId, @Body ChangePasswordDto passwordDto);

    @POST("Auth/delete-account/{userId}")
    Call<Map<String, String>> deleteAccount(@Path("userId") int userId, @Body DeleteAccountDto deleteDto);

    @POST("Auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Body ForgotPasswordRequestDto forgotPasswordRequestDto);

    @POST("Auth/verify-otp")
    Call<Map<String, String>> verifyOtp(@Body VerifyOtpDto verifyOtpDto);

    @POST("Auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body ResetPasswordDto resetPasswordDto);
}

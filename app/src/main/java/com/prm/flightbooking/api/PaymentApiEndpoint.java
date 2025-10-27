package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.payment.CreatePaymentDto;
import com.prm.flightbooking.dto.payment.PaymentResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PaymentApiEndpoint {
    
    @POST("api/payment/create")
    Call<PaymentResponseDto> createPayment(@Body CreatePaymentDto paymentDto);
    
    @GET("api/payment/status/{transactionId}")
    Call<PaymentResponseDto> getPaymentStatus(@Path("transactionId") String transactionId);
    
    @POST("api/payment/callback")
    Call<PaymentResponseDto> paymentCallback(@Body Object callbackData);
}



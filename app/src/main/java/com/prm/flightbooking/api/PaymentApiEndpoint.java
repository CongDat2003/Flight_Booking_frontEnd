package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.payment.CreatePaymentDto;
import com.prm.flightbooking.dto.payment.PaymentResponseDto;
import com.prm.flightbooking.dto.payment.UpdatePaymentDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PaymentApiEndpoint {
    
    @POST("payment/create")
    Call<PaymentResponseDto> createPayment(@Body CreatePaymentDto paymentDto);
    
    @GET("payment/status/{transactionId}")
    Call<PaymentResponseDto> getPaymentStatus(@Path("transactionId") String transactionId);
    
    @POST("payment/callback")
    Call<PaymentResponseDto> paymentCallback(@Body Object callbackData);
    
    @PUT("payment/{paymentId}")
    Call<PaymentResponseDto> updatePayment(@Path("paymentId") int paymentId, @Body UpdatePaymentDto dto);
}



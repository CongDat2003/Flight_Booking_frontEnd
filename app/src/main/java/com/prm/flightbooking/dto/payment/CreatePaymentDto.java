package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

public class CreatePaymentDto {
    @SerializedName("BookingId")
    private int bookingId;
    
    @SerializedName("PaymentMethod")
    private String paymentMethod;
    
    @SerializedName("ReturnUrl")
    private String returnUrl;
    
    @SerializedName("CancelUrl")
    private String cancelUrl;

    @SerializedName("BankCode")
    private String bankCode;

    public CreatePaymentDto() {}

    public CreatePaymentDto(int bookingId, String paymentMethod, String returnUrl, String cancelUrl) {
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}



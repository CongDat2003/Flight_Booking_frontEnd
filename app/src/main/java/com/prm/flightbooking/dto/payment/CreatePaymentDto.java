package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

public class CreatePaymentDto {
    @SerializedName("bookingId")
    private int bookingId;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("returnUrl")
    private String returnUrl;
    
    @SerializedName("cancelUrl")
    private String cancelUrl;

    @SerializedName("bankCode")
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



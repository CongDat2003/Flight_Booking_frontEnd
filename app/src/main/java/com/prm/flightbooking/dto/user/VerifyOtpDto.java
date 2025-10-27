package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpDto {
    @SerializedName("email")
    private String email;

    @SerializedName("otpCode")
    private String otpCode;

    public VerifyOtpDto(){

    }

    public VerifyOtpDto(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}

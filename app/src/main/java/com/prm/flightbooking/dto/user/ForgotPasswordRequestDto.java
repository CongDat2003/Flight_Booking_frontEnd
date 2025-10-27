package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequestDto {
    @SerializedName("email")
    private String email;

    public ForgotPasswordRequestDto(){

    }

    public ForgotPasswordRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

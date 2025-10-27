package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountDto {
    @SerializedName("password")
    private String password;

    public  DeleteAccountDto(){

    }

    public DeleteAccountDto(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

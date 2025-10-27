package com.prm.flightbooking.dto.user;

import com.google.gson.annotations.SerializedName;

public class UpdateUserStatusDto {
    @SerializedName("isActive")
    private boolean isActive;

    public UpdateUserStatusDto() {
    }

    public UpdateUserStatusDto(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

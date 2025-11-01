package com.prm.flightbooking.dto.chat;

import com.google.gson.annotations.SerializedName;

public class CreateMessageDto {
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("content")
    private String content;

    @SerializedName("senderType")
    private String senderType = "USER"; // USER, ADMIN

    public CreateMessageDto() {
    }

    public CreateMessageDto(Integer userId, String content) {
        this.userId = userId;
        this.content = content;
        this.senderType = "USER";
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }
}








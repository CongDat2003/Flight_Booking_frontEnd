package com.prm.flightbooking.dto.chat;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageDto {
    @SerializedName("messageId")
    private int messageId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("content")
    private String content;

    @SerializedName("senderType")
    private String senderType; // USER, ADMIN, SYSTEM

    @SerializedName("status")
    private String status; // SENT, READ

    @SerializedName("isAutoReply")
    private boolean isAutoReply;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("readAt")
    private Date readAt;

    public MessageDto() {
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAutoReply() {
        return isAutoReply;
    }

    public void setAutoReply(boolean autoReply) {
        isAutoReply = autoReply;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public boolean isFromUser() {
        return "USER".equals(senderType);
    }

    public boolean isFromAdmin() {
        return "ADMIN".equals(senderType);
    }

    public boolean isFromSystem() {
        return "SYSTEM".equals(senderType);
    }
}






































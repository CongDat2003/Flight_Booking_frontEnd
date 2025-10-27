package com.prm.flightbooking.dto.notify;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationDto {
    @SerializedName("notificationId")
    private int notificationId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("readAt")
    private Date readAt;

    @SerializedName("relatedBookingId")
    private Integer relatedBookingId;

    @SerializedName("relatedFlightId")
    private Integer relatedFlightId;

    public NotificationDto() {
    }

    public NotificationDto(int notificationId, String title, String message, String type, String status, Date createdAt, Date readAt, Integer relatedBookingId, Integer relatedFlightId) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.relatedBookingId = relatedBookingId;
        this.relatedFlightId = relatedFlightId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getRelatedBookingId() {
        return relatedBookingId;
    }

    public void setRelatedBookingId(Integer relatedBookingId) {
        this.relatedBookingId = relatedBookingId;
    }

    public Integer getRelatedFlightId() {
        return relatedFlightId;
    }

    public void setRelatedFlightId(Integer relatedFlightId) {
        this.relatedFlightId = relatedFlightId;
    }
}

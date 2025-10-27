package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {
    @SerializedName("NotificationId")
    private int notificationId;

    @SerializedName("UserId")
    private int userId;

    @SerializedName("Title")
    private String title;

    @SerializedName("Message")
    private String message;

    @SerializedName("Type")
    private String type; // BOOKING, FLIGHT_UPDATE, PAYMENT, REMINDER

    @SerializedName("Status")
    private String status; // UNREAD, READ

    @SerializedName("RelatedBookingId")
    private Integer relatedBookingId;

    @SerializedName("RelatedFlightId")
    private Integer relatedFlightId;

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("ReadAt")
    private Date readAt;

    @SerializedName("AdditionalData")
    private String additionalData;

    @SerializedName("User")
    private User user;

    @SerializedName("RelatedBooking")
    private Booking relatedBooking;

    @SerializedName("RelatedFlight")
    private Flight relatedFlight;

    public Notification() {
    }

    public Notification(int notificationId, int userId, String title, String message, String type, String status, Integer relatedBookingId, Integer relatedFlightId, Date createdAt, Date readAt, String additionalData, User user, Booking relatedBooking, Flight relatedFlight) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.relatedBookingId = relatedBookingId;
        this.relatedFlightId = relatedFlightId;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.additionalData = additionalData;
        this.user = user;
        this.relatedBooking = relatedBooking;
        this.relatedFlight = relatedFlight;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Booking getRelatedBooking() {
        return relatedBooking;
    }

    public void setRelatedBooking(Booking relatedBooking) {
        this.relatedBooking = relatedBooking;
    }

    public Flight getRelatedFlight() {
        return relatedFlight;
    }

    public void setRelatedFlight(Flight relatedFlight) {
        this.relatedFlight = relatedFlight;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", relatedBookingId=" + relatedBookingId +
                ", relatedFlightId=" + relatedFlightId +
                ", createdAt=" + createdAt +
                ", readAt=" + readAt +
                ", additionalData='" + additionalData + '\'' +
                ", user=" + user +
                ", relatedBooking=" + relatedBooking +
                ", relatedFlight=" + relatedFlight +
                '}';
    }
}

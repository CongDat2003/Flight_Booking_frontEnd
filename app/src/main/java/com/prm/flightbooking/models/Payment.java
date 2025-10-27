package com.prm.flightbooking.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class Payment {
    @SerializedName("PaymentId")
    private int paymentId;

    @SerializedName("BookingId")
    private int bookingId;

    @SerializedName("PaymentMethod")
    private String paymentMethod; // VNPAY, MOMO, ZALOPAY, CARD

    @SerializedName("TransactionId")
    private String transactionId;

    @SerializedName("Amount")
    private BigDecimal amount;

    @SerializedName("Status")
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED

    @SerializedName("CreatedAt")
    private Date createdAt;

    @SerializedName("ProcessedAt")
    private Date processedAt;

    @SerializedName("Notes")
    private String notes;

    @SerializedName("PaymentData")
    private String paymentData;

    @SerializedName("Booking")
    private Booking booking;

    public Payment() {
    }

    public Payment(int paymentId, int bookingId, String paymentMethod, String transactionId, BigDecimal amount, String status, Date createdAt, Date processedAt, String notes, String paymentData, Booking booking) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.notes = notes;
        this.paymentData = paymentData;
        this.booking = booking;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(String paymentData) {
        this.paymentData = paymentData;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", bookingId=" + bookingId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", notes='" + notes + '\'' +
                ", paymentData='" + paymentData + '\'' +
                ", booking=" + booking +
                '}';
    }
}

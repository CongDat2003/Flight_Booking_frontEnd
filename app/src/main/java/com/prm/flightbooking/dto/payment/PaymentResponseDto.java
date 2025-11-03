package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentResponseDto {
    @SerializedName("bookingId")
    private int bookingId;
    
    @SerializedName("paymentId")
    private int paymentId;
    
    @SerializedName("transactionId")
    private String transactionId;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("paymentUrl")
    private String paymentUrl;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("amount")
    private BigDecimal amount;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("notes")
    private String notes;

    public PaymentResponseDto() {}

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "PaymentResponseDto{" +
                "bookingId=" + bookingId +
                ", paymentId=" + paymentId +
                ", transactionId='" + transactionId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", notes='" + notes + '\'' +
                '}';
    }
}



package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentResponseDto {
    @SerializedName("BookingId")
    private int bookingId;
    
    @SerializedName("PaymentId")
    private int paymentId;
    
    @SerializedName("TransactionId")
    private String transactionId;
    
    @SerializedName("PaymentMethod")
    private String paymentMethod;
    
    @SerializedName("PaymentUrl")
    private String paymentUrl;
    
    @SerializedName("Status")
    private String status;
    
    @SerializedName("Amount")
    private BigDecimal amount;
    
    @SerializedName("CreatedAt")
    private Date createdAt;
    
    @SerializedName("Notes")
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



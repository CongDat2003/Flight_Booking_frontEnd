package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentResponseDto {
    @SerializedName("PaymentId")
    private int paymentId;
    
    @SerializedName("TransactionId")
    private String transactionId;
    
    @SerializedName("PaymentUrl")
    private String paymentUrl;
    
    @SerializedName("Status")
    private String status;
    
    @SerializedName("Amount")
    private BigDecimal amount;
    
    @SerializedName("CreatedAt")
    private Date createdAt;

    public PaymentResponseDto() {}

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
}



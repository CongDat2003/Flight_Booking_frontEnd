package com.prm.flightbooking.dto.payment;

import com.google.gson.annotations.SerializedName;

public class UpdatePaymentDto {
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("amount")
    private Double amount;
    
    @SerializedName("status")
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED
    
    @SerializedName("notes")
    private String notes;
    
    public UpdatePaymentDto() {}
    
    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}








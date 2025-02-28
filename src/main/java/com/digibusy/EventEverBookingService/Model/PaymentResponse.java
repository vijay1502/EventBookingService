package com.digibusy.EventEverBookingService.Model;

import lombok.*;


public class PaymentResponse {
    private String status;    // PENDING, SUCCESS, FAILED
    private String orderId;   // Razorpay Order ID (if applicable)

    public PaymentResponse() {
    }

    public PaymentResponse(String status, String orderId) {
        this.status = status;
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "status='" + status + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}


package com.example.retix.model;

import java.time.LocalDateTime;

public class TicketDTO {
    private String eventName;
    private LocalDateTime eventDateTime;
    private String seatDetails;
    private double price;
    private Long sellerId;

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }
    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
    public String getSeatDetails() {
        return seatDetails;
    }
    public void setSeatDetails(String seatDetails) {
        this.seatDetails = seatDetails;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public Long getSellerId() {
        return sellerId;
    }
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}

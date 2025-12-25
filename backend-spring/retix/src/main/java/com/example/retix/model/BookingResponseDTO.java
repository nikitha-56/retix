package com.example.retix.model;

public class BookingResponseDTO {
    private Long requestId;
    private String status; // use String instead of BookingRequest.Status

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.retix.model;

public class BookingResponseDTO {
    private Long requestId;
    private BookingRequest.Status status;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public BookingRequest.Status getStatus() {
        return status;
    }

    public void setStatus(BookingRequest.Status status) {
        this.status = status;
    }
}

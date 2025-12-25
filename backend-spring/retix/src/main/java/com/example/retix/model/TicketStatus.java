package com.example.retix.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TicketStatus {
    AVAILABLE,
    RESERVED,
    SOLD, 
    REQUESTED,
    TRANSFERRED
}

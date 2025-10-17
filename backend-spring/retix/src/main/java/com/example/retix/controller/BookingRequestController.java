package com.example.retix.controller;

import com.example.retix.model.BookingRequest;
import com.example.retix.model.BookingRequestDTO;
import com.example.retix.model.BookingResponseDTO;
import com.example.retix.service.BookingRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    public BookingRequestController(BookingRequestService bookingRequestService) {
        this.bookingRequestService = bookingRequestService;
    }

    @PostMapping("/request")
    public BookingRequest requestTicket(@RequestBody BookingRequestDTO request) {
        return bookingRequestService.createRequest(request.getTicketId(), request.getBuyerId());
    }

    @PostMapping("/respond")
    public BookingRequest respondToRequest(@RequestBody BookingResponseDTO response) {
        return bookingRequestService.respondToRequest(response.getRequestId(), response.getStatus());
    }

    @GetMapping("/seller/{sellerId}")
    public List<BookingRequest> getSellerRequests(@PathVariable Long sellerId) {
        return bookingRequestService.getRequestsForSeller(sellerId);
    }

    @GetMapping("/buyer/{buyerId}")
    public List<BookingRequest> getBuyerRequests(@PathVariable Long buyerId) {
        return bookingRequestService.getRequestsForBuyer(buyerId);
    }
}

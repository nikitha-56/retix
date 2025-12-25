package com.example.retix.service;

import com.example.retix.model.*;
import com.example.retix.repository.BookingRequestRepository;
import com.example.retix.repository.TicketRepository;
import com.example.retix.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepo;
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;

    public BookingRequestService(BookingRequestRepository bookingRequestRepo,
                                 TicketRepository ticketRepo,
                                 UserRepository userRepo) {
        this.bookingRequestRepo = bookingRequestRepo;
        this.ticketRepo = ticketRepo;
        this.userRepo = userRepo;
    }

  
    @Transactional
    public BookingRequest createRequest(Long ticketId, Long buyerId) {

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        User buyer = userRepo.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        if (ticket.getStatus() != TicketStatus.AVAILABLE)
            throw new IllegalStateException("Ticket not available");

        BookingRequest req = new BookingRequest();
        req.setTicket(ticket);
        req.setBuyer(buyer);
        req.setStatus(BookingRequest.Status.PENDING);
        req.setTimestamp(LocalDateTime.now());

        ticket.setStatus(TicketStatus.REQUESTED);
        ticketRepo.save(ticket);

        return bookingRequestRepo.save(req);
    }

    /* ------------------------------------------------------------- */
    /* 2. Seller responds (ACCEPT / DECLINE)                         */
    /* ------------------------------------------------------------- */
    @Transactional
    public BookingRequest respondToRequest(Long requestId, String status) {
        BookingRequest request = bookingRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found"));

        BookingRequest.Status enumStatus = BookingRequest.Status.valueOf(status.toUpperCase());
        request.setStatus(enumStatus);

        Ticket ticket = request.getTicket();
        if (enumStatus == BookingRequest.Status.ACCEPTED) {
            ticket.setStatus(TicketStatus.SOLD);
            ticket.setBuyer(request.getBuyer());
        } else if (enumStatus == BookingRequest.Status.DECLINED) {
            ticket.setStatus(TicketStatus.AVAILABLE);
        }
        ticketRepo.save(ticket);
        return bookingRequestRepo.save(request);
    }

    /* ------------------------------------------------------------- */
    /* 3. Query helpers                                              */
    /* ------------------------------------------------------------- */
    public List<BookingRequest> getRequestsForSeller(Long sellerId) {
        return bookingRequestRepo.findByTicketSellerId(sellerId);
    }

    public List<BookingRequest> getRequestsForBuyer(Long buyerId) {
        return bookingRequestRepo.findByBuyerId(buyerId);
    }
}

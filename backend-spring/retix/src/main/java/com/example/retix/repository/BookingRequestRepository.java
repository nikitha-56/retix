package com.example.retix.repository;

import com.example.retix.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByTicketSellerId(Long sellerId);
    List<BookingRequest> findByBuyerId(Long buyerId);
    List<BookingRequest> findByTicketId(Long ticketId);
}

package com.example.retix.service;

import com.example.retix.model.Payment;
import com.example.retix.model.PaymentStatus;
import com.example.retix.model.Ticket;
import com.example.retix.model.TicketStatus;
import com.example.retix.model.User;
import com.example.retix.repository.PaymentRepository;
import com.example.retix.repository.TicketRepository;
import com.example.retix.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketRepository  ticketRepository;
    private final UserRepository    userRepository;
    private final TicketService     ticketService;

    public PaymentService(PaymentRepository paymentRepository,
                          TicketRepository  ticketRepository,
                          UserRepository    userRepository,
                          TicketService     ticketService) {
        this.paymentRepository = paymentRepository;
        this.ticketRepository  = ticketRepository;
        this.userRepository    = userRepository;
        this.ticketService     = ticketService;
    }

    /* ---------------------------------------------------------- */
    /* 1. CREATE PAYMENT (reserves ticket + inserts PENDING row)  */
    /* ---------------------------------------------------------- */
    @Transactional
    public Payment createPayment(Long ticketId, Long buyerId, double amount) {

        paymentRepository.findFirstByTicketIdAndBuyerIdAndStatus(
                ticketId, buyerId, PaymentStatus.PENDING
        ).ifPresent(p -> { throw new IllegalStateException(); });

        ticketService.reserve(ticketId, buyerId);            // status -> RESERVED

        Payment p = new Payment();
        p.setTicketId(ticketId);
        p.setBuyerId(buyerId);
        p.setAmount(amount);
        p.setStatus(PaymentStatus.PENDING);
        p.setReference(UUID.randomUUID().toString());

        return paymentRepository.save(p);
    }

    /* -------------------------------------- */
    /* 2. SIMPLE GETTER FOR CONTROLLER / API  */
    /* -------------------------------------- */
    public Payment getPayment(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    /* ------------------------------------------------------ */
    /* 3. CONFIRM PAYMENT (marks ticket SOLD + payment DONE)  */
    /* ------------------------------------------------------ */
    @Transactional
    public boolean markPaymentAsCompleted(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null || payment.getStatus() != PaymentStatus.PENDING) return false;

        Ticket ticket = ticketRepository.findById((Long) payment.getTicketId()).orElse(null);
        if (ticket == null || ticket.getStatus() != TicketStatus.RESERVED) return false;

        User buyer = userRepository.findById((Long) payment.getBuyerId()).orElse(null);
        if (buyer == null) return false;

        ticket.setBuyer(buyer);
        ticket.setStatus(TicketStatus.SOLD);
        ticketRepository.save(ticket);

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        return true;
    }
}

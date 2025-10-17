package com.example.retix.service;

import com.example.retix.model.*;
import com.example.retix.repository.TicketRepository;
import com.example.retix.repository.UserRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /* ───── CRUD with Caching ───────────────────────────── */

    @CachePut(value = "tickets", key = "#result.id")
    @CacheEvict(value = "ticketList", allEntries = true)
    public Ticket createTicket(Ticket t) {
        if (t.getOwner() == null) t.setOwner(t.getSeller());
        return ticketRepository.save(t);
    }

    @Cacheable("ticketList")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Cacheable(value = "tickets", key = "#p0")
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Caching(evict = {
        @CacheEvict(value = "tickets", key = "#p0"),
        @CacheEvict(value = "ticketList", allEntries = true)
    })
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @CachePut(value = "tickets", key = "#result.id")
    @CacheEvict(value = "ticketList", allEntries = true)
    public Ticket resellTicket(Long id, User current) {
        Ticket t = ticketRepository.findById(id).orElseThrow();
        t.setStatus(TicketStatus.AVAILABLE);
        t.setBuyer(null);
        t.setSeller(current);
        t.setOwner(current);
        return ticketRepository.save(t);
    }

    @Transactional
    @CachePut(value = "tickets", key = "#result.id")
    public Ticket confirmTransfer(Long id) {
        Ticket t = ticketRepository.findById(id).orElseThrow();

        if (t.getStatus() == TicketStatus.SOLD)
            throw new IllegalStateException("Ticket already sold");
        if (t.getStatus() != TicketStatus.AVAILABLE)
            throw new IllegalStateException("Not available");

        t.setStatus(TicketStatus.SOLD);
        Ticket saved = ticketRepository.save(t);

        User buyer = t.getBuyer();
        User seller = t.getSeller();

        if (buyer != null) System.out.println("Email to buyer " + buyer.getName());
        if (seller != null) System.out.println("Email to seller " + seller.getName());

        return saved;
    }

    @Transactional
    @CachePut(value = "tickets", key = "#result.id")
    public Ticket transfer(Long ticketId, Long currentUserId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (t.getStatus() != TicketStatus.SOLD)
            throw new IllegalStateException("Ticket not paid");

        if (!t.getBuyer().getId().equals(currentUserId))
            throw new IllegalStateException("Only buyer can claim transfer");

        t.setOwner(t.getBuyer());
        t.setBuyer(null);
        t.setStatus(TicketStatus.TRANSFERRED);
        return ticketRepository.save(t);
    }

    @Transactional
    @CachePut(value = "tickets", key = "#result.id")
    public Ticket reserve(Long ticketId, Long buyerId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.AVAILABLE)
            throw new IllegalStateException("Ticket is not available");

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        if (ticket.getSeller() == null)
            throw new IllegalStateException("Ticket seller cannot be null");

        ticket.setBuyer(buyer);
        ticket.setStatus(TicketStatus.RESERVED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    @CachePut(value = "tickets", key = "#result.id")
    public Ticket complete(Long id) {
        Ticket t = ticketRepository.findById(id).orElseThrow();
        if (t.getStatus() != TicketStatus.RESERVED)
            throw new IllegalStateException("Not reserved");
        t.setStatus(TicketStatus.SOLD);
        return ticketRepository.save(t);
    }

    @CachePut(value = "tickets", key = "#result.id")
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /* ───── Additional Features Without Caching ───────────── */

    public List<Ticket> searchTickets(String k) {
        return ticketRepository.findBySeatDetailsContainingIgnoreCase(k);
    }

    public List<Ticket> getTicketsListedBySeller(User s) {
        return ticketRepository.findBySeller(s);
    }

    public List<Ticket> getTicketsRequestedByBuyer(User b) {
        return ticketRepository.findByBuyer(b);
    }

    public List<Ticket> getTicketRequestsReceivedBySeller(User s) {
        return ticketRepository.findBySellerAndStatus(s, TicketStatus.REQUESTED);
    }

    public void fixSoldTicketsWithNullBuyer(User placeholder) {
        List<Ticket> list = ticketRepository.findAll();
        for (Ticket t : list) {
            if (t.getStatus() == TicketStatus.SOLD && t.getBuyer() == null) {
                t.setBuyer(placeholder);
                ticketRepository.save(t);
            }
        }
    }

    public void fillMissingEventData() {
        List<Ticket> list = ticketRepository.findAll();
        for (Ticket t : list) {
            boolean updated = false;

            if (t.getEventDateTime() == null) {
                t.setEventDateTime(LocalDateTime.now());
                updated = true;
            }

            if (t.getSeatDetails() == null || t.getSeatDetails().trim().isEmpty()) {
                t.setSeatDetails("TBD");
                updated = true;
            }

            if (updated) ticketRepository.save(t);
        }
    }

    public void standardizeCasing() {
        List<Ticket> list = ticketRepository.findAll();
        for (Ticket t : list) {
            boolean upd = false;
            String seat = t.getSeatDetails();
            if (seat != null) {
                String stdSeat = toTitleCase(seat);
                if (!stdSeat.equals(seat)) {
                    t.setSeatDetails(stdSeat);
                    upd = true;
                }
            }
            if (upd) ticketRepository.save(t);
        }
    }

    private String toTitleCase(String s) {
        if (s == null || s.isEmpty()) return s;
        StringBuilder sb = new StringBuilder();
        boolean next = true;
        for (char c : s.toCharArray()) {
            if (Character.isSpaceChar(c) || c == '-' || c == ',') {
                next = true;
                sb.append(c);
            } else if (next) {
                sb.append(Character.toTitleCase(c));
                next = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }
}

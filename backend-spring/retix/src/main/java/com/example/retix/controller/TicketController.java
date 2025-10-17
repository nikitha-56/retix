package com.example.retix.controller;

import com.example.retix.model.*;
import com.example.retix.service.TicketService;
import com.example.retix.service.UserService;
import com.example.retix.repository.BookingRequestRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService   userService;
    private final BookingRequestRepository bookingRequestRepository;

    public TicketController(TicketService ticketService, UserService userService, BookingRequestRepository bookingRequestRepository) {
        this.ticketService = ticketService;
        this.userService   = userService;
        this.bookingRequestRepository = bookingRequestRepository;
    }
    @PostMapping
    public ResponseEntity createTicket(@RequestBody TicketDTO dto) {

        if (dto.getSellerId() == null)      return ResponseEntity.badRequest().build();

        User seller = userService.getUserById(dto.getSellerId()).orElse(null);
        if (seller == null)                 return ResponseEntity.badRequest().build();

        if (seller.getRole() == Role.BUYER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Role access denied: seller cannot be a buyer");
        }

        Ticket t = new Ticket();
        t.setEventName(dto.getEventName());
        t.setEventDateTime(dto.getEventDateTime());   
        t.setSeatDetails(dto.getSeatDetails());
        t.setPrice(dto.getPrice());
        t.setSeller(seller);
        t.setStatus(TicketStatus.AVAILABLE);       

        return new ResponseEntity<>(ticketService.createTicket(t), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        var bookingRequests = bookingRequestRepository.findByTicketId(id);
        if (!bookingRequests.isEmpty()) {
            bookingRequestRepository.deleteAll(bookingRequests);
        }
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("deleted successfully");
    }

    @GetMapping("/search")
    public List<Ticket> searchTickets(@RequestParam String eventName) {
        return ticketService.searchTickets(eventName);
    }

    }

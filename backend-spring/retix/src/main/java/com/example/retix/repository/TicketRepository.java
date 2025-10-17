package com.example.retix.repository;

import com.example.retix.model.Ticket;
import com.example.retix.model.TicketStatus;
import com.example.retix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findBySeatDetailsContainingIgnoreCase(String seatDetails);

    List<Ticket> findBySeller(User seller);

    List<Ticket> findByBuyer(User buyer);
    List<Ticket> findByOwner(User u);
   List<Ticket> findByEventNameContainingIgnoreCase(String eventName);


    List<Ticket> findBySellerAndStatus(User seller, TicketStatus requested);
}

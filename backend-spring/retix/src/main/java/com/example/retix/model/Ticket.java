package com.example.retix.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    private boolean used = false;


    private LocalDateTime eventDateTime;   // ✅ new field
    private double price;

    @ManyToOne
private User owner; 

public User getOwner()  { return owner; }
    public void setOwner(User o) { this.owner = o; }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Enumerated(EnumType.STRING)
 @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'AVAILABLE'")
private TicketStatus status = TicketStatus.AVAILABLE;


    @ManyToOne private User seller;
    @ManyToOne private User buyer;

    private String seatDetails;

    /* ---------- getters & setters ---------- */

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public LocalDateTime getEventDateTime()      { return eventDateTime; }      // ✅
    public void setEventDateTime(LocalDateTime d){ this.eventDateTime = d; }    // ✅

    public double getPrice()                     { return price; }
    public void setPrice(double price)           { this.price = price; }

    public TicketStatus getStatus()              { return status; }
    public void setStatus(TicketStatus status)   { this.status = status; }

    public User getSeller()                      { return seller; }
    public void setSeller(User seller)           { this.seller = seller; }

    public User getBuyer()                       { return buyer; }
    public void setBuyer(User buyer)             { this.buyer = buyer; }

    public String getSeatDetails()               { return seatDetails; }
    public void setSeatDetails(String seat)      { this.seatDetails = seat; }
   
    public boolean isUsed()       { return used; }
public void setUsed(boolean u){ this.used = u; }
}


package com.example.retix.controller;

import com.example.retix.model.Payment;
import com.example.retix.service.PaymentService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService svc;

    public PaymentController(PaymentService svc) {
        this.svc = svc;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(@RequestParam Long ticketId,
                                      @RequestParam Long buyerId,
                                      @RequestParam double amount) {
        if (ticketId == null || ticketId < 1 || buyerId == null || buyerId < 1 || amount <= 0)
            return ResponseEntity.badRequest().build();
        try {
            Payment p = svc.createPayment(ticketId, buyerId, amount);
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam Long paymentId, @RequestParam Long currentUserId) {
        Payment p = svc.getPayment(paymentId);
        if (p == null) return ResponseEntity.badRequest().build();
        if (!p.getBuyerId().equals(currentUserId)) return ResponseEntity.status(403).build();
        return svc.markPaymentAsCompleted(paymentId)
                ? ResponseEntity.ok("Payment Confirmed")
                : ResponseEntity.badRequest().body("could not confirm payment");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> get(@PathVariable Long id) {
        Payment p = svc.getPayment(id);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }
}


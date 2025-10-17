package com.example.retix.repository;
import com.example.retix.model.Payment;
import com.example.retix.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByTicketIdAndBuyerIdAndStatus(Long t, Long b, PaymentStatus s);
}

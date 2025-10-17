package com.example.retix.controller;

import com.example.retix.model.Ticket;
import com.example.retix.service.TicketService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.*;
import java.util.*;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final TicketService ticketService;
    private final SecretKey secretKey;

    public QrController(TicketService ticketService,
                        @Value("${retix.jwt.secret}") String secretKeyBase64) {
        this.ticketService = ticketService;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
    }

    @GetMapping(value = "/generate/{ticketId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQr(@PathVariable Long ticketId) throws Exception {
        Optional<Ticket> ticketOpt = ticketService.getTicketById(ticketId);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Ticket ticket = ticketOpt.get();
        if (ticket.getOwner() == null || ticket.getEventDateTime() == null) {
            return ResponseEntity.badRequest().body("Missing ticket owner or event time".getBytes());
        }

        // Build JWT
        long eventEpochMillis = ticket.getEventDateTime()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String jwt = Jwts.builder()
                .setSubject("ticket")
                .claim("ticketId", ticket.getId())
                .claim("ownerId", ticket.getOwner().getId())
                .claim("eventDateTime", eventEpochMillis)
                .setExpiration(Date.from(Instant.ofEpochMilli(eventEpochMillis).plus(Duration.ofHours(4))))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        BufferedImage qrImage = createQrImage(jwt, 300, 300);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .header("X-JWT", jwt) // optional: expose the token directly
                .body(imageBytes);
    }

    @PostMapping("/verify")
public ResponseEntity<String> verifyTicket(@RequestBody Map<String, String> payload) {
    // 1. Extract token from JSON body

    String token = payload.get("token");
    if (token == null || token.isBlank()) {
        return ResponseEntity.badRequest().body("Missing token");
    }

    try {
        // 2. Parse & validate JWT
        var claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long ticketId           = claims.get("ticketId", Long.class);
        Long ownerId            = claims.get("ownerId", Long.class);
        Long eventDateTimeMillis= claims.get("eventDateTime", Long.class);

        // 3. Cross‑check ticket in DB
        Ticket ticket = ticketService.getTicketById(ticketId)
                .orElse(null);
        if (ticket == null) {
            return ResponseEntity.badRequest().body("Invalid ticket ID");
        }
        if (ticket.getOwner() == null || !ticket.getOwner().getId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Owner mismatch");
        }

         if (ticket.isUsed()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ticket already used");
        }


        long actualTime = ticket.getEventDateTime()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (Math.abs(actualTime - eventDateTimeMillis) > 1000) {
            return ResponseEntity.badRequest().body("Event time mismatch");
        }

        // 4. Expiry check (event time + 4 h)
        Instant expiry = Instant.ofEpochMilli(eventDateTimeMillis).plus(Duration.ofHours(4));
        if (Instant.now().isAfter(expiry)) {
            return ResponseEntity.status(HttpStatus.GONE).body("QR code expired");
        }

        ticket.setUsed(true);
        ticketService.createTicket(ticket);
        return ResponseEntity.ok("QR code verified ✅");

    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Invalid or expired QR token");
    }
}

    private BufferedImage createQrImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}

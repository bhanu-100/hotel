package com.example.hotel.Controllers;

import com.example.hotel.DTOs.PaymentRequestDTO;
import com.example.hotel.DTOs.PaymentResponseDTO;
import com.example.hotel.Services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Controller", description = "Endpoints to manage payments for bookings")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    @Operation(summary = "Create a payment", description = "Create a payment entry for a booking")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO payment = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/update-status/{paymentId}")
    @Operation(summary = "Update payment status", description = "Update the status of a payment (e.g., SUCCESS, FAILED)")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(@PathVariable Long paymentId,
                                                                  @RequestParam String status) {
        PaymentResponseDTO payment = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Fetch details of a specific payment using its ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all payments for a user", description = "Fetch all payments made by a specific user")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUser(@PathVariable Long userId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all payments for a hotel", description = "Fetch all payments for a specific hotel (admin only)")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByHotel(@PathVariable Long hotelId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByHotel(hotelId);
        return ResponseEntity.ok(payments);
    }
}

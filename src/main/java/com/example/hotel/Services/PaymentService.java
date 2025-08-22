package com.example.hotel.Services;

import com.example.hotel.DTOs.PaymentRequestDTO;
import com.example.hotel.DTOs.PaymentResponseDTO;
import com.example.hotel.Enums.PaymentMethod;
import com.example.hotel.Enums.PaymentStatus;
import com.example.hotel.Models.BookingModel;
import com.example.hotel.Models.PaymentModel;
import com.example.hotel.Repositories.BookingRepository;
import com.example.hotel.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // 1️⃣ Create a payment
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        BookingModel booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getBookingId()));

        PaymentModel payment = new PaymentModel();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(PaymentStatus.valueOf("PENDING"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(UUID.randomUUID().toString()); // Unique transaction id

        PaymentModel savedPayment = paymentRepository.save(payment);
        return mapToDTO(savedPayment);
    }

    // 2️⃣ Update payment status
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(PaymentStatus.valueOf(status.toUpperCase())); // e.g., SUCCESS, FAILED
        PaymentModel updatedPayment = paymentRepository.save(payment);
        return mapToDTO(updatedPayment);
    }

    // 3️⃣ Get payment by ID
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        return mapToDTO(payment);
    }

    // 4️⃣ Get all payments for a specific user
    public List<PaymentResponseDTO> getPaymentsByUser(Long userId) {
        List<PaymentModel> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking().getUser().getId().equals(userId))
                .collect(Collectors.toList());

        return payments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 5️⃣ Get all payments for a specific hotel
    public List<PaymentResponseDTO> getPaymentsByHotel(Long hotelId) {
        List<PaymentModel> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking().getRoom().getHotel().getId().equals(hotelId))
                .collect(Collectors.toList());

        return payments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Mapper function: PaymentModel -> PaymentResponseDTO
    private PaymentResponseDTO mapToDTO(PaymentModel payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(String.valueOf(payment.getPaymentMethod()));
        dto.setStatus(String.valueOf(payment.getStatus()));
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setTransactionId(payment.getTransactionId());
        return dto;
    }
}

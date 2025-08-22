package com.example.hotel.Repositories;

import com.example.hotel.Models.PaymentModel;
import com.example.hotel.Models.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

}

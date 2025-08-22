package com.example.hotel.Repositories;

import com.example.hotel.Models.HotelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<HotelModel, Long> {

    List<HotelModel> findByIsActiveTrue();
    boolean existsByName(String name);
    List<HotelModel> findByCity(String city);
    @Query("SELECT h FROM HotelModel h WHERE h.rating >= :minRating")
    List<HotelModel> findHotelsWithMinRating(double minRating);
}

package com.example.hotel.Repositories;

import com.example.hotel.Models.HotelModel;
import com.example.hotel.Models.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomModel, Long> {

    List<RoomModel> findByHotel(HotelModel hotel);
    List<RoomModel> findByHotelAndAvailableTrue(HotelModel hotel);

}

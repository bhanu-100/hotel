package com.example.hotel.Repositories;

import com.example.hotel.Enums.BookingStatus;
import com.example.hotel.Models.BookingModel;
import com.example.hotel.Models.UserModel;
import com.example.hotel.Models.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingModel, Long> {

    List<BookingModel> findByRoomAndStatusIn(RoomModel room, List<BookingStatus> statuses);
    List<BookingModel> findByUser(UserModel user);

}

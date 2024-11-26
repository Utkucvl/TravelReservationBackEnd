package com.rezervation.TravelRezervation.dao;
import com.rezervation.TravelRezervation.dao.entity.Hotel;
import com.rezervation.TravelRezervation.dao.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByHotel(Hotel hotel);

    List<Reservation> findByUserId(Long userId);
}

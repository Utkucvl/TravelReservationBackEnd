package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dao.entity.Reservation;
import com.rezervation.TravelRezervation.dto.ReservationCreateDto;
import com.rezervation.TravelRezervation.dto.ReservationDto;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto);
    public void cancelReservation(Long reservationId);
    public ReservationDto updateReservation(Long reservationId, ReservationCreateDto reservationUpdateDto);
    public List<ReservationDto> getAllReservations();
    public List<ReservationDto> getByHotelId(Long hotelId);
    public int getAvailableRoomCount(Long hotelId, LocalDate entryDate, LocalDate outDate, int guessCount);
}

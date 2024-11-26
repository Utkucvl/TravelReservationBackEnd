package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dao.entity.Reservation;
import com.rezervation.TravelRezervation.dto.ReservationCreateDto;
import com.rezervation.TravelRezervation.dto.ReservationDto;
import com.rezervation.TravelRezervation.dto.UserReservationDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto);

    public void cancelReservation(Long reservationId);

    public ReservationDto updateReservation(Long reservationId, ReservationCreateDto reservationUpdateDto);

    public List<ReservationDto> getAllReservations();

    public List<ReservationDto> getByHotelId(Long hotelId);

    public List<UserReservationDto> getFutureReservationsByUserId(Long userId);

    public int getAvailableRoomCount(Long hotelId, LocalDate entryDate, LocalDate outDate, int guessCount);

    public void notifyUsersForNextDayReservations();

    public void notifyUsersForNextWeekReservations();

    public int getTotalPriceOfReservationsOneWeekAgo();
    public int getTotalPriceOfReservationsOneMonthAgo();

    public int getNumOfReservationsOneWeekAgo();
    public int getNumOfReservationsOneMonthAgo();
    public List<Map<String, Integer>> getReservedHotelsForOneYear();

}

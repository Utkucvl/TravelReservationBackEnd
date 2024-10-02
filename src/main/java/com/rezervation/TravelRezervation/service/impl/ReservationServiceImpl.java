package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.HotelRepository;
import com.rezervation.TravelRezervation.dao.ReservationRepository;
import com.rezervation.TravelRezervation.dao.UserRepository;
import com.rezervation.TravelRezervation.dao.entity.Hotel;
import com.rezervation.TravelRezervation.dao.entity.Reservation;
import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.ReservationCreateDto;
import com.rezervation.TravelRezervation.dto.ReservationDto;
import com.rezervation.TravelRezervation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto) {
        Optional<User> userOptional = userRepository.findById(reservationCreateDto.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz kullanıcı ID'si: " + reservationCreateDto.getUserId());
        }

        Optional<Hotel> hotelOptional = hotelRepository.findById(reservationCreateDto.getHotelId());
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + reservationCreateDto.getHotelId());
        }

        User user = userOptional.get();
        Hotel hotel = hotelOptional.get();

        if (reservationCreateDto.getEntryDate().isAfter(reservationCreateDto.getOutDate())) {
            throw new IllegalArgumentException("Giriş tarihi, çıkış tarihinden önce olmalıdır.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setHotel(hotel);
        reservation.setEntryDate(reservationCreateDto.getEntryDate());
        reservation.setOutDate(reservationCreateDto.getOutDate());
        reservation.setGuessCount(reservationCreateDto.getGuessCount());

        if(reservation != null){
            if(reservation.getGuessCount() == 1){
                reservation.getHotel().setSingleRoomCount(reservation.getHotel().getSingleRoomCount()-1);
            }
            if(reservation.getGuessCount() == 2){
                reservation.getHotel().setDoubleRoomCount(reservation.getHotel().getDoubleRoomCount()-1);
            }
            if(reservation.getGuessCount() == 3){
                reservation.getHotel().setFamilyRoomCount(reservation.getHotel().getFamilyRoomCount()-1);
            }
            user.getReservations().add(reservation);
            hotel.getReservations().add(reservation);
            userRepository.save(user);
            hotelRepository.save(hotel);
        }


        Reservation savedReservation = reservationRepository.save(reservation);

        return convertToReservationDto(savedReservation);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz rezervasyon ID'si: " + reservationId);
        }
        Reservation reservation = reservationOptional.get();
        User user = reservation.getUser();
        Hotel hotel = reservation.getHotel();
        if(reservation != null){
            if(reservation.getGuessCount() == 1){
                reservation.getHotel().setSingleRoomCount(reservation.getHotel().getSingleRoomCount()+1);
            }
            if(reservation.getGuessCount() == 2){
                reservation.getHotel().setDoubleRoomCount(reservation.getHotel().getDoubleRoomCount()+1);
            }
            if(reservation.getGuessCount() == 3){
                reservation.getHotel().setFamilyRoomCount(reservation.getHotel().getFamilyRoomCount()+1);
            }
            user.getReservations().remove(reservation);
            hotel.getReservations().remove(reservation);
            userRepository.save(user);
            hotelRepository.save(hotel);
        }

        reservationRepository.delete(reservation);
    }

    @Override
    public ReservationDto updateReservation(Long reservationId, ReservationCreateDto reservationUpdateDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz rezervasyon ID'si: " + reservationId);
        }

        Reservation reservation = reservationOptional.get();
        if (reservationUpdateDto.getEntryDate().isAfter(reservationUpdateDto.getOutDate())) {
            throw new IllegalArgumentException("Giriş tarihi, çıkış tarihinden önce olmalıdır.");
        }

        reservation.setEntryDate(reservationUpdateDto.getEntryDate());
        reservation.setOutDate(reservationUpdateDto.getOutDate());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToReservationDto(updatedReservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertToReservationDto)
                .collect(Collectors.toList());
    }
    // New method to get reservations by hotel ID
    @Override
    public List<ReservationDto> getByHotelId(Long hotelId) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + hotelId);
        }
        List<Reservation> reservations = reservationRepository.findByHotel(hotelOptional.get());
        return reservations.stream()
                .map(this::convertToReservationDto)
                .collect(Collectors.toList());
    }
    @Override
    public int getAvailableRoomCount(Long hotelId, LocalDate entryDate, LocalDate outDate, int guessCount) {
        // Öncelikle oteli ID'sine göre al
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + hotelId);
        }

        Hotel hotel = hotelOptional.get();

        // Otele ait olan aktif rezervasyonları al
        List<Reservation> reservations = reservationRepository.findByHotel(hotel);

        // Toplam odaları hesapla
        int availableRooms = 0;
        if (guessCount == 1) {
            availableRooms = hotel.getSingleRoomCount();
        } else if (guessCount == 2) {
            availableRooms = hotel.getDoubleRoomCount();
        } else if (guessCount == 3) {
            availableRooms = hotel.getFamilyRoomCount();
        } else {
            throw new IllegalArgumentException("Geçersiz misafir sayısı: " + guessCount);
        }

        // Aktif rezervasyonları kontrol et
        for (Reservation reservation : reservations) {
            // Rezervasyonun tarih aralığı mevcut tarih aralığı ile çakışıyorsa
            if ((entryDate.isBefore(reservation.getOutDate()) && outDate.isAfter(reservation.getEntryDate()))) {
                // Misafir sayısına göre mevcut oda sayısını azalt
                if (reservation.getGuessCount() == guessCount) {
                    if (guessCount == 1) {
                        availableRooms--;
                    } else if (guessCount == 2) {
                        availableRooms--;
                    } else if (guessCount == 3) {
                        availableRooms--;
                    }
                }
            }
        }

        return availableRooms; // Mevcut oda sayısını döndür
    }


    // Manual Mapping Methods
    private ReservationDto convertToReservationDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setUserId(reservation.getUser().getId());
        reservationDto.setHotelId(reservation.getHotel().getId());
        reservationDto.setEntryDate(reservation.getEntryDate());
        reservationDto.setOutDate(reservation.getOutDate());
        reservationDto.setGuessCount(reservation.getGuessCount());
        return reservationDto;
    }

    private Reservation convertToReservation(ReservationCreateDto reservationCreateDto, User user, Hotel hotel) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setHotel(hotel);
        reservation.setGuessCount(reservationCreateDto.getGuessCount());
        reservation.setEntryDate(reservationCreateDto.getEntryDate());
        reservation.setOutDate(reservationCreateDto.getOutDate());
        return reservation;
    }
}

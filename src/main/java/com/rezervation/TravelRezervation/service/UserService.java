package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dao.UserRepository;
import com.rezervation.TravelRezervation.dao.entity.Reservation;
import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.ReservationDto;
import com.rezervation.TravelRezervation.dto.UserDtoWithName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDtoWithName> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserDtoWithName userDtoWithName = new UserDtoWithName();
                    userDtoWithName.setId(user.getId());
                    userDtoWithName.setUserName(user.getUserName());
                    userDtoWithName.setAge(user.getAge());
                    userDtoWithName.setRole(user.getRole());
                    userDtoWithName.setEmail(user.getEmail());
                    userDtoWithName.setSurname(user.getSurname());
                    userDtoWithName.setTcNo(user.getTcNo());
                    userDtoWithName.setReservations(convertToReservationDtoList(user.getReservations())); // Düzenleme
                    return userDtoWithName;
                })
                .collect(Collectors.toList());
    }

    public UserDtoWithName findById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        UserDtoWithName userDtoWithName = new UserDtoWithName();
        userDtoWithName.setId(user.getId());
        userDtoWithName.setUserName(user.getUserName());
        userDtoWithName.setAge(user.getAge());
        userDtoWithName.setRole(user.getRole());
        userDtoWithName.setEmail(user.getEmail());
        userDtoWithName.setSurname(user.getSurname());
        userDtoWithName.setTcNo(user.getTcNo());
        userDtoWithName.setReservations(convertToReservationDtoList(user.getReservations())); // Düzenleme
        return userDtoWithName;
    }

    public User createOneUser(User user) {
        return userRepository.save(user);
    }

    public User getOneUserByUserName(String userName) {
        return (userRepository.findByUserName(userName));
    }

    public User getOneUserByEmail(String email) {
        return (userRepository.findByEmail(email));
    }

    // Tek bir rezervasyonu DTO'ya çeviren metod
    private ReservationDto convertToReservationDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setUserId(reservation.getUser().getId());
        reservationDto.setHotelId(reservation.getHotel().getId());
        reservationDto.setEntryDate(reservation.getEntryDate());
        reservationDto.setOutDate(reservation.getOutDate());
        reservationDto.setGuessCount(reservation.getGuessCount());
        reservationDto.setTotalPrice(reservation.getTotalPrice());
        return reservationDto;
    }

    // Listeyi DTO'ya çeviren metod
    private List<ReservationDto> convertToReservationDtoList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::convertToReservationDto)
                .collect(Collectors.toList());
    }
}


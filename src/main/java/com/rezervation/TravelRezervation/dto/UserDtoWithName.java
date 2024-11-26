package com.rezervation.TravelRezervation.dto;

import com.rezervation.TravelRezervation.dao.entity.Reservation;
import lombok.Data;

import java.util.List;

@Data
public class UserDtoWithName {
    private int id;
    private String userName;
    private String surname;
    private String role ;
    private String email;
    private Long tcNo;
    private Long age;
    private List<ReservationDto> reservations;
}

package com.rezervation.TravelRezervation.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserReservationDto {
    private Long id;
    private String hotelName;
    private String country;
    private String city;
    private String userName;
    private String email;
    private int guessCount;
    private int totalPrice;
    private LocalDate entryDate;
    private LocalDate outDate;
}

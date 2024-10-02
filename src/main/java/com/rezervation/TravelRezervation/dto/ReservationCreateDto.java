package com.rezervation.TravelRezervation.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationCreateDto {
    private Long userId;
    private Long hotelId;
    private int guessCount;
    private LocalDate entryDate;
    private LocalDate outDate;
}

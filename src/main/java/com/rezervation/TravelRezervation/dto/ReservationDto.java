package com.rezervation.TravelRezervation.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationDto {
    private Long id;
    private int userId;
    private int hotelId;
    private int guessCount;
    private LocalDate entryDate;
    private LocalDate outDate;
}

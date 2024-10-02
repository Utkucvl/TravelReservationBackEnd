package com.rezervation.TravelRezervation.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "Reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "guess_count", nullable = false)
    private int guessCount;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "out_date", nullable = false)
    private LocalDate outDate;
}
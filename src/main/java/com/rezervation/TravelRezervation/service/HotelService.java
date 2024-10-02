package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dto.HotelCreateDto;
import com.rezervation.TravelRezervation.dto.HotelDto;
import com.rezervation.TravelRezervation.dto.TestimonialCreateDto;
import com.rezervation.TravelRezervation.dto.TestimonialDto;

import java.time.LocalDate;
import java.util.List;

public interface HotelService {
    HotelDto getById(Long id);

    List<HotelDto> getHotels();

    HotelDto save(HotelCreateDto hotelCreateDto);


    List<HotelDto> getHotelsByFilters(String country , String city, int guestCount, LocalDate entryDate, LocalDate outDate, Double maxPrice);

    Boolean delete(Long id);

    HotelDto update(Long id, HotelDto hotelDto);
}

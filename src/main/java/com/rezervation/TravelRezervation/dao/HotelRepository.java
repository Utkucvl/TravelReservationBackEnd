package com.rezervation.TravelRezervation.dao;

import com.rezervation.TravelRezervation.dao.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByCityAndCountryAndSingleRoomCountGreaterThanEqual(String city, String country, int count);
    List<Hotel> findByCityAndCountryAndDoubleRoomCountGreaterThanEqual(String city, String country, int count);
    List<Hotel> findByCityAndCountryAndFamilyRoomCountGreaterThanEqual(String city, String country, int count);
    List<Hotel> findByCityAndCountry(String city, String country);
}

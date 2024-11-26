package com.rezervation.TravelRezervation.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.util.List;

@Data
public class HotelCreateDto {
    private String name;
    private String street; // Sokak
    private String city; // Şehir
    private String state; // Eyalet veya il (ülkelere göre değişebilir)
    private String country; // Ülke
    private String postalCode; // Posta Kodu
    private String address;
    private String neighborhood;
    private int starRating;
    private int singleRoomCount;
    private int doubleRoomCount;
    private int familyRoomCount;
    private String mainImageUrl;
    private List<String> imageUrls;
    private int singleRoomPrice;
    private int doubleRoomPrice;
    private int familyRoomPrice;

}

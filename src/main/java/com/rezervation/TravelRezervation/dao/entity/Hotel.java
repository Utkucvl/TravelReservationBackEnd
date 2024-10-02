package com.rezervation.TravelRezervation.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name="Hotels")
@Data
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id ;
    @Column(nullable = false)
    private String name;
    private String street; // Sokak
    private String city; // Şehir
    private String state; // Eyalet veya il (ülkelere göre değişebilir)
    private String country; // Ülke
    private String postalCode; // Posta Kodu
    private String address;
    private int starRating;
    private int singleRoomCount;
    private int doubleRoomCount;
    private int familyRoomCount;
    private int totalRoomCount;
    private int singleRoomPrice;
    private int doubleRoomPrice;
    private int familyRoomPrice;
    private String mainImageUrl;
    @ElementCollection
    @CollectionTable(name = "hotel_images", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;
}

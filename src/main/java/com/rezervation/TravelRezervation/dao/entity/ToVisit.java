package com.rezervation.TravelRezervation.dao.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name="ToVisits")
@Data
public class ToVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id ;
    String title;
    String description;
    String type;
    Long price;
    String imageUrl;
    String location;
}

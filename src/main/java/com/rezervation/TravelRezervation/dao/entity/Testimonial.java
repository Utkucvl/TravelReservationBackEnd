package com.rezervation.TravelRezervation.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="Testimonials")
@Data
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id ;
    String name;
    String text;
    String imageUrl;
}

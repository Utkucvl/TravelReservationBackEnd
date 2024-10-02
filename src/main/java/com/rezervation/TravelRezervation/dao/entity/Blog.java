package com.rezervation.TravelRezervation.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name="Blogs")
@Data
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id ;
    String title;
    String description;
    String author;
    Date date;
    String imageUrl;
}

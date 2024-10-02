package com.rezervation.TravelRezervation.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class BlogDto {
    int id ;
    String title;
    String description;
    String author;
    Date date;
    String imageUrl;
}

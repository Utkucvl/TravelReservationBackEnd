package com.rezervation.TravelRezervation.dto;

import lombok.Data;

@Data
public class ToVisitDto {
    int id ;
    String title;
    String description;
    String type;
    Long price;
    String imageUrl;
    String location;
}

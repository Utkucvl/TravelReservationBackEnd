package com.rezervation.TravelRezervation.dto;

import lombok.Data;

@Data
public class ToVisitCreateDto {

    String title;
    String description;
    String type;
    Long price;
    String imageUrl;
    String location;
}

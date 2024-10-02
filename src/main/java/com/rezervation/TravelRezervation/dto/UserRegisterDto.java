package com.rezervation.TravelRezervation.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserRegisterDto {
    String userName;
    String surname;
    String password;
    String email;
    Long tcNo;
    Long age;
}

package com.rezervation.TravelRezervation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class
AuthDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String accessToken;
    String role;

}

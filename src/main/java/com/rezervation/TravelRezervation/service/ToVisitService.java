package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dto.BlogCreateDto;
import com.rezervation.TravelRezervation.dto.BlogDto;
import com.rezervation.TravelRezervation.dto.ToVisitCreateDto;
import com.rezervation.TravelRezervation.dto.ToVisitDto;

import java.util.List;

public interface ToVisitService {
    ToVisitDto getById(Long id);

    List<ToVisitDto> getToVisits();

    ToVisitDto save(ToVisitCreateDto toVisitCreateDto);

    Boolean delete(Long id);

    ToVisitDto update(Long id, ToVisitDto toVisitDto);
}

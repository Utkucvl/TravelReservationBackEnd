package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.ToVisitRepository;
import com.rezervation.TravelRezervation.dao.entity.ToVisit;
import com.rezervation.TravelRezervation.dto.ToVisitCreateDto;
import com.rezervation.TravelRezervation.dto.ToVisitDto;
import com.rezervation.TravelRezervation.service.ToVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ToVisitServiceImpl implements ToVisitService {

    private final ToVisitRepository toVisitRepository;

    @Autowired
    public ToVisitServiceImpl(ToVisitRepository toVisitRepository) {
        this.toVisitRepository = toVisitRepository;
    }

    @Override
    public ToVisitDto getById(Long id) {
        ToVisit toVisit = toVisitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ToVisit not found with id: " + id));
        return convertToDto(toVisit);
    }

    @Override
    public List<ToVisitDto> getToVisits() {
        return toVisitRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ToVisitDto save(ToVisitCreateDto toVisitCreateDto) {
        ToVisit toVisit = convertToEntity(toVisitCreateDto);
        ToVisit savedToVisit = toVisitRepository.save(toVisit);
        return convertToDto(savedToVisit);
    }

    @Override
    public Boolean delete(Long id) {
        if (toVisitRepository.existsById(id)) {
            toVisitRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ToVisitDto update(Long id, ToVisitDto toVisitDto) {
        ToVisit existingToVisit = toVisitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ToVisit not found with id: " + id));

        existingToVisit.setDescription(toVisitDto.getDescription());
        existingToVisit.setTitle(toVisitDto.getTitle());
        existingToVisit.setImageUrl(toVisitDto.getImageUrl());
        existingToVisit.setPrice(toVisitDto.getPrice());
        existingToVisit.setLocation(toVisitDto.getLocation());
        existingToVisit.setType(toVisitDto.getType());

        ToVisit updatedToVisit = toVisitRepository.save(existingToVisit);
        return convertToDto(updatedToVisit);
    }

    // Convert ToVisit entity to ToVisitDto
    private ToVisitDto convertToDto(ToVisit toVisit) {
        ToVisitDto toVisitDto = new ToVisitDto();
        toVisitDto.setId(toVisit.getId());
        toVisitDto.setDescription(toVisit.getDescription());
        toVisitDto.setTitle(toVisit.getTitle());
        toVisitDto.setImageUrl(toVisit.getImageUrl());
        toVisitDto.setPrice(toVisit.getPrice());
        toVisitDto.setLocation(toVisit.getLocation());
        toVisitDto.setType(toVisit.getType());
        return toVisitDto;
    }

    // Convert ToVisitCreateDto to ToVisit entity
    private ToVisit convertToEntity(ToVisitCreateDto toVisitCreateDto) {
        ToVisit toVisit = new ToVisit();
        toVisit.setDescription(toVisitCreateDto.getDescription());
        toVisit.setTitle(toVisitCreateDto.getTitle());
        toVisit.setImageUrl(toVisitCreateDto.getImageUrl());
        toVisit.setPrice(toVisitCreateDto.getPrice());
        toVisit.setLocation(toVisitCreateDto.getLocation());
        toVisit.setType(toVisitCreateDto.getType());
        return toVisit;
    }
}

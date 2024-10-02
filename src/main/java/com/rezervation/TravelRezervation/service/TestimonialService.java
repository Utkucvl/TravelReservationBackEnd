package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dto.TestimonialCreateDto;
import com.rezervation.TravelRezervation.dto.TestimonialDto;

import java.util.List;

public interface TestimonialService {
    TestimonialDto getById(Long id);

    List<TestimonialDto> getTestimonials();

    TestimonialDto save(TestimonialCreateDto testimonialCreateDto);

    Boolean delete(Long id);

    TestimonialDto update(Long id, TestimonialDto testimonialDto);
}

package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.TestimonialRepository;
import com.rezervation.TravelRezervation.dto.TestimonialCreateDto;
import com.rezervation.TravelRezervation.dto.TestimonialDto;
import com.rezervation.TravelRezervation.dao.entity.Testimonial;
import com.rezervation.TravelRezervation.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;

    @Autowired
    public TestimonialServiceImpl(TestimonialRepository testimonialRepository) {
        this.testimonialRepository = testimonialRepository;
    }

    @Override
    public TestimonialDto getById(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with id: " + id));
        return convertToDto(testimonial);
    }

    @Override
    public List<TestimonialDto> getTestimonials() {
        return testimonialRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestimonialDto save(TestimonialCreateDto testimonialCreateDto) {
        Testimonial testimonial = convertToEntity(testimonialCreateDto);
        Testimonial savedTestimonial = testimonialRepository.save(testimonial);
        return convertToDto(savedTestimonial);
    }

    @Override
    public Boolean delete(Long id) {
        if (testimonialRepository.existsById(id)) {
            testimonialRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public TestimonialDto update(Long id, TestimonialDto testimonialDto) {
        Testimonial existingTestimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with id: " + id));

        existingTestimonial.setName(testimonialDto.getName());
        existingTestimonial.setText(testimonialDto.getText());
        existingTestimonial.setImageUrl(testimonialDto.getImageUrl());

        Testimonial updatedTestimonial = testimonialRepository.save(existingTestimonial);
        return convertToDto(updatedTestimonial);
    }

    // Convert Testimonial entity to TestimonialDto
    private TestimonialDto convertToDto(Testimonial testimonial) {
        TestimonialDto testimonialDto = new TestimonialDto();
        testimonialDto.setId(testimonial.getId());
        testimonialDto.setName(testimonial.getName());
        testimonialDto.setText(testimonial.getText());
        testimonialDto.setImageUrl(testimonial.getImageUrl());
        return testimonialDto;
    }

    // Convert TestimonialCreateDto to Testimonial entity
    private Testimonial convertToEntity(TestimonialCreateDto testimonialCreateDto) {
        Testimonial testimonial = new Testimonial();
        testimonial.setName(testimonialCreateDto.getName());
        testimonial.setText(testimonialCreateDto.getText());
        testimonial.setImageUrl(testimonialCreateDto.getImageUrl());
        return testimonial;
    }
}

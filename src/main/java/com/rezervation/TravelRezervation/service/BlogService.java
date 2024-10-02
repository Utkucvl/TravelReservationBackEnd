package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dto.BlogCreateDto;
import com.rezervation.TravelRezervation.dto.BlogDto;
import com.rezervation.TravelRezervation.dto.TestimonialCreateDto;
import com.rezervation.TravelRezervation.dto.TestimonialDto;

import java.util.List;

public interface BlogService {
    BlogDto getById(Long id);

    List<BlogDto> getBlogs();

    BlogDto save(BlogCreateDto blogCreateDto);

    Boolean delete(Long id);

    BlogDto update(Long id, BlogDto blogDto);
}

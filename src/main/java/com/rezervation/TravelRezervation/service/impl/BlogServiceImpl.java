package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.BlogRepository;
import com.rezervation.TravelRezervation.dao.entity.Blog;
import com.rezervation.TravelRezervation.dto.BlogCreateDto;
import com.rezervation.TravelRezervation.dto.BlogDto;
import com.rezervation.TravelRezervation.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public BlogDto getById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));
        return convertToDto(blog);
    }

    @Override
    public List<BlogDto> getBlogs() {
        return blogRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDto save(BlogCreateDto blogCreateDto) {
        Blog blog = convertToEntity(blogCreateDto);
        System.out.println(blog);
        Blog savedBlog = blogRepository.save(blog);
        return convertToDto(savedBlog);
    }

    @Override
    public Boolean delete(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public BlogDto update(Long id, BlogDto blogDto) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));

        existingBlog.setDescription(blogDto.getDescription());
        existingBlog.setTitle(blogDto.getTitle());
        existingBlog.setImageUrl(blogDto.getImageUrl());
        existingBlog.setAuthor(blogDto.getAuthor());
        existingBlog.setDate(blogDto.getDate());

        Blog updatedBlog = blogRepository.save(existingBlog);
        return convertToDto(updatedBlog);
    }

    // Convert Blog entity to BlogDto
    private BlogDto convertToDto(Blog blog) {
        BlogDto blogDto = new BlogDto();
        blogDto.setId(blog.getId());
        blogDto.setTitle(blog.getTitle());
        blogDto.setDescription(blog.getDescription());
        blogDto.setImageUrl(blog.getImageUrl());
        blogDto.setDate(blog.getDate());
        blogDto.setAuthor(blog.getAuthor());
        return blogDto;
    }

    // Convert BlogCreateDto to Blog entity
    private Blog convertToEntity(BlogCreateDto blogCreateDto) {
        Blog blog = new Blog();
        blog.setAuthor(blogCreateDto.getAuthor());
        blog.setDate(blogCreateDto.getDate());
        blog.setImageUrl(blogCreateDto.getImageUrl());
        blog.setTitle(blogCreateDto.getTitle());
        blog.setDescription(blogCreateDto.getDescription());
        return blog;
    }
}

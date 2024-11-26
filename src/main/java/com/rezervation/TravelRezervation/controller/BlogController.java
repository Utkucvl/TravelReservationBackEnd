package com.rezervation.TravelRezervation.controller;
import com.rezervation.TravelRezervation.dao.BlogRepository;
import com.rezervation.TravelRezervation.dto.BlogCreateDto;
import com.rezervation.TravelRezervation.dto.BlogDto;
import com.rezervation.TravelRezervation.service.impl.BlogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    BlogRepository blogRepository;
    @Autowired
    BlogServiceImpl blogServiceImpl;

    @GetMapping()
    public ResponseEntity<List<BlogDto>> getAll() {
        List<BlogDto> data= new ArrayList<>();
        data = blogServiceImpl.getBlogs();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogDto> getById(@PathVariable(value = "blogId", required = true) Long blogid) {
        try {

            BlogDto blogDto = blogServiceImpl.getById(blogid);
            return ResponseEntity.ok(blogDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //return 404, with null body
        }
    }
    @PostMapping
    public ResponseEntity<?> createBlog(@RequestBody BlogCreateDto blogDto) {
        try {
            System.out.println(blogDto);
            BlogDto newBlogDto = blogServiceImpl.save(blogDto);

            return ResponseEntity.created(new URI("blog/"+newBlogDto.getId())).body(newBlogDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable(value = "blogId", required = true) Long blogId,
                                                            @RequestBody BlogDto blogDto) {
        try {
            blogServiceImpl.update(blogId, blogDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<?> delete(@PathVariable(value = "blogId", required = true) Long blogId) {
        try {
            if(blogId!=null)
            {

                blogServiceImpl.delete(blogId);
                return ResponseEntity.noContent().build();
            }
            else
            {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

}

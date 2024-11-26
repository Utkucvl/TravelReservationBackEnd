package com.rezervation.TravelRezervation.controller;
import com.rezervation.TravelRezervation.dao.TestimonialRepository;
import com.rezervation.TravelRezervation.dto.TestimonialCreateDto;
import com.rezervation.TravelRezervation.dto.TestimonialDto;
import com.rezervation.TravelRezervation.service.impl.TestimonialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/testimonials")
public class TestimonialController {

    @Autowired
    TestimonialRepository testimonialRepository;
    @Autowired
    TestimonialServiceImpl testimonialServiceImpl;

    @GetMapping()
    public ResponseEntity<List<TestimonialDto>> getAll() {
        List<TestimonialDto> data= new ArrayList<>();
        data = testimonialServiceImpl.getTestimonials();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{testimonialId}")
    public ResponseEntity<TestimonialDto> getById(@PathVariable(value = "testimonialId", required = true) Long testimonialid) {
        try {

            TestimonialDto testimonialDto = testimonialServiceImpl.getById(testimonialid);
            return ResponseEntity.ok(testimonialDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //return 404, with null body
        }
    }
    @PostMapping
    public ResponseEntity<?> createTestimonial(@RequestBody TestimonialCreateDto testimonialDto) {
        try {
            TestimonialDto newTestimonialDto = testimonialServiceImpl.save(testimonialDto);

            return ResponseEntity.created(new URI("testimonial/"+newTestimonialDto.getId())).body(newTestimonialDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{testimonialId}")
    public ResponseEntity<TestimonialDto> updateTestimonial(@PathVariable(value = "testimonialId", required = true) Long testimonialId,
                                                            @RequestBody TestimonialDto testimonialDto) {
        try {
            testimonialServiceImpl.update(testimonialId, testimonialDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{testimonialId}")
    public ResponseEntity<?> delete(@PathVariable(value = "testimonialId", required = true) Long testimonialId) {
        try {
            if(testimonialId!=null)
            {

                testimonialServiceImpl.delete(testimonialId);
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

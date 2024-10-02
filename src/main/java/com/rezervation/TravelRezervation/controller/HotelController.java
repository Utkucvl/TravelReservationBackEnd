package com.rezervation.TravelRezervation.controller;
import com.rezervation.TravelRezervation.dao.HotelRepository;
import com.rezervation.TravelRezervation.dto.HotelCreateDto;
import com.rezervation.TravelRezervation.dto.HotelDto;
import com.rezervation.TravelRezervation.service.impl.HotelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    HotelServiceImpl hotelServiceImpl;

    @GetMapping()
    public ResponseEntity<List<HotelDto>> getAll() {
        List<HotelDto> data= new ArrayList<>();
        data = hotelServiceImpl.getHotels();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getById(@PathVariable(value = "hotelId", required = true) Long hotelid) {
        try {

            HotelDto hotelDto = hotelServiceImpl.getById(hotelid);
            return ResponseEntity.ok(hotelDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //return 404, with null body
        }
    }
    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody HotelCreateDto hotelDto) {
        try {
            System.out.println(hotelDto);
            HotelDto newHotelDto = hotelServiceImpl.save(hotelDto);

            return ResponseEntity.created(new URI("hotel/"+newHotelDto.getId())).body(newHotelDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable(value = "hotelId", required = true) Long hotelId,
                                              @RequestBody HotelDto hotelDto) {
        try {
            hotelServiceImpl.update(hotelId, hotelDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/search")
    public List<HotelDto> getHotelsByCityAndRoomType(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam int guestCount,
            @RequestParam LocalDate entryDate,
            @RequestParam LocalDate outDate,
            @RequestParam Double maxPrice) {
        return hotelServiceImpl.getHotelsByFilters(country,city, guestCount, entryDate, outDate, maxPrice);
    }


    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> delete(@PathVariable(value = "hotelId", required = true) Long hotelId) {
        try {
            if(hotelId!=null)
            {

                hotelServiceImpl.delete(hotelId);
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

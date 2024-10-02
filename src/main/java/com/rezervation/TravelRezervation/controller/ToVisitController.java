package com.rezervation.TravelRezervation.controller;
import com.rezervation.TravelRezervation.dao.ToVisitRepository;
import com.rezervation.TravelRezervation.dto.ToVisitCreateDto;
import com.rezervation.TravelRezervation.dto.ToVisitDto;
import com.rezervation.TravelRezervation.service.impl.ToVisitServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/toVisit")
public class ToVisitController {

    @Autowired
    ToVisitRepository toVisitRepository;
    @Autowired
    ToVisitServiceImpl toVisitServiceImpl;

    @GetMapping()
    public ResponseEntity<List<ToVisitDto>> getAll() {
        List<ToVisitDto> data= new ArrayList<>();
        data = toVisitServiceImpl.getToVisits();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{toVisitId}")
    public ResponseEntity<ToVisitDto> getById(@PathVariable(value = "toVisitId", required = true) Long toVisitid) {
        try {

            ToVisitDto toVisitDto = toVisitServiceImpl.getById(toVisitid);
            return ResponseEntity.ok(toVisitDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //return 404, with null body
        }
    }
    @PostMapping
    public ResponseEntity<?> createToVisit(@RequestBody ToVisitCreateDto toVisitDto) {
        try {
            ToVisitDto newToVisitDto = toVisitServiceImpl.save(toVisitDto);

            return ResponseEntity.created(new URI("toVisit/"+newToVisitDto.getId())).body(newToVisitDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{toVisitId}")
    public ResponseEntity<ToVisitDto> updateToVisit(@PathVariable(value = "toVisitId", required = true) Long toVisitId,
                                                            @RequestBody ToVisitDto toVisitDto) {
        try {
            toVisitServiceImpl.update(toVisitId, toVisitDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{toVisitId}")
    public ResponseEntity<?> delete(@PathVariable(value = "toVisitId", required = true) Long toVisitId) {
        try {
            if(toVisitId!=null)
            {

                toVisitServiceImpl.delete(toVisitId);
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

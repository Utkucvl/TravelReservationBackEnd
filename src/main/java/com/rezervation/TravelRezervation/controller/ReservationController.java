package com.rezervation.TravelRezervation.controller;

import com.rezervation.TravelRezervation.dto.ReservationCreateDto;
import com.rezervation.TravelRezervation.dto.ReservationDashboardStatsDto;
import com.rezervation.TravelRezervation.dto.ReservationDto;
import com.rezervation.TravelRezervation.dto.UserReservationDto;
import com.rezervation.TravelRezervation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationCreateDto reservationCreateDto) {
        if (reservationCreateDto.getUserId() == null || reservationCreateDto.getHotelId() == null ||
                reservationCreateDto.getEntryDate() == null || reservationCreateDto.getOutDate() == null ||
                reservationCreateDto.getGuessCount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hatalı giriş: Kullanıcı ID, Otel ID, giriş ve çıkış tarihleri ve misafir sayısı zorunludur.");
        }

        try {
            ReservationDto reservationDto = reservationService.createReservation(reservationCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody ReservationCreateDto reservationCreateDto) {
        if (reservationCreateDto.getEntryDate() == null || reservationCreateDto.getOutDate() == null ||
                reservationCreateDto.getGuessCount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hatalı giriş: Giriş ve çıkış tarihleri ve misafir sayısı zorunludur.");
        }

        try {
            ReservationDto reservationDto = reservationService.updateReservation(id, reservationCreateDto);
            return ResponseEntity.ok(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Rezervasyon bulunamadı.");
        }
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getReservationsByHotelId(@PathVariable Long hotelId) {
        try {
            List<ReservationDto> reservations = reservationService.getByHotelId(hotelId);
            if (reservations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Bu otel için rezervasyon bulunamadı.");
            }
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailableRoomCount(
            @RequestParam Long hotelId,
            @RequestParam LocalDate entryDate,
            @RequestParam LocalDate outDate,
            @RequestParam int guessCount) {
        if (entryDate == null || outDate == null || guessCount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hatalı giriş: Giriş ve çıkış tarihleri ve misafir sayısı zorunludur.");
        }

        try {
            int availableRooms = reservationService.getAvailableRoomCount(hotelId, entryDate, outDate, guessCount);
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/future")
    public ResponseEntity<?> getFutureReservationsByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        // userId boşsa, bad request hatası dön
        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: User ID is required.");
        }

        try {
            // Kullanıcı ID'sine göre gelecekteki rezervasyonları al
            List<UserReservationDto> reservations = reservationService.getFutureReservationsByUserId(userId);

            // Eğer rezervasyon bulunmazsa bilgi mesajı döndür
            if (reservations.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No future reservations found for user ID: " + userId);
            }

            // Başarılı durumda rezervasyonları dön
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            // Herhangi bir hata oluşursa, iç sunucu hatası (500) dön
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching reservations: " + e.getMessage());
        }
    }
    @GetMapping("/dashboardstats")
    public ResponseEntity<?> getDashboardStats() {
        try {

            int totalSalesMonthly = reservationService.getTotalPriceOfReservationsOneMonthAgo();
            int totalSalesWeekly = reservationService.getTotalPriceOfReservationsOneWeekAgo();
            int numOfReservationsMonthly = reservationService.getNumOfReservationsOneMonthAgo();
            int numOfReservationsWeekly = reservationService.getNumOfReservationsOneWeekAgo();
            ReservationDashboardStatsDto dto = new ReservationDashboardStatsDto();
            dto.setNumOfReservationsMonthly(numOfReservationsMonthly);
            dto.setNumOfReservationsWeekly(numOfReservationsWeekly);
            dto.setTotalSalesMonthly(totalSalesMonthly);
            dto.setTotalSalesWeekly(totalSalesWeekly);

            // Başarılı durumda rezervasyonları dön
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            // Herhangi bir hata oluşursa, iç sunucu hatası (500) dön
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching reservations: " + e.getMessage());
        }
    }
    @GetMapping("/chartstats")
    public ResponseEntity<?> getChartStats() {
        try {
            return ResponseEntity.ok(reservationService.getReservedHotelsForOneYear());
        } catch (Exception e) {
            // Herhangi bir hata oluşursa, iç sunucu hatası (500) dön
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching reservations: " + e.getMessage());
        }
    }
}



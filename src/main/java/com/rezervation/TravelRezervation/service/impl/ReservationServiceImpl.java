package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.HotelRepository;
import com.rezervation.TravelRezervation.dao.ReservationRepository;
import com.rezervation.TravelRezervation.dao.UserRepository;
import com.rezervation.TravelRezervation.dao.entity.Hotel;
import com.rezervation.TravelRezervation.dao.entity.Reservation;
import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.ReservationCreateDto;
import com.rezervation.TravelRezervation.dto.ReservationDto;
import com.rezervation.TravelRezervation.dto.UserReservationDto;
import com.rezervation.TravelRezervation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto) {
        System.out.println(reservationCreateDto);
        Optional<User> userOptional = userRepository.findById(reservationCreateDto.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz kullanıcı ID'si: " + reservationCreateDto.getUserId());
        }

        Optional<Hotel> hotelOptional = hotelRepository.findById(reservationCreateDto.getHotelId());
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + reservationCreateDto.getHotelId());
        }

        User user = userOptional.get();
        Hotel hotel = hotelOptional.get();

        if (reservationCreateDto.getEntryDate().isAfter(reservationCreateDto.getOutDate())) {
            throw new IllegalArgumentException("Giriş tarihi, çıkış tarihinden önce olmalıdır.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setHotel(hotel);
        reservation.setEntryDate(reservationCreateDto.getEntryDate());
        reservation.setOutDate(reservationCreateDto.getOutDate());
        reservation.setGuessCount(reservationCreateDto.getGuessCount());
        reservation.setTotalPrice(reservationCreateDto.getTotalPrice());
        user.getReservations().add(reservation);
        hotel.getReservations().add(reservation);

        String addresss =hotel.getStreet()+"+"+hotel.getNeighborhood()+"+"+hotel.getCity()+"+"+hotel.getPostalCode();
        String addressUrl = "https://www.google.com/maps/search/?api=1&query=" + addresss.replace(" ", "+");
        String subject = "Rezervason Hatırlatma";
        String text = "Sayın " + user.getUserName().toUpperCase() + " Bey/Hanım" + ",\n\n" +
                "Rezervasyonunuz Bulunduğu Otelin Adı: " +hotel.getName()  + "\n" +
                "Rezervasyon Giriş Tarihi : "+ reservation.getEntryDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                "Rezervasyon Çıkış Tarihi : "+ reservation.getOutDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                "Rezervasyon Kaç Kişili : " + reservation.getGuessCount() + "\n" +
                "Rezervasyon Toplam Kaç TL : " + reservation.getTotalPrice() + "\n" +
                "Otel Kaç Yıldızlı : "+ hotel.getStarRating() + "\n"+
                "Haritada Göster : " + addressUrl +"\n\n"+
                "İyi günler diler , sağlıklı ve mutlu günler dileriz.";
        emailService.sendSimpleMessage(user.getEmail(), subject, text);


        Reservation savedReservation = reservationRepository.save(reservation);

        return convertToReservationDto(savedReservation);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz rezervasyon ID'si: " + reservationId);
        }
        Reservation reservation = reservationOptional.get();
        User user = reservation.getUser();
        Hotel hotel = reservation.getHotel();
        user.getReservations().remove(reservation);
        hotel.getReservations().remove(reservation);
        userRepository.save(user);
        hotelRepository.save(hotel);


        reservationRepository.delete(reservation);
    }

    @Override
    public ReservationDto updateReservation(Long reservationId, ReservationCreateDto reservationUpdateDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz rezervasyon ID'si: " + reservationId);
        }

        Reservation reservation = reservationOptional.get();
        if (reservationUpdateDto.getEntryDate().isAfter(reservationUpdateDto.getOutDate())) {
            throw new IllegalArgumentException("Giriş tarihi, çıkış tarihinden önce olmalıdır.");
        }

        reservation.setEntryDate(reservationUpdateDto.getEntryDate());
        reservation.setOutDate(reservationUpdateDto.getOutDate());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToReservationDto(updatedReservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertToReservationDto)
                .collect(Collectors.toList());
    }
    // New method to get reservations by hotel ID
    @Override
    public List<ReservationDto> getByHotelId(Long hotelId) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + hotelId);
        }
        List<Reservation> reservations = reservationRepository.findByHotel(hotelOptional.get());
        return reservations.stream()
                .map(this::convertToReservationDto)
                .collect(Collectors.toList());
    }
    @Override
    public int getAvailableRoomCount(Long hotelId, LocalDate entryDate, LocalDate outDate, int guessCount) {
        // Öncelikle oteli ID'sine göre al
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz otel ID'si: " + hotelId);
        }

        Hotel hotel = hotelOptional.get();

        // Otele ait olan aktif rezervasyonları al
        List<Reservation> reservations = reservationRepository.findByHotel(hotel);

        // Toplam odaları hesapla
        int availableRooms = 0;
        if (guessCount == 1) {
            availableRooms = hotel.getSingleRoomCount();
        } else if (guessCount == 2) {
            availableRooms = hotel.getDoubleRoomCount();
        } else if (guessCount == 3) {
            availableRooms = hotel.getFamilyRoomCount();
        } else {
            throw new IllegalArgumentException("Geçersiz misafir sayısı: " + guessCount);
        }

        // Aktif rezervasyonları kontrol et
        for (Reservation reservation : reservations) {
            // Rezervasyonun tarih aralığı mevcut tarih aralığı ile çakışıyorsa
            if ((entryDate.isBefore(reservation.getOutDate()) && outDate.isAfter(reservation.getEntryDate()))) {
                // Misafir sayısına göre mevcut oda sayısını azalt
                if (reservation.getGuessCount() == guessCount) {
                    if (guessCount == 1) {
                        availableRooms--;
                    } else if (guessCount == 2) {
                        availableRooms--;
                    } else if (guessCount == 3) {
                        availableRooms--;
                    }
                }
            }
        }

        return availableRooms; // Mevcut oda sayısını döndür
    }
    @Override
    public List<UserReservationDto> getFutureReservationsByUserId(Long userId) {
        LocalDate currentDate = LocalDate.now();

        // User ID'ye göre rezervasyonları al ve gelecekteki rezervasyonları filtrele
        List<Reservation> reservations = reservationRepository.findByUserId(userId).stream()
                .filter(reservation -> reservation.getEntryDate().isAfter(currentDate))
                .collect(Collectors.toList());

        // Reservation'ları DTO'ya dönüştürerek döndür
        return reservations.stream()
                .map(this::convertToUserReservationDto)
                .collect(Collectors.toList());
    }
    @Override
    public void notifyUsersForNextDayReservations() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Reservation> reservations = reservationRepository.findAll();
        reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isEqual(tomorrow.toLocalDate()))
                .forEach(reservation -> {
                    String addresss =reservation.getHotel().getStreet()+"+"+reservation.getHotel().getNeighborhood()+"+"+reservation.getHotel().getCity()+"+"+reservation.getHotel().getPostalCode();
                    String addressUrl = "https://www.google.com/maps/search/?api=1&query=" + addresss.replace(" ", "+");
                    String subject = "Rezervason Hatırlatma";
                    String text = "Sayın " + reservation.getUser().getUserName().toUpperCase() + " Bey/Hanım" + ",\n\n" +
                            "Rezervasyonunuz Bulunduğu Otelin Adı: " +reservation.getHotel().getName()  + "\n" +
                            "Rezervasyon Giriş Tarihi : "+ reservation.getEntryDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                            "Rezervasyon Çıkış Tarihi : "+ reservation.getOutDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                            "Rezervasyon Kaç Kişili : " + reservation.getGuessCount() + "\n" +
                            "Rezervasyon Toplam Kaç TL : " + reservation.getTotalPrice() + "\n" +
                            "Otel Kaç Yıldızlı : "+ reservation.getHotel().getStarRating() + "\n"+
                            "Haritada Göster : " + addressUrl +"\n\n"+
                            "İyi günler diler , sağlıklı ve mutlu günler dileriz.";
                    emailService.sendSimpleMessage(reservation.getUser().getEmail(), subject, text);
                });
    }

    @Override
    public void notifyUsersForNextWeekReservations() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(7);
        List<Reservation> reservations = reservationRepository.findAll();
        reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isEqual(tomorrow.toLocalDate()))
                .forEach(reservation -> {
                    String addresss =reservation.getHotel().getStreet()+"+"+reservation.getHotel().getNeighborhood()+"+"+reservation.getHotel().getCity()+"+"+reservation.getHotel().getPostalCode();
                    String addressUrl = "https://www.google.com/maps/search/?api=1&query=" + addresss.replace(" ", "+");
                    String subject = "Rezervason Hatırlatma";
                    String text = "Sayın " + reservation.getUser().getUserName().toUpperCase() + " Bey/Hanım" + ",\n\n" +
                            "Rezervasyonunuz Bulunduğu Otelin Adı: " +reservation.getHotel().getName()  + "\n" +
                            "Rezervasyon Giriş Tarihi : "+ reservation.getEntryDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                            "Rezervasyon Çıkış Tarihi : "+ reservation.getOutDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+ "\n"+
                            "Rezervasyon Kaç Kişili : " + reservation.getGuessCount() + "\n" +
                            "Rezervasyon Toplam Kaç TL : " + reservation.getTotalPrice() + "\n" +
                            "Otel Kaç Yıldızlı : "+ reservation.getHotel().getStarRating() + "\n"+
                            "Haritada Göster : " + addressUrl +"\n\n"+
                            "İyi günler diler , sağlıklı ve mutlu günler dileriz.";
                    emailService.sendSimpleMessage(reservation.getUser().getEmail(), subject, text);
                });
    }

    @Override
    public List<Map<String, Integer>> getReservedHotelsForOneYear() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        List<Reservation> reservations = reservationRepository.findAll();

        // Otel adlarını ve rezervasyon sayılarını saklamak için bir harita oluştur
        Map<String, Integer> hotelReservationCount = new HashMap<>();

        for (Reservation reservation : reservations) {
            // Rezervasyon tarihi bir yıl öncesinden sonra mı?
            if (reservation.getEntryDate().isAfter(oneYearAgo)) {
                String hotelName = reservation.getHotel().getName(); // Otel adını al

                // Haritada otel adı varsa sayısını artır, yoksa 1 ile başla
                hotelReservationCount.put(hotelName, hotelReservationCount.getOrDefault(hotelName, 0) + 1);
            }
        }

        // Haritayı bir diziye ekle
        List<Map<String, Integer>> result = new ArrayList<>();
        result.add(hotelReservationCount);

        return result; // Dizi olarak döndür
    }

    @Override
    public int getTotalPriceOfReservationsOneMonthAgo() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Reservation> reservations = reservationRepository.findAll();

        int totalPrice = reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isAfter(oneMonthAgo)
                        && reservation.getEntryDate().isBefore(LocalDate.now()))
                .mapToInt(Reservation::getTotalPrice)
                .sum();

        return totalPrice;
    }
    @Override
    public int getTotalPriceOfReservationsOneWeekAgo() {
        LocalDate oneMonthAgo = LocalDate.now().minusWeeks(1);
        List<Reservation> reservations = reservationRepository.findAll();

        int totalPrice = reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isAfter(oneMonthAgo)
                        && reservation.getEntryDate().isBefore(LocalDate.now()))
                .mapToInt(Reservation::getTotalPrice)
                .sum();

        return totalPrice;
    }

    @Override
    public int getNumOfReservationsOneMonthAgo() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Reservation> reservations = reservationRepository.findAll();

        long reservationCount = reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isAfter(oneMonthAgo)
                        && reservation.getEntryDate().isBefore(LocalDate.now()))
                .count();

        return (int) reservationCount;
    }
    @Override
    public int getNumOfReservationsOneWeekAgo() {
        LocalDate oneMonthAgo = LocalDate.now().minusWeeks(1);
        List<Reservation> reservations = reservationRepository.findAll();

        long reservationCount = reservations.stream()
                .filter(reservation -> reservation.getEntryDate().isAfter(oneMonthAgo)
                        && reservation.getEntryDate().isBefore(LocalDate.now()))
                .count();

        return (int) reservationCount;
    }



    // Manual Mapping Methods
    private ReservationDto convertToReservationDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setUserId(reservation.getUser().getId());
        reservationDto.setHotelId(reservation.getHotel().getId());
        reservationDto.setEntryDate(reservation.getEntryDate());
        reservationDto.setOutDate(reservation.getOutDate());
        reservationDto.setGuessCount(reservation.getGuessCount());
        reservationDto.setTotalPrice(reservation.getTotalPrice());
        return reservationDto;
    }
    private UserReservationDto convertToUserReservationDto(Reservation reservation) {
        UserReservationDto reservationDto = new UserReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setUserName(reservation.getUser().getUserName());
        reservationDto.setEmail(reservation.getUser().getEmail());
        reservationDto.setEntryDate(reservation.getEntryDate());
        reservationDto.setOutDate(reservation.getOutDate());
        reservationDto.setGuessCount(reservation.getGuessCount());
        reservationDto.setTotalPrice(reservation.getTotalPrice());
        reservationDto.setCity(reservation.getHotel().getCity());
        reservationDto.setCountry(reservation.getHotel().getCountry());
        reservationDto.setHotelName(reservation.getHotel().getName());
        return reservationDto;
    }
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        notifyUsersForNextWeekReservations();
        notifyUsersForNextDayReservations();
    }
}

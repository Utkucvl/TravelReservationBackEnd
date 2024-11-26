package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.HotelRepository;
import com.rezervation.TravelRezervation.dto.HotelCreateDto;
import com.rezervation.TravelRezervation.dto.HotelDto;
import com.rezervation.TravelRezervation.dao.entity.Hotel;
import com.rezervation.TravelRezervation.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ReservationServiceImpl reservationServiceImpl;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository , ReservationServiceImpl reservationServiceImpl) {
        this.hotelRepository = hotelRepository;
        this.reservationServiceImpl = reservationServiceImpl;
    }

    @Override
    public HotelDto getById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with id: " + id));
        return convertToDto(hotel);
    }

    @Override
    public List<HotelDto> getHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public HotelDto save(HotelCreateDto hotelCreateDto) {
        Hotel hotel = convertToEntity(hotelCreateDto);
        hotel.setTotalRoomCount(hotelCreateDto.getSingleRoomCount() + (2 * hotel.getDoubleRoomCount()) + (3 * hotelCreateDto.getFamilyRoomCount()));
        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDto(savedHotel);
    }

    @Override
    public Boolean delete(Long id) {
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public HotelDto update(Long id, HotelDto hotelDto) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with id: " + id));

        // Güncellemeler
        existingHotel.setName(hotelDto.getName());
        existingHotel.setStreet(hotelDto.getStreet());
        existingHotel.setCity(hotelDto.getCity());
        existingHotel.setState(hotelDto.getState());
        existingHotel.setCountry(hotelDto.getCountry());
        existingHotel.setPostalCode(hotelDto.getPostalCode());
        existingHotel.setAddress(hotelDto.getAddress());
        existingHotel.setStarRating(hotelDto.getStarRating());
        existingHotel.setSingleRoomCount(hotelDto.getSingleRoomCount());
        existingHotel.setDoubleRoomCount(hotelDto.getDoubleRoomCount());
        existingHotel.setFamilyRoomCount(hotelDto.getFamilyRoomCount());
        existingHotel.setTotalRoomCount(hotelDto.getSingleRoomCount() + (2 * hotelDto.getDoubleRoomCount()) + (3 * hotelDto.getFamilyRoomCount()));
        existingHotel.setMainImageUrl(hotelDto.getMainImageUrl());
        existingHotel.setImageUrls(hotelDto.getImageUrls());

        // Otel kaydını güncelle
        Hotel updatedHotel = hotelRepository.save(existingHotel);

        // Güncellenmiş oteli DTO'ya dönüştür ve geri döndür
        return convertToDto(updatedHotel);
    }

    @Override
    public List<HotelDto> getHotelsByFilters(String country , String city, int guestCount, LocalDate entryDate, LocalDate outDate, Double maxPrice) {
        List<Hotel> hotels;

        // Misafir sayısına göre otelleri filtrele
        if (guestCount == 1) {
            List<Hotel> notFiltered = hotelRepository.findByCityAndCountryAndSingleRoomCountGreaterThanEqual(city, country, 1);
            hotels = notFiltered.stream()
                    .filter(hotel -> hotel.getSingleRoomCount() > 0)
                    .collect(Collectors.toList());
        } else if (guestCount == 2) {
            List<Hotel> notFiltered = hotelRepository.findByCityAndCountryAndDoubleRoomCountGreaterThanEqual(city, country, 1);
            hotels = notFiltered.stream()
                    .filter(hotel -> hotel.getDoubleRoomCount() > 0)
                    .collect(Collectors.toList());
        } else if (guestCount >= 3) {
            List<Hotel> notFiltered = hotelRepository.findByCityAndCountryAndFamilyRoomCountGreaterThanEqual(city, country, 1);
            hotels = notFiltered.stream()
                    .filter(hotel -> hotel.getFamilyRoomCount() > 0)
                    .collect(Collectors.toList());
        } else if (guestCount == 0) {
            List<Hotel> notFiltered = hotelRepository.findByCityAndCountry(city, country);
            hotels = notFiltered.stream()
                    .filter(hotel -> hotel.getFamilyRoomCount() > 0 || hotel.getDoubleRoomCount() > 0 || hotel.getSingleRoomCount() > 0)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Geçersiz misafir sayısı belirtildi.");
        }

        // Tarih aralığı ve fiyata göre filtreleme
        if (maxPrice != null) {
            hotels = hotels.stream()
                    .filter(hotel -> {
                        if (guestCount == 1) {
                            return hotel.getSingleRoomPrice() <= maxPrice;
                        } else if (guestCount == 2) {
                            return hotel.getDoubleRoomPrice() <= maxPrice;
                        } else if (guestCount >= 3) {
                            return hotel.getFamilyRoomPrice() <= maxPrice;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // Belirtilen tarihler arasındaki toplam fiyatı hesapla ve HotelDto'ya ekle
        long daysBetween = entryDate != null && outDate != null ? entryDate.until(outDate).getDays() : 0;

        return hotels.stream()
                .map(hotel -> {
                    HotelDto hotelDto = convertToDto(hotel);
                    int availableRoomCount = reservationServiceImpl.getAvailableRoomCount((long) hotel.getId(), entryDate, outDate, guestCount);

                    // availableRoomCount'u HotelDto'ya ayarlayın
                    if (guestCount == 1) {
                        hotelDto.setSingleRoomCount(availableRoomCount);
                    } else if (guestCount == 2) {
                        hotelDto.setDoubleRoomCount(availableRoomCount);
                    } else if (guestCount >= 3) {
                        hotelDto.setFamilyRoomCount(availableRoomCount);
                    }

                    if (daysBetween > 0) {
                        int roomPrice = 0;
                        if (guestCount == 1) {
                            roomPrice = hotel.getSingleRoomPrice();
                        } else if (guestCount == 2) {
                            roomPrice = hotel.getDoubleRoomPrice();
                        } else if (guestCount >= 3) {
                            roomPrice = hotel.getFamilyRoomPrice();
                        }
                        hotelDto.setTotalPrice((int) (roomPrice * daysBetween));
                    }
                    return hotelDto;
                })
                .filter(hotelDto -> { // Available room count'u kontrol ederek otelleri filtreleyin
                    if (guestCount == 1) {
                        return hotelDto.getSingleRoomCount() > 0;
                    } else if (guestCount == 2) {
                        return hotelDto.getDoubleRoomCount() > 0;
                    } else if (guestCount >= 3) {
                        return hotelDto.getFamilyRoomCount() > 0;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }







    // Hotel -> HotelDto dönüşümü
    private HotelDto convertToDto(Hotel hotel) {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setId(hotel.getId());
        hotelDto.setName(hotel.getName());
        hotelDto.setStreet(hotel.getStreet());
        hotelDto.setCity(hotel.getCity());
        hotelDto.setState(hotel.getState());
        hotelDto.setCountry(hotel.getCountry());
        hotelDto.setPostalCode(hotel.getPostalCode());
        hotelDto.setAddress(hotel.getAddress());
        hotelDto.setNeighborhood(hotel.getNeighborhood());
        hotelDto.setStarRating(hotel.getStarRating());
        hotelDto.setSingleRoomCount(hotel.getSingleRoomCount());
        hotelDto.setDoubleRoomCount(hotel.getDoubleRoomCount());
        hotelDto.setFamilyRoomCount(hotel.getFamilyRoomCount());
        hotelDto.setTotalRoomCount(hotel.getTotalRoomCount());
        hotelDto.setMainImageUrl(hotel.getMainImageUrl());
        hotelDto.setImageUrls(hotel.getImageUrls());
        hotelDto.setSingleRoomPrice(hotel.getSingleRoomPrice());
        hotelDto.setDoubleRoomPrice(hotel.getDoubleRoomPrice());
        hotelDto.setFamilyRoomPrice(hotel.getFamilyRoomPrice());
        hotelDto.setReservations(reservationServiceImpl.getByHotelId((long)hotel.getId()));
        return hotelDto;
    }

    // HotelCreateDto -> Hotel dönüşümü
    private Hotel convertToEntity(HotelCreateDto hotelCreateDto) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelCreateDto.getName());
        hotel.setStreet(hotelCreateDto.getStreet());
        hotel.setCity(hotelCreateDto.getCity());
        hotel.setState(hotelCreateDto.getState());
        hotel.setCountry(hotelCreateDto.getCountry());
        hotel.setPostalCode(hotelCreateDto.getPostalCode());
        hotel.setAddress(hotelCreateDto.getAddress());
        hotel.setNeighborhood(hotelCreateDto.getNeighborhood());
        hotel.setStarRating(hotelCreateDto.getStarRating());
        hotel.setSingleRoomCount(hotelCreateDto.getSingleRoomCount());
        hotel.setDoubleRoomCount(hotelCreateDto.getDoubleRoomCount());
        hotel.setFamilyRoomCount(hotelCreateDto.getFamilyRoomCount());
        hotel.setTotalRoomCount(hotelCreateDto.getSingleRoomCount() + (2 * hotelCreateDto.getDoubleRoomCount()) + (3 * hotelCreateDto.getFamilyRoomCount()));
        hotel.setMainImageUrl(hotelCreateDto.getMainImageUrl());
        hotel.setImageUrls(hotelCreateDto.getImageUrls());
        hotel.setSingleRoomPrice(hotelCreateDto.getSingleRoomPrice());
        hotel.setDoubleRoomPrice(hotelCreateDto.getDoubleRoomPrice());
        hotel.setFamilyRoomPrice(hotelCreateDto.getFamilyRoomPrice());
        return hotel;
    }
}

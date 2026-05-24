package com.bookashow.bookingservice.service;

import com.bookashow.bookingservice.dto.BookingRequest;
import com.bookashow.bookingservice.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking create(BookingRequest request);
    List<Booking> getAll();
    Booking getById(Long id);
    void delete(Long id);
}
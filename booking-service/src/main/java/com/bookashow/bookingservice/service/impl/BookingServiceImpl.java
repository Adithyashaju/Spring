package com.bookashow.bookingservice.service.impl;

import com.bookashow.bookingservice.client.MovieClient;
import com.bookashow.bookingservice.client.NotificationClient;
import com.bookashow.bookingservice.client.PlayClient;
import com.bookashow.bookingservice.dto.BookingRequest;
import com.bookashow.bookingservice.dto.NotificationRequest;
import com.bookashow.bookingservice.entity.Booking;
import com.bookashow.bookingservice.exception.ResourceNotFoundException;
import com.bookashow.bookingservice.repository.BookingRepository;
import com.bookashow.bookingservice.service.BookingService;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final MovieClient movieClient;
    private final PlayClient playClient;
    private final NotificationClient notificationClient;

    public BookingServiceImpl(BookingRepository repository,
                              MovieClient movieClient,
                              PlayClient playClient,
                              NotificationClient notificationClient) {
        this.repository = repository;
        this.movieClient = movieClient;
        this.playClient = playClient;
        this.notificationClient = notificationClient;
    }

    @Override
    public Booking create(BookingRequest request) {
        try {
            movieClient.getMovieById(request.getMovieId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Movie not found with id: " + request.getMovieId());
        }

        if (request.getPlayId() != null) {
            try {
                playClient.getPlayById(request.getPlayId());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException("Play not found with id: " + request.getPlayId());
            }
        }

        Booking booking = new Booking();
        booking.setMovieId(request.getMovieId());
        booking.setPlayId(request.getPlayId());
        booking.setUserName(request.getUserName());
        booking.setSeats(request.getSeats());

        Booking saved = repository.save(booking);

        try {
            notificationClient.sendNotification(new NotificationRequest(
                    saved.getUserName(),
                    "Your booking (ID: " + saved.getId() + ") has been confirmed!"
            ));
        } catch (Exception e) {
            // notification failure should not affect booking
        }

        return saved;
    }

    @Override
    public List<Booking> getAll() {
        return repository.findAll();
    }

    @Override
    public Booking getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

package com.bookashow.bookingservice.service;

import com.bookashow.bookingservice.client.MovieClient;
import com.bookashow.bookingservice.client.NotificationClient;
import com.bookashow.bookingservice.client.PlayClient;
import com.bookashow.bookingservice.dto.BookingRequest;
import com.bookashow.bookingservice.entity.Booking;
import com.bookashow.bookingservice.exception.ResourceNotFoundException;
import com.bookashow.bookingservice.repository.BookingRepository;
import com.bookashow.bookingservice.service.impl.BookingServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository repository;
    @Mock private MovieClient movieClient;
    @Mock private PlayClient playClient;
    @Mock private NotificationClient notificationClient;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setMovieId(1L);
        booking.setPlayId(2L);
        booking.setUserName("john_doe");
        booking.setSeats(3);

        bookingRequest = new BookingRequest();
        bookingRequest.setMovieId(1L);
        bookingRequest.setPlayId(2L);
        bookingRequest.setUserName("john_doe");
        bookingRequest.setSeats(3);
    }

    @Test
    void create_Success() {
        when(movieClient.getMovieById(1L)).thenReturn(new Object());
        when(playClient.getPlayById(2L)).thenReturn(new Object());
        when(repository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.create(bookingRequest);

        assertEquals("john_doe", result.getUserName());
        assertEquals(3, result.getSeats());
        verify(repository).save(any(Booking.class));
        verify(movieClient).getMovieById(1L);
        verify(playClient).getPlayById(2L);
    }

    @Test
    void create_MovieNotFound_ThrowsResourceNotFoundException() {
        when(movieClient.getMovieById(1L)).thenThrow(FeignException.NotFound.class);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.create(bookingRequest));
        verify(repository, never()).save(any());
    }

    @Test
    void create_PlayNotFound_ThrowsResourceNotFoundException() {
        when(movieClient.getMovieById(1L)).thenReturn(new Object());
        when(playClient.getPlayById(2L)).thenThrow(FeignException.NotFound.class);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.create(bookingRequest));
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithoutPlayId_SkipsPlayValidation() {
        bookingRequest.setPlayId(null);
        when(movieClient.getMovieById(1L)).thenReturn(new Object());
        when(repository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.create(bookingRequest);

        assertEquals("john_doe", result.getUserName());
        verify(playClient, never()).getPlayById(any());
    }

    @Test
    void getAll_ReturnsList() {
        Booking booking2 = new Booking();
        booking2.setUserName("jane_doe");
        booking2.setSeats(2);

        when(repository.findAll()).thenReturn(List.of(booking, booking2));

        List<Booking> result = bookingService.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(bookingService.getAll().isEmpty());
    }

    @Test
    void getById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(1L);

        assertEquals("john_doe", result.getUserName());
        verify(repository).findById(1L);
    }

    @Test
    void getById_NotFound_ThrowsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getById(99L));

        assertEquals("Booking not found with id: 99", ex.getMessage());
    }

    @Test
    void delete_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        bookingService.delete(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_NotFound_ThrowsResourceNotFoundException() {
        when(repository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> bookingService.delete(99L));

        assertEquals("Booking not found with id: 99", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }
}

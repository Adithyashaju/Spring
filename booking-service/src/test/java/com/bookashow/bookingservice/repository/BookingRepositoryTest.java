package com.bookashow.bookingservice.repository;

import com.bookashow.bookingservice.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRepositoryTest {

    @Mock
    private BookingRepository bookingRepository;

    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        booking1 = new Booking();
        booking1.setMovieId(1L);
        booking1.setPlayId(2L);
        booking1.setUserName("john_doe");
        booking1.setSeats(3);

        booking2 = new Booking();
        booking2.setMovieId(3L);
        booking2.setUserName("jane_doe");
        booking2.setSeats(2);
    }

    @Test
    void save_PersistsBookingSuccessfully() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        Booking saved = bookingRepository.save(booking1);

        assertNotNull(saved);
        assertEquals("john_doe", saved.getUserName());
        assertEquals(3, saved.getSeats());
        verify(bookingRepository, times(1)).save(booking1);
    }

    @Test
    void findAll_ReturnsAllBookings() {
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1, booking2));

        List<Booking> result = bookingRepository.findAll();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void findAll_ReturnsEmptyList() {
        when(bookingRepository.findAll()).thenReturn(List.of());

        List<Booking> result = bookingRepository.findAll();

        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void findById_ReturnsBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));

        Optional<Booking> found = bookingRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().getUserName());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ReturnsEmpty() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Booking> found = bookingRepository.findById(99L);

        assertFalse(found.isPresent());
        verify(bookingRepository, times(1)).findById(99L);
    }

    @Test
    void deleteById_DeletesSuccessfully() {
        doNothing().when(bookingRepository).deleteById(1L);

        bookingRepository.deleteById(1L);

        verify(bookingRepository, times(1)).deleteById(1L);
    }
}

package com.bookashow.bookingservice.controller;

import com.bookashow.bookingservice.dto.BookingRequest;
import com.bookashow.bookingservice.entity.Booking;
import com.bookashow.bookingservice.exception.GlobalExceptionHandler;
import com.bookashow.bookingservice.exception.ResourceNotFoundException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.bookashow.bookingservice.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper objectMapper;

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
    void createBooking_Returns201WithApiResponse() throws Exception {
        when(service.create(any(BookingRequest.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking created successfully"))
                .andExpect(jsonPath("$.data.userName").value("john_doe"))
                .andExpect(jsonPath("$.data.seats").value(3));
    }

    @Test
    void getAllBookings_Returns200WithApiResponse() throws Exception {
        Booking booking2 = new Booking();
        booking2.setUserName("jane_doe");
        booking2.setSeats(2);

        when(service.getAll()).thenReturn(List.of(booking, booking2));

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].userName").value("john_doe"))
                .andExpect(jsonPath("$.data[1].userName").value("jane_doe"));
    }

    @Test
    void getAllBookings_ReturnsEmptyList() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void getBookingById_Returns200WithApiResponse() throws Exception {
        when(service.getById(1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userName").value("john_doe"))
                .andExpect(jsonPath("$.data.seats").value(3));
    }

    @Test
    void getBookingById_NotFound_Returns404WithApiResponse() throws Exception {
        when(service.getById(99L)).thenThrow(new ResourceNotFoundException("Booking not found with id: 99"));

        mockMvc.perform(get("/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Booking not found with id: 99"));
    }

    @Test
    void deleteBooking_Returns200WithApiResponse() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking deleted successfully"));
    }

    @Test
    void deleteBooking_NotFound_Returns404WithApiResponse() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found with id: 99")).when(service).delete(99L);

        mockMvc.perform(delete("/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Booking not found with id: 99"));
    }

    @Test
    void createBooking_InvalidBody_Returns400WithValidationErrors() throws Exception {
        BookingRequest invalid = new BookingRequest(); // missing movieId, userName, seats=0

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.userName").value("Username is required"));
    }
}

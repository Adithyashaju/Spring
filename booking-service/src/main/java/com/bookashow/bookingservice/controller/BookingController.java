package com.bookashow.bookingservice.controller;

import com.bookashow.bookingservice.dto.ApiResponse;
import com.bookashow.bookingservice.dto.BookingRequest;
import com.bookashow.bookingservice.entity.Booking;
import com.bookashow.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking Controller", description = "APIs for managing bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new booking")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Booking created successfully", service.create(request)));
    }

    @Operation(summary = "Get all bookings")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    @Operation(summary = "Get booking by ID")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getById(@Parameter(description = "ID of the booking") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @Operation(summary = "Delete a booking by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@Parameter(description = "ID of the booking") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
    }
}

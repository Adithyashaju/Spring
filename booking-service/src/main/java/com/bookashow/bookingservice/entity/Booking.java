package com.bookashow.bookingservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Schema(description = "Booking entity representing a ticket booking in the system")
@Entity
public class Booking {

    @Schema(description = "Auto-generated unique ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID of the movie being booked", example = "1")
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @Schema(description = "ID of the play being booked (optional)", example = "2")
    private Long playId;

    @Schema(description = "Username of the person booking", example = "john_doe")
    @NotBlank(message = "Username is required")
    private String userName;

    @Schema(description = "Number of seats to book", example = "3")
    @Min(value = 1, message = "At least 1 seat must be booked")
    private int seats;

    //  getters & setters

    public Long getId() { return id; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getPlayId() { return playId; }
    public void setPlayId(Long playId) { this.playId = playId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
}
package com.bookashow.bookingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for creating a booking")
public class BookingRequest {

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

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getPlayId() { return playId; }
    public void setPlayId(Long playId) { this.playId = playId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
}

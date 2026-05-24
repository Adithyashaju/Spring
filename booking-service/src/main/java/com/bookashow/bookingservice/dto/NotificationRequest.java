package com.bookashow.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String userName;
    private String message;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

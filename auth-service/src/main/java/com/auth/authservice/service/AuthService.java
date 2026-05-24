package com.auth.authservice.service;

import com.auth.authservice.dto.AuthResponse;
import com.auth.authservice.dto.LoginRequest;
import com.auth.authservice.dto.LogoutRequest;
import com.auth.authservice.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String logout(LogoutRequest request);
}

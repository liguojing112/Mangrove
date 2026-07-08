package com.mangrove.service;

import com.mangrove.dto.request.LoginRequest;
import com.mangrove.dto.request.RegisterRequest;
import com.mangrove.dto.response.LoginResponse;
import com.mangrove.dto.response.UserInfoResponse;

public interface AuthService {

    LoginResponse login(LoginRequest req);

    LoginResponse register(RegisterRequest req);

    UserInfoResponse getCurrentUser(Long userId);
}

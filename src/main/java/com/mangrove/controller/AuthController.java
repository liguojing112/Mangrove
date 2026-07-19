package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LoginRequest;
import com.mangrove.dto.request.RegisterRequest;
import com.mangrove.dto.response.LoginResponse;
import com.mangrove.dto.response.UserInfoResponse;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysUserRepository sysUserRepository;

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(authService.register(req));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Long userId = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"))
                .getId();
        return Result.success(authService.getCurrentUser(userId));
    }

    @PutMapping("/me/favorite-date")
    public Result<Void> updateFavoriteStartDate(@RequestBody Map<String, String> body, Authentication authentication) {
        String username = authentication.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
        String dateStr = body.get("favoriteStartDate");
        if (dateStr == null || dateStr.isBlank()) {
            user.setFavoriteStartDate(null);
        } else {
            user.setFavoriteStartDate(LocalDate.parse(dateStr));
        }
        sysUserRepository.save(user);
        return Result.success(null);
    }
}

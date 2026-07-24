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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PutMapping("/me")
    public Result<Void> updateProfile(@RequestBody Map<String, String> body, Authentication authentication) {
        String username = authentication.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

        if (body.containsKey("nickname")) {
            String nickname = body.get("nickname");
            if (nickname != null && !nickname.isBlank()) {
                user.setNickname(nickname.trim().substring(0, Math.min(nickname.trim().length(), 30)));
            }
        }
        if (body.containsKey("bio")) {
            user.setBio(body.get("bio"));
        }
        if (body.containsKey("email")) {
            String email = body.get("email");
            user.setEmail((email != null && !email.isBlank()) ? email.trim() : null);
        }
        if (body.containsKey("phone")) {
            String phone = body.get("phone");
            user.setPhone((phone != null && !phone.isBlank()) ? phone.trim() : null);
        }

        sysUserRepository.save(user);
        return Result.success(null);
    }

    @PutMapping("/me/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, Authentication authentication) {
        String username = authentication.getName();
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || oldPassword.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请输入当前密码");
        }
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码不能为空且至少6位");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前密码不正确");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserRepository.save(user);
        return Result.success(null);
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

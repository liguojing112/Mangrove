package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LoginRequest;
import com.mangrove.dto.request.RegisterRequest;
import com.mangrove.dto.response.LoginResponse;
import com.mangrove.dto.response.UserInfoResponse;
import com.mangrove.entity.SysUser;
import com.mangrove.entity.UserTree;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.repository.UserTreeRepository;
import com.mangrove.security.JwtTokenProvider;
import com.mangrove.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository sysUserRepository;
    private final UserTreeRepository userTreeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest req) {
        SysUser user = sysUserRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (sysUserRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }

        if (req.getEmail() != null && sysUserRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被注册");
        }

        SysUser user = SysUser.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .email(req.getEmail())
                .role(SysUser.Role.FAN)
                .status(1)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        user = sysUserRepository.save(user);

        UserTree tree = UserTree.builder()
                .user(user)
                .level(1)
                .experience(0L)
                .consecutiveDays(0)
                .totalLikes(0)
                .totalUploads(0)
                .build();
        userTreeRepository.save(tree);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public UserInfoResponse getCurrentUser(Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }
}

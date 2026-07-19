package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.SysUser;
import com.mangrove.entity.UserTree;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.repository.UserTreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminUserController {

    private final SysUserRepository userRepository;
    private final UserTreeRepository userTreeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public Result<SysUser> createAdmin(@RequestBody CreateAdminRequest request) {
        String username = normalizeRequired(request.getUsername(), "用户名不能为空");
        String nickname = normalizeRequired(request.getNickname(), "昵称不能为空");
        String password = normalizeRequired(request.getPassword(), "密码不能为空");
        String email = normalizeOptional(request.getEmail());

        if (username.length() < 3 || username.length() > 50) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名长度应为 3-50 个字符");
        }
        if (password.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码至少需要 6 个字符");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被使用");
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser admin = userRepository.save(SysUser.builder()
                .username(username)
                .publicId(generatePublicId())
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .role(SysUser.Role.ADMIN)
                .status(1)
                .createdAt(now)
                .updatedAt(now)
                .build());

        userTreeRepository.save(UserTree.builder()
                .user(admin)
                .level(1)
                .experience(0L)
                .consecutiveDays(0)
                .totalLikes(0)
                .totalUploads(0)
                .createdAt(now)
                .updatedAt(now)
                .build());

        return Result.success(admin);
    }

    @GetMapping
    public Result<PageResult<SysUser>> listAll(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size,
                                               @RequestParam(required = false) String keyword,
                                               Authentication authentication) {
        SysUser actor = currentActor(authentication);
        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        List<SysUser> visible = userRepository.findAll().stream()
                .filter(user -> canManage(actor, user))
                .filter(user -> query.isEmpty()
                        || user.getUsername().toLowerCase().contains(query)
                        || user.getNickname().toLowerCase().contains(query))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
        int from = Math.min(page * size, visible.size());
        int to = Math.min(from + size, visible.size());
        int pages = visible.isEmpty() ? 0 : (int) Math.ceil((double) visible.size() / size);
        return Result.success(PageResult.<SysUser>builder()
                .content(visible.subList(from, to))
                .totalElements(visible.size())
                .totalPages(pages)
                .currentPage(page)
                .size(size)
                .build());
    }

    @PutMapping("/{id}/status")
    public Result<SysUser> updateStatus(@PathVariable Long id, @RequestParam Integer status,
                                        Authentication authentication) {
        SysUser target = managedTarget(id, authentication);
        target.setStatus(status != null && status == 1 ? 1 : 0);
        return Result.success(userRepository.save(target));
    }

    @PutMapping("/{id}/role")
    public Result<SysUser> updateRole(@PathVariable Long id, @RequestBody RoleRequest request,
                                      Authentication authentication) {
        SysUser actor = currentActor(authentication);
        SysUser target = managedTarget(id, authentication);
        SysUser.Role requested = SysUser.Role.valueOf(request.getRole());
        if (requested == SysUser.Role.SUPER_ADMIN
                || (actor.getRole() == SysUser.Role.ADMIN && requested == SysUser.Role.ADMIN)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权分配该角色");
        }
        target.setRole(requested);
        return Result.success(userRepository.save(target));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        userRepository.delete(managedTarget(id, authentication));
        return Result.success();
    }

    private SysUser managedTarget(Long id, Authentication authentication) {
        SysUser actor = currentActor(authentication);
        SysUser target = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));
        if (!canManage(actor, target)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权管理该用户");
        }
        return target;
    }

    private SysUser currentActor(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }

    private boolean canManage(SysUser actor, SysUser target) {
        if (actor.getRole() == SysUser.Role.SUPER_ADMIN) return true;
        if (target.getRole() == SysUser.Role.SUPER_ADMIN) return false;
        return actor.getRole() == SysUser.Role.ADMIN
                && (target.getRole() == SysUser.Role.FAN || target.getRole() == SysUser.Role.CREATOR);
    }

    private String generatePublicId() {
        String publicId;
        do {
            publicId = "MG" + UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 10).toUpperCase();
        } while (userRepository.existsByPublicId(publicId));
        return publicId;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    @lombok.Data
    static class CreateAdminRequest {
        private String username;
        private String nickname;
        private String password;
        private String email;
    }

    @lombok.Data
    static class RoleRequest {
        private String role;
    }
}

package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.CalendarResponse;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final SysUserRepository sysUserRepository;

    @GetMapping("/days")
    public Result<CalendarResponse> days(@RequestParam Long artistId, Authentication authentication) {
        String username = authentication.getName();
        Long userId = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"))
                .getId();
        return Result.success(calendarService.getDays(userId, artistId));
    }
}

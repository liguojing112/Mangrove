package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.CalendarResponse;
import com.mangrove.entity.Artist;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final ArtistRepository artistRepository;
    private final SysUserRepository sysUserRepository;

    @Override
    public CalendarResponse getDays(Long userId, Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

        LocalDate today = LocalDate.now();

        long aliveDays = 0;
        if (artist.getBirthDate() != null) {
            aliveDays = ChronoUnit.DAYS.between(artist.getBirthDate(), today);
        }

        long loveDays = 0;
        if (user.getCreatedAt() != null) {
            loveDays = ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), today);
        }

        long debutDays = 0;
        if (artist.getDebutDate() != null) {
            debutDays = ChronoUnit.DAYS.between(artist.getDebutDate(), today);
        }

        long nextBirthdayDays = 0;
        if (artist.getBirthDate() != null) {
            LocalDate nextBirthday = artist.getBirthDate().withYear(today.getYear());
            if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }
            nextBirthdayDays = ChronoUnit.DAYS.between(today, nextBirthday);
        }

        return CalendarResponse.builder()
                .aliveDays(aliveDays)
                .loveDays(loveDays)
                .debutDays(debutDays)
                .nextBirthdayDays(nextBirthdayDays)
                .build();
    }
}

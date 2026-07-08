package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.entity.BirthdayWish;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.BirthdayWishRepository;
import com.mangrove.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/birthday")
@RequiredArgsConstructor
@Slf4j
public class BirthdayController {

    private final BirthdayWishRepository birthdayWishRepository;
    private final ArtistRepository artistRepository;
    private final SysUserRepository sysUserRepository;

    @GetMapping("/artist/{id}")
    public Result<Map<String, Object>> getArtistWithBirthday(@PathVariable Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", artist.getId());
        data.put("stageName", artist.getStageName());
        data.put("birthDate", artist.getBirthDate());
        data.put("debutDate", artist.getDebutDate());
        data.put("avatarUrl", artist.getAvatarUrl());
        data.put("coverUrl", artist.getCoverUrl());
        data.put("bio", artist.getBio());

        if (artist.getBirthDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate birth = artist.getBirthDate();
            LocalDate nextBirthday = birth.withYear(today.getYear());
            if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }
            data.put("nextBirthdayDays", ChronoUnit.DAYS.between(today, nextBirthday));
        } else {
            data.put("nextBirthdayDays", null);
        }

        long totalWishes = birthdayWishRepository.countByArtistIdAndStatus(id, 1);
        data.put("totalWishes", totalWishes);

        return Result.success(data);
    }

    @PostMapping("/wishes")
    @Transactional
    public Result<BirthdayWish> submitWish(@RequestBody Map<String, Object> req, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);

        Long artistId = Long.valueOf(req.get("artistId").toString());
        String content = (String) req.get("content");
        String authorName = (String) req.getOrDefault("authorName", null);

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));

        BirthdayWish wish = BirthdayWish.builder()
                .artist(artist)
                .user(user)
                .content(content)
                .authorName(authorName)
                .status(0)
                .likes(0)
                .build();

        birthdayWishRepository.save(wish);
        log.info("用户 {} 为艺人 {} 发送了生日祝福", user.getUsername(), artist.getStageName());
        return Result.success(wish);
    }

    @GetMapping("/wishes/{artistId}")
    public Result<PageResult<BirthdayWish>> listApprovedWishes(@PathVariable Long artistId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes", "createdAt"));
        List<BirthdayWish> wishes = birthdayWishRepository
                .findByArtistIdAndStatusOrderByCreatedAtDesc(artistId, 1, pageable);
        long total = birthdayWishRepository.countByArtistIdAndStatus(artistId, 1);

        PageResult<BirthdayWish> pageResult = PageResult.<BirthdayWish>builder()
                .content(wishes)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();

        return Result.success(pageResult);
    }

    @PostMapping("/wishes/{id}/like")
    @Transactional
    public Result<String> likeWish(@PathVariable Long id) {
        BirthdayWish wish = birthdayWishRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "祝福不存在"));

        wish.setLikes(wish.getLikes() + 1);
        birthdayWishRepository.save(wish);
        return Result.success("点赞成功");
    }

    @GetMapping("/admin/birthday/wishes")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<BirthdayWish>> listPendingWishes(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<BirthdayWish> wishes = birthdayWishRepository.findByStatus(0, pageable);
        long total = birthdayWishRepository.count();

        PageResult<BirthdayWish> pageResult = PageResult.<BirthdayWish>builder()
                .content(wishes)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();

        return Result.success(pageResult);
    }

    @PutMapping("/admin/birthday/wishes/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Result<String> approveWish(@PathVariable Long id) {
        BirthdayWish wish = birthdayWishRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "祝福不存在"));

        wish.setStatus(1);
        birthdayWishRepository.save(wish);
        log.info("管理员审核通过生日祝福 id={}", id);
        return Result.success("审核通过");
    }

    @PutMapping("/admin/birthday/wishes/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Result<String> rejectWish(@PathVariable Long id) {
        BirthdayWish wish = birthdayWishRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "祝福不存在"));

        wish.setStatus(2);
        birthdayWishRepository.save(wish);
        log.info("管理员拒绝生日祝福 id={}", id);
        return Result.success("已拒绝");
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.FanWorkRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.entity.FanWork;
import com.mangrove.entity.Favorite;
import com.mangrove.entity.LikeRecord;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class FanWorkController {

    private final FanWorkRepository fanWorkRepository;
    private final SysUserRepository sysUserRepository;
    private final ArtistRepository artistRepository;
    private final LikeRecordRepository likeRecordRepository;
    private final FavoriteRepository favoriteRepository;

    @GetMapping
    public Result<PageResult<FanWork>> list(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<FanWork> works;
        if (category != null && !category.isBlank()) {
            try {
                FanWork.Category cat = FanWork.Category.valueOf(category.toUpperCase());
                works = fanWorkRepository.findByStatusAndCategory(FanWork.Status.PUBLISHED, cat, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的分类参数");
            }
        } else {
            works = fanWorkRepository.findByStatus(FanWork.Status.PUBLISHED, pageable);
        }
        PageResult<FanWork> result = PageResult.<FanWork>builder()
                .content(works)
                .totalElements(works.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<FanWork> getById(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));
        work.setViewCount(work.getViewCount() + 1);
        fanWorkRepository.save(work);
        return Result.success(work);
    }

    @PostMapping
    @Transactional
    public Result<FanWork> create(@Valid @RequestBody FanWorkRequest req, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        Artist artist = null;
        if (req.getArtistId() != null) {
            artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));
        }

        FanWork work = FanWork.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(FanWork.Category.valueOf(req.getCategory().toUpperCase()))
                .fileUrl(req.getFileUrl())
                .coverUrl(req.getCoverUrl())
                .duration(req.getDuration())
                .artist(artist)
                .creator(user)
                .status(FanWork.Status.PUBLISHED)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .build();

        return Result.success(fanWorkRepository.save(work));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<FanWork> update(@PathVariable Long id, @Valid @RequestBody FanWorkRequest req,
                                   Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (!work.getCreator().getId().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能修改自己的作品");
        }

        work.setTitle(req.getTitle());
        work.setDescription(req.getDescription());
        work.setCategory(FanWork.Category.valueOf(req.getCategory().toUpperCase()));
        work.setFileUrl(req.getFileUrl());
        work.setCoverUrl(req.getCoverUrl());
        work.setDuration(req.getDuration());

        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "艺人不存在"));
            work.setArtist(artist);
        } else {
            work.setArtist(null);
        }

        return Result.success(fanWorkRepository.save(work));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (!work.getCreator().getId().equals(user.getId()) && user.getRole() != SysUser.Role.ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此作品");
        }

        fanWorkRepository.delete(work);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    @Transactional
    public Result<String> toggleLike(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        Optional<LikeRecord> existing = likeRecordRepository
                .findByUserIdAndTargetTypeAndTargetId(user.getId(), LikeRecord.TargetType.FAN_WORK, id);

        if (existing.isPresent()) {
            likeRecordRepository.deleteByUserIdAndTargetTypeAndTargetId(user.getId(), LikeRecord.TargetType.FAN_WORK, id);
            work.setLikeCount(Math.max(0, work.getLikeCount() - 1));
            fanWorkRepository.save(work);
            return Result.success("取消点赞");
        } else {
            LikeRecord record = LikeRecord.builder()
                    .user(user)
                    .targetType(LikeRecord.TargetType.FAN_WORK)
                    .targetId(id)
                    .build();
            likeRecordRepository.save(record);
            work.setLikeCount(work.getLikeCount() + 1);
            fanWorkRepository.save(work);
            return Result.success("点赞成功");
        }
    }

    @PostMapping("/{id}/favorite")
    @Transactional
    public Result<String> toggleFavorite(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        Optional<Favorite> existing = favoriteRepository
                .findByUserIdAndTargetTypeAndTargetId(user.getId(), Favorite.TargetType.FAN_WORK, id);

        if (existing.isPresent()) {
            favoriteRepository.deleteByUserIdAndTargetTypeAndTargetId(user.getId(), Favorite.TargetType.FAN_WORK, id);
            return Result.success("取消收藏");
        } else {
            Favorite fav = Favorite.builder()
                    .user(user)
                    .targetType(Favorite.TargetType.FAN_WORK)
                    .targetId(id)
                    .build();
            favoriteRepository.save(fav);
            return Result.success("收藏成功");
        }
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

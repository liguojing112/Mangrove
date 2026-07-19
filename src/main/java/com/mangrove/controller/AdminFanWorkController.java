package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.FanWork;
import com.mangrove.repository.FanWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/works")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminFanWorkController {

    private final FanWorkRepository fanWorkRepository;

    @GetMapping("/pending")
    public Result<PageResult<FanWork>> listPending(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        List<FanWork> works = fanWorkRepository.findByStatus(FanWork.Status.PENDING, pageable);
        long total = fanWorkRepository.countByStatus(FanWork.Status.PENDING);

        PageResult<FanWork> result = PageResult.<FanWork>builder()
                .content(works)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @GetMapping
    public Result<PageResult<FanWork>> listAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<FanWork> works;
        long total;

        if (status != null && !status.isBlank()) {
            try {
                FanWork.Status workStatus = FanWork.Status.valueOf(status.toUpperCase());
                works = fanWorkRepository.findByStatus(workStatus, pageable);
                total = fanWorkRepository.countByStatus(workStatus);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的状态参数");
            }
        } else {
            works = fanWorkRepository.findAll(pageable).getContent();
            total = fanWorkRepository.count();
        }

        PageResult<FanWork> result = PageResult.<FanWork>builder()
                .content(works)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @PostMapping("/{id}/approve")
    @Transactional
    public Result<FanWork> approve(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (work.getStatus() != FanWork.Status.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该作品不在待审核状态");
        }

        work.setStatus(FanWork.Status.PUBLISHED);
        return Result.success(fanWorkRepository.save(work));
    }

    @PostMapping("/{id}/reject")
    @Transactional
    public Result<FanWork> reject(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (work.getStatus() != FanWork.Status.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该作品不在待审核状态");
        }

        work.setStatus(FanWork.Status.HIDDEN);
        return Result.success(fanWorkRepository.save(work));
    }

    @PostMapping("/{id}/feature")
    @Transactional
    public Result<FanWork> feature(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (work.getStatus() != FanWork.Status.PUBLISHED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该作品不在已发布状态");
        }

        work.setStatus(FanWork.Status.FEATURED);
        return Result.success(fanWorkRepository.save(work));
    }

    @PostMapping("/{id}/unfeature")
    @Transactional
    public Result<FanWork> unfeature(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));

        if (work.getStatus() != FanWork.Status.FEATURED) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该作品不在精选状态");
        }

        work.setStatus(FanWork.Status.PUBLISHED);
        return Result.success(fanWorkRepository.save(work));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        FanWork work = fanWorkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "作品不存在"));
        fanWorkRepository.delete(work);
        return Result.success();
    }
}

package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LotteryRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Lottery;
import com.mangrove.entity.LotteryEntry;
import com.mangrove.repository.LotteryEntryRepository;
import com.mangrove.repository.LotteryRepository;
import com.mangrove.service.LotteryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryServiceImpl implements LotteryService {

    private final LotteryRepository lotteryRepository;
    private final LotteryEntryRepository lotteryEntryRepository;

    @Override
    public PageResult<Lottery> list(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Lottery> lotteryPage = lotteryRepository.findAllByOrderByCreatedAtDesc(pageable);
        lotteryPage.forEach(this::resolveStatus);
        return PageResult.of(lotteryPage);
    }

    @Override
    public Lottery getById(Long id) {
        Lottery lottery = lotteryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在"));
        resolveStatus(lottery);
        return lottery;
    }

    @Override
    @Transactional
    public Lottery create(LotteryRequest req, Long userId, String nickname) {
        Lottery lottery = Lottery.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .conditions(req.getConditions())
                .winnerCount(req.getWinnerCount())
                .startTime(LocalDateTime.parse(req.getStartTime()))
                .endTime(LocalDateTime.parse(req.getEndTime()))
                .createdBy(userId)
                .createdByNickname(nickname)
                .status(Lottery.Status.PENDING)
                .entryCount(0)
                .statusFlag(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return lotteryRepository.save(lottery);
    }

    @Override
    @Transactional
    public Lottery update(Long id, LotteryRequest req) {
        Lottery lottery = lotteryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在"));
        if (lottery.getStatus() != Lottery.Status.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能编辑未开始的抽奖活动");
        }
        if (req.getTitle() != null) lottery.setTitle(req.getTitle());
        if (req.getDescription() != null) lottery.setDescription(req.getDescription());
        if (req.getImageUrl() != null) lottery.setImageUrl(req.getImageUrl());
        if (req.getConditions() != null) lottery.setConditions(req.getConditions());
        if (req.getWinnerCount() != null) lottery.setWinnerCount(req.getWinnerCount());
        if (req.getStartTime() != null) lottery.setStartTime(LocalDateTime.parse(req.getStartTime()));
        if (req.getEndTime() != null) lottery.setEndTime(LocalDateTime.parse(req.getEndTime()));
        lottery.setUpdatedAt(LocalDateTime.now());
        return lotteryRepository.save(lottery);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!lotteryRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在");
        }
        lotteryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Lottery toggleStatus(Long id, Integer statusFlag) {
        Lottery lottery = lotteryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在"));
        lottery.setStatusFlag(statusFlag);
        lottery.setUpdatedAt(LocalDateTime.now());
        return lotteryRepository.save(lottery);
    }

    @Override
    @Transactional
    public Lottery draw(Long id) {
        Lottery lottery = lotteryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在"));
        resolveStatus(lottery);
        if (lottery.getStatus() == Lottery.Status.DRAWN) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已经开奖过了");
        }
        if (lottery.getStatus() == Lottery.Status.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "活动尚未开始，不能开奖");
        }
        List<LotteryEntry> entries = lotteryEntryRepository.findByLotteryIdOrderByCreatedAtAsc(id);
        if (entries.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "没有人参与此抽奖");
        }
        int actualCount = Math.min(lottery.getWinnerCount(), entries.size());
        List<LotteryEntry> shuffled = new ArrayList<>(entries);
        Collections.shuffle(shuffled);
        List<LotteryEntry> winners = shuffled.subList(0, actualCount);
        lottery.setWinnerUserIds(winners.stream().map(LotteryEntry::getUserId).collect(Collectors.toList()));
        lottery.setWinnerNicknames(winners.stream().map(LotteryEntry::getUserNickname).collect(Collectors.toList()));
        lottery.setWinnerCount(actualCount);
        lottery.setStatus(Lottery.Status.DRAWN);
        lottery.setUpdatedAt(LocalDateTime.now());
        return lotteryRepository.save(lottery);
    }

    @Override
    public PageResult<LotteryEntry> listEntries(Long lotteryId, Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<LotteryEntry> entryPage = lotteryEntryRepository.findByLotteryIdOrderByCreatedAtAsc(lotteryId, pageable);
        return PageResult.of(entryPage);
    }

    @Override
    @Transactional
    public LotteryEntry enter(Long lotteryId, Long userId, String nickname) {
        Lottery lottery = lotteryRepository.findById(lotteryId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "抽奖活动不存在"));
        resolveStatus(lottery);
        if (lottery.getStatus() != Lottery.Status.ACTIVE) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前不在报名时间内");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(lottery.getStartTime()) || now.isAfter(lottery.getEndTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前不在报名时间内");
        }
        if (lotteryEntryRepository.existsByLotteryIdAndUserId(lotteryId, userId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已参与过此抽奖");
        }
        LotteryEntry entry = LotteryEntry.builder()
                .lotteryId(lotteryId)
                .userId(userId)
                .userNickname(nickname)
                .createdAt(LocalDateTime.now())
                .build();
        try {
            lotteryEntryRepository.save(entry);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已参与过此抽奖");
        }
        lottery.setEntryCount(lottery.getEntryCount() + 1);
        lottery.setUpdatedAt(LocalDateTime.now());
        lotteryRepository.save(lottery);
        return entry;
    }

    @Override
    public boolean hasEntered(Long lotteryId, Long userId) {
        return lotteryEntryRepository.existsByLotteryIdAndUserId(lotteryId, userId);
    }

    @Override
    public List<Lottery> listPublic() {
        List<Lottery> lotteries = lotteryRepository.findByStatusInAndStatusFlag(
                List.of(Lottery.Status.PENDING, Lottery.Status.ACTIVE, Lottery.Status.ENDED, Lottery.Status.DRAWN),
                1);
        lotteries.forEach(this::resolveStatus);
        return lotteries;
    }

    private void resolveStatus(Lottery lottery) {
        if (lottery.getStatus() == Lottery.Status.DRAWN) return;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(lottery.getStartTime())) {
            lottery.setStatus(Lottery.Status.PENDING);
        } else if (now.isBefore(lottery.getEndTime())) {
            lottery.setStatus(Lottery.Status.ACTIVE);
        } else {
            lottery.setStatus(Lottery.Status.ENDED);
        }
    }
}

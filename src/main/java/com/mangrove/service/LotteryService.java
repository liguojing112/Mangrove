package com.mangrove.service;

import com.mangrove.dto.request.LotteryRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Lottery;
import com.mangrove.entity.LotteryEntry;

import java.util.List;

public interface LotteryService {

    PageResult<Lottery> list(Integer page, Integer size);

    Lottery getById(Long id);

    Lottery create(LotteryRequest req, Long userId, String nickname);

    Lottery update(Long id, LotteryRequest req);

    void delete(Long id);

    Lottery toggleStatus(Long id, Integer statusFlag);

    Lottery draw(Long id);

    PageResult<LotteryEntry> listEntries(Long lotteryId, Integer page, Integer size);

    LotteryEntry enter(Long lotteryId, Long userId, String nickname);

    boolean hasEntered(Long lotteryId, Long userId);

    List<Lottery> listPublic();
}

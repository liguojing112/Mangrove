package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.*;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
public class UserTreeController {

    private final UserTreeRepository userTreeRepository;
    private final SysUserRepository sysUserRepository;
    private final CheckinRecordRepository checkinRecordRepository;
    private final TreeDecorationRepository treeDecorationRepository;
    private final UserDecorationRepository userDecorationRepository;
    private final TreeJournalRepository treeJournalRepository;

    @GetMapping("/my")
    public Result<UserTree> getMyTree(Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));
        return Result.success(tree);
    }

    @PutMapping("/my")
    @Transactional
    public Result<UserTree> updateMyTree(@RequestBody Map<String, String> body, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));
        String name = body.get("treeName");
        if (name != null) {
            tree.setTreeName(name.isBlank() ? null : name.trim().substring(0, Math.min(name.trim().length(), 20)));
        }
        userTreeRepository.save(tree);
        return Result.success(tree);
    }

    @PostMapping("/checkin")
    @Transactional
    public Result<UserTree> checkin(Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        LocalDate today = LocalDate.now();

        if (checkinRecordRepository.findByUserIdAndCheckinDate(user.getId(), today).isPresent()) {
            throw new BusinessException("今日已签到");
        }

        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));

        // 连续签到判定
        LocalDate yesterday = today.minusDays(1);
        if (checkinRecordRepository.findByUserIdAndCheckinDate(user.getId(), yesterday).isPresent()) {
            tree.setConsecutiveDays(tree.getConsecutiveDays() + 1);
        } else {
            tree.setConsecutiveDays(1);
        }

        // 记录签到
        CheckinRecord record = CheckinRecord.builder()
                .user(user)
                .checkinDate(today)
                .expEarned(10)
                .build();
        checkinRecordRepository.save(record);

        // 累计签到天数（只增不减）
        tree.setTotalCheckins(tree.getTotalCheckins() + 1);

        // 经验 + 积分
        int basePoints = 10;
        int bonusPoints = 0;
        int consecutive = tree.getConsecutiveDays();
        if (consecutive % 30 == 0) {
            bonusPoints = 200;
        } else if (consecutive % 7 == 0) {
            bonusPoints = 50;
        }
        int totalPointsEarned = basePoints + bonusPoints;

        tree.setExperience(tree.getExperience() + 10);
        tree.setPoints(tree.getPoints() + totalPointsEarned);
        tree.setTotalPoints(tree.getTotalPoints() + totalPointsEarned);
        tree.setLastCheckinAt(today);

        // 自动升级
        int requiredExp = tree.getLevel() * 100;
        while (tree.getExperience() >= requiredExp) {
            tree.setExperience(tree.getExperience() - requiredExp);
            tree.setLevel(tree.getLevel() + 1);
            requiredExp = tree.getLevel() * 100;
        }

        userTreeRepository.save(tree);
        return Result.success(tree);
    }

    @GetMapping("/decorations")
    public Result<List<TreeDecoration>> listDecorations() {
        List<TreeDecoration> decorations = treeDecorationRepository.findAll();
        return Result.success(decorations);
    }

    @PostMapping("/decorations/{id}/equip")
    @Transactional
    public Result<String> equipDecoration(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);

        TreeDecoration decoration = treeDecorationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "装扮不存在"));

        UserDecoration userDeco = userDecorationRepository
                .findByUserIdAndDecorationId(user.getId(), id)
                .orElseThrow(() -> new BusinessException(ResultCode.FORBIDDEN, "您还未拥有此装扮"));

        List<UserDecoration> sameType = userDecorationRepository
                .findByUserIdAndIsEquipped(user.getId(), 1);
        for (UserDecoration ud : sameType) {
            if (ud.getDecoration().getType() == decoration.getType()) {
                ud.setIsEquipped(0);
                userDecorationRepository.save(ud);
            }
        }

        userDeco.setIsEquipped(1);
        userDecorationRepository.save(userDeco);

        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));
        tree.setTreeDecoration("{\"" + decoration.getType().name().toLowerCase() + "\":\"" + id + "\"}");
        userTreeRepository.save(tree);

        return Result.success("装备成功");
    }

    // ========== 成长记事 CRUD ==========

    @GetMapping("/journal")
    public Result<Page<TreeJournal>> listJournals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        Page<TreeJournal> journals = treeJournalRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        return Result.success(journals);
    }

    @PostMapping("/journal")
    @Transactional
    public Result<TreeJournal> createJournal(@RequestBody Map<String, String> body, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        String title = body.get("title");
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "记事内容不能为空");
        }
        TreeJournal journal = TreeJournal.builder()
                .user(user)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        journal = treeJournalRepository.save(journal);
        return Result.success(journal);
    }

    @DeleteMapping("/journal/{id}")
    @Transactional
    public Result<Void> deleteJournal(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        TreeJournal journal = treeJournalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "记事不存在"));
        if (!journal.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除他人的记事");
        }
        treeJournalRepository.deleteById(id);
        return Result.success(null);
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

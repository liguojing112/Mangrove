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
    private final com.mangrove.repository.BarrageMessageRepository barrageMessageRepository;

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
        return doCheckin(user);
    }

    @PostMapping("/checkin-for/{username}")
    @Transactional
    public Result<String> checkinFor(@PathVariable String username, Authentication authentication) {
        SysUser helper = getCurrentUser(authentication);
        SysUser target = sysUserRepository.findByUsername(username)
                .orElseGet(() -> sysUserRepository.findByNickname(username)
                        .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在")));
        if (target.getId().equals(helper.getId())) {
            throw new BusinessException("请直接使用自己的签到按钮");
        }
        doCheckin(target);
        return Result.success("成功帮 " + target.getNickname() + " 签到！");
    }

    private Result<UserTree> doCheckin(SysUser user) {
        LocalDate today = LocalDate.now();

        if (checkinRecordRepository.findByUserIdAndCheckinDate(user.getId(), today).isPresent()) {
            throw new BusinessException("今日已签到");
        }

        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));

        // 连续签到判定
        LocalDate yesterday = today.minusDays(1);
        int prevConsecutive = tree.getConsecutiveDays() != null ? tree.getConsecutiveDays() : 0;
        if (checkinRecordRepository.findByUserIdAndCheckinDate(user.getId(), yesterday).isPresent()) {
            tree.setConsecutiveDays(prevConsecutive + 1);
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
        int prev = tree.getTotalCheckins() != null ? tree.getTotalCheckins() : 0;
        tree.setTotalCheckins(prev + 1);

        // 经验 + 积分
        int basePoints = 10;
        int bonusPoints = 0;
        int consecutive = tree.getConsecutiveDays() != null ? tree.getConsecutiveDays() : 1;
        if (consecutive % 30 == 0) {
            bonusPoints = 200;
        } else if (consecutive % 7 == 0) {
            bonusPoints = 50;
        }
        int totalPointsEarned = basePoints + bonusPoints;

        tree.setExperience((tree.getExperience() != null ? tree.getExperience() : 0) + 10);
        tree.setPoints((tree.getPoints() != null ? tree.getPoints() : 0) + totalPointsEarned);
        tree.setTotalPoints((tree.getTotalPoints() != null ? tree.getTotalPoints() : 0) + totalPointsEarned);
        tree.setLastCheckinAt(today);

        // 自动升级
        Long currentExp = tree.getExperience() != null ? tree.getExperience() : 0L;
        int currentLevel = tree.getLevel() != null ? tree.getLevel() : 1;
        long requiredExp = (long) currentLevel * 100;
        while (currentExp >= requiredExp) {
            currentExp -= requiredExp;
            currentLevel++;
            requiredExp = (long) currentLevel * 100;
        }
        tree.setExperience(currentExp);
        tree.setLevel(currentLevel);

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

    @GetMapping("/recent-checkins")
    public Result<List<Map<String, String>>> recentCheckins() {
        PageRequest top20 = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        List<CheckinRecord> records = checkinRecordRepository.findAll(top20).getContent();
        List<Map<String, String>> names = records.stream()
                .map(r -> Map.of("nickname", r.getUser().getNickname(), "publicId", r.getUser().getPublicId()))
                .collect(java.util.stream.Collectors.toList());
        return Result.success(names);
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> ranking(Authentication authentication) {
        // 任何登录用户都可以查看排行榜
        if (authentication == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        List<UserTree> trees = userTreeRepository.findTop20ByOrderByTotalPointsDesc();
        List<Map<String, Object>> list = trees.stream()
                .map(t -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("nickname", t.getUser().getNickname());
                    item.put("publicId", t.getUser().getPublicId());
                    item.put("totalPoints", t.getTotalPoints());
                    item.put("totalCheckins", t.getTotalCheckins());
                    item.put("consecutiveDays", t.getConsecutiveDays());
                    item.put("level", t.getLevel());
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
        return Result.success(list);
    }

    // ========== 弹幕 ==========
    @GetMapping("/barrage")
    public Result<List<Map<String, Object>>> getBarrage() {
        List<BarrageMessage> messages = barrageMessageRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 30));
        List<Map<String, Object>> list = messages.stream()
                .map(m -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", m.getId());
                    item.put("content", m.getContent());
                    item.put("nickname", m.getNickname());
                    item.put("publicId", m.getPublicId() != null ? m.getPublicId() : "");
                    item.put("likeCount", m.getLikeCount() != null ? m.getLikeCount() : 0);
                    item.put("userId", m.getUserId());
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
        return Result.success(list);
    }

    @PostMapping("/barrage")
    public Result<Void> postBarrage(@RequestBody Map<String, String> body, Authentication authentication) {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "弹幕内容不能为空");
        }
        if (content.length() > 100) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "弹幕内容最多100字");
        }

        SysUser user = getCurrentUser(authentication);
        BarrageMessage msg = BarrageMessage.builder()
                .content(content.trim())
                .nickname(user.getNickname())
                .publicId(user.getPublicId())
                .userId(user.getId())
                .likeCount(0)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        barrageMessageRepository.save(msg);
        return Result.success(null);
    }

    @PostMapping("/barrage/{id}/like")
    public Result<Void> likeBarrage(@PathVariable Long id, Authentication authentication) {
        BarrageMessage msg = barrageMessageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "弹幕不存在"));
        msg.setLikeCount(msg.getLikeCount() + 1);
        barrageMessageRepository.save(msg);
        return Result.success(null);
    }

    @DeleteMapping("/barrage/{id}")
    public Result<Void> deleteBarrage(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        BarrageMessage msg = barrageMessageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "弹幕不存在"));
        // 只有本人或管理员可删
        boolean isOwner = msg.getUserId() != null && msg.getUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == SysUser.Role.ADMIN || user.getRole() == SysUser.Role.SUPER_ADMIN;
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此弹幕");
        }
        barrageMessageRepository.deleteById(id);
        return Result.success(null);
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

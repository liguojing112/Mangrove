package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.entity.*;
import com.mangrove.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
public class UserTreeController {

    private final UserTreeRepository userTreeRepository;
    private final SysUserRepository sysUserRepository;
    private final CheckinRecordRepository checkinRecordRepository;
    private final TreeDecorationRepository treeDecorationRepository;
    private final UserDecorationRepository userDecorationRepository;

    @GetMapping("/my")
    public Result<UserTree> getMyTree(Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        UserTree tree = userTreeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "芒果树不存在"));
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

        CheckinRecord record = CheckinRecord.builder()
                .user(user)
                .checkinDate(today)
                .expEarned(10)
                .build();
        checkinRecordRepository.save(record);

        tree.setExperience(tree.getExperience() + 10);

        LocalDate yesterday = today.minusDays(1);
        if (checkinRecordRepository.findByUserIdAndCheckinDate(user.getId(), yesterday).isPresent()) {
            tree.setConsecutiveDays(tree.getConsecutiveDays() + 1);
        } else {
            tree.setConsecutiveDays(1);
        }

        tree.setLastCheckinAt(today);

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

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

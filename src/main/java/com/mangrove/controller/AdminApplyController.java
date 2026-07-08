package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.DeleteApplyRequest;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminApplyController {

    private final ContentService contentService;
    private final SysUserRepository sysUserRepository;

    /** 管理员申请删除内容（插入审核日志，待超级管理员审核） */
    @PostMapping("/apply-delete")
    public Result<String> applyDelete(@Valid @RequestBody DeleteApplyRequest req,
                                    Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        contentService.applyDelete(req.getAssetId(), user.getId());
        return Result.success("整理申请已成功提交，正在等待超级管理员审核，请耐心等待。审核结果将在 24 小时内通知。");
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.AuditRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.dto.response.PendingAuditItem;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final ContentService contentService;
    private final SysUserRepository sysUserRepository;

    /** 获取所有待审核的内容列表（type: upload/delete） */
    @GetMapping("/pending-list")
    public Result<PageResult<PendingAuditItem>> pendingList(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "upload") String type) {
        return Result.success(contentService.getPendingList(page, size, type));
    }

    /** 审核（同意/驳回） */
    @PostMapping("/audit")
    public Result<String> audit(@Valid @RequestBody AuditRequest req,
                              Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        contentService.audit(req.getAuditId(), req.getAction(), req.getComment(), user.getId());
        String msg = req.getAction() == 1 ? "审核通过" : "审核驳回";
        return Result.success(msg);
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

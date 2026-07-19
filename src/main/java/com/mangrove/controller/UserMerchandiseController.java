package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.MerchandiseRequest;
import com.mangrove.entity.Merchandise;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.MerchandiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/merchandise")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserMerchandiseController {

    private final MerchandiseService merchandiseService;
    private final SysUserRepository sysUserRepository;

    @GetMapping
    public Result<List<Merchandise>> mine() {
        return Result.success(merchandiseService.listMine());
    }

    @PostMapping
    public Result<Merchandise> create(@Valid @RequestBody MerchandiseRequest request) {
        return Result.success(merchandiseService.create(request));
    }

    @PutMapping("/{id}")
    public Result<Merchandise> update(@PathVariable Long id, @Valid @RequestBody MerchandiseRequest request, Authentication auth) {
        Merchandise merchandise = merchandiseService.getById(id);
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        if (!merchandise.getProducerId().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能编辑自己的周边");
        }
        return Result.success(merchandiseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        Merchandise merchandise = merchandiseService.getById(id);
        SysUser user = sysUserRepository.findByUsername(auth.getName()).orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        if (!merchandise.getProducerId().equals(user.getId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能删除自己的周边");
        }
        merchandiseService.delete(id);
        return Result.success();
    }
}

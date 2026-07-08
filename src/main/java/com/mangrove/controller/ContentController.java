package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.ContentUploadRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.MediaAsset;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.MediaAssetRepository;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final SysUserRepository sysUserRepository;
    private final MediaAssetRepository mediaAssetRepository;

    /** 前台获取已审核通过的内容 */
    @GetMapping
    public Result<List<MediaAsset>> listPublished(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            try {
                MediaAsset.Category cat = MediaAsset.Category.valueOf(category.toUpperCase());
                return Result.success(mediaAssetRepository.findPublishedByCategory(cat));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的分类参数");
            }
        }
        return Result.success(mediaAssetRepository.findPublished());
    }

    /** 上传内容（普通用户 → 待审核；管理员 → 待审核，等待超级管理员确认） */
    @PostMapping("/upload")
    public Result<MediaAsset> upload(@Valid @RequestBody ContentUploadRequest req,
                                     Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        try {
            MediaAsset.Category category = MediaAsset.Category.valueOf(req.getCategory().toUpperCase());
            MediaAsset asset = contentService.upload(
                    req.getTitle(), req.getFileUrl(), req.getThumbnailUrl(),
                    category, req.getDescription(), user.getId(), req.getPhotoCategoryId());
            return Result.success("您的投稿已成功提交，正在等待管理员审核，请耐心等待。审核结果将在 24 小时内通知。", asset);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的分类: 可选 PHOTO/VIDEO/WORK");
        }
    }

    /** 删除内容（管理员/超管直接软删除） */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        if (user.getRole() != SysUser.Role.ADMIN && user.getRole() != SysUser.Role.SUPER_ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
        MediaAsset asset = mediaAssetRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));
        asset.setIsDeleted(1);
        asset.setUpdatedAt(java.time.LocalDateTime.now());
        mediaAssetRepository.save(asset);
        return Result.success("已删除");
    }

    /** 编辑内容标题和描述 */
    @PutMapping("/{id}")
    public Result<MediaAsset> update(@PathVariable Long id,
                                      @RequestBody ContentUploadRequest req,
                                      Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        if (user.getRole() != SysUser.Role.ADMIN && user.getRole() != SysUser.Role.SUPER_ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限");
        }
        MediaAsset asset = mediaAssetRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));
        asset.setTitle(req.getTitle());
        asset.setDescription(req.getDescription());
        asset.setUpdatedAt(java.time.LocalDateTime.now());
        return Result.success(mediaAssetRepository.save(asset));
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

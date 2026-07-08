package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.dto.response.PendingAuditItem;
import com.mangrove.entity.AuditLog;
import com.mangrove.entity.MediaAsset;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.AuditLogRepository;
import com.mangrove.repository.MediaAssetRepository;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final MediaAssetRepository mediaAssetRepository;
    private final AuditLogRepository auditLogRepository;
    private final SysUserRepository sysUserRepository;

    @Override
    @Transactional
    public MediaAsset upload(String title, String fileUrl, String thumbnailUrl,
                             MediaAsset.Category category, String description,
                             Long uploaderId, Long photoCategoryId) {
        SysUser uploader = sysUserRepository.findById(uploaderId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

        // 超级管理员上传直接通过，普通用户和管理员需要审核
        boolean isSuperAdmin = uploader.getRole() == SysUser.Role.SUPER_ADMIN;
        int status = isSuperAdmin ? 1 : 0;

        MediaAsset asset = MediaAsset.builder()
                .title(title)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .uploader(uploader)
                .category(category)
                .photoCategoryId(photoCategoryId)
                .description(description)
                .status(status)
                .isDeleted(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        asset = mediaAssetRepository.save(asset);

        AuditLog auditLog = AuditLog.builder()
                .action(AuditLog.AuditAction.UPLOAD)
                .target(asset)
                .initiator(uploader)
                .auditor(isSuperAdmin ? uploader : null)
                .auditStatus(isSuperAdmin ? 1 : 0)
                .auditComment(isSuperAdmin ? "超级管理员上传，自动通过" : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);

        log.info("内容上传成功: assetId={}, uploader={}, status={}",
                asset.getId(), uploader.getUsername(), status == 1 ? "已通过" : "待审核");
        return asset;
    }

    @Override
    @Transactional
    public void applyDelete(Long assetId, Long adminId) {
        MediaAsset asset = mediaAssetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "内容不存在"));

        if (asset.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该内容已被删除");
        }

        // 检查是否已存在待审核的删除申请
        auditLogRepository.findPendingDeleteByTarget(asset, AuditLog.AuditAction.DELETE).ifPresent(existing -> {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已存在对该内容的待审核删除申请");
        });

        SysUser admin = sysUserRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "管理员不存在"));

        AuditLog auditLog = AuditLog.builder()
                .action(AuditLog.AuditAction.DELETE)
                .target(asset)
                .initiator(admin)
                .auditStatus(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);

        log.info("删除申请已提交，待超级管理员审核: assetId={}, adminId={}", assetId, adminId);
    }

    @Override
    public PageResult<PendingAuditItem> getPendingList(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if ("delete".equals(type)) {
            Page<MediaAsset> assets = mediaAssetRepository.findPendingDeletions(AuditLog.AuditAction.DELETE, pageable);
            List<PendingAuditItem> items = new ArrayList<>();
            for (MediaAsset asset : assets.getContent()) {
                AuditLog deleteLog = auditLogRepository.findPendingDeleteByTarget(asset, AuditLog.AuditAction.DELETE).orElse(null);
                items.add(PendingAuditItem.builder()
                        .auditLogId(deleteLog != null ? deleteLog.getId() : null)
                        .assetId(asset.getId())
                        .title(asset.getTitle())
                        .fileUrl(asset.getFileUrl())
                        .thumbnailUrl(asset.getThumbnailUrl())
                        .category(asset.getCategory().name())
                        .action("DELETE")
                        .uploaderId(asset.getUploader().getId())
                        .uploaderName(asset.getUploader().getNickname())
                        .initiatorId(deleteLog != null ? deleteLog.getInitiator().getId() : null)
                        .initiatorName(deleteLog != null ? deleteLog.getInitiator().getNickname() : null)
                        .description(asset.getDescription())
                        .createdAt(asset.getCreatedAt())
                        .build());
            }
            return PageResult.<PendingAuditItem>builder()
                    .content(items)
                    .totalElements(assets.getTotalElements())
                    .totalPages(assets.getTotalPages())
                    .currentPage(assets.getNumber())
                    .size(assets.getSize())
                    .build();
        } else {
            Page<MediaAsset> assets = mediaAssetRepository.findPendingUploads(pageable);
            List<PendingAuditItem> items = new ArrayList<>();
            for (MediaAsset asset : assets.getContent()) {
                AuditLog uploadLog = auditLogRepository
                        .findPendingByTargetAndAction(asset, AuditLog.AuditAction.UPLOAD)
                        .orElse(null);
                items.add(PendingAuditItem.builder()
                        .auditLogId(uploadLog != null ? uploadLog.getId() : null)
                        .assetId(asset.getId())
                        .title(asset.getTitle())
                        .fileUrl(asset.getFileUrl())
                        .thumbnailUrl(asset.getThumbnailUrl())
                        .category(asset.getCategory().name())
                        .action("UPLOAD")
                        .uploaderId(asset.getUploader().getId())
                        .uploaderName(asset.getUploader().getNickname())
                        .description(asset.getDescription())
                        .createdAt(asset.getCreatedAt())
                        .build());
            }
            return PageResult.<PendingAuditItem>builder()
                    .content(items)
                    .totalElements(assets.getTotalElements())
                    .totalPages(assets.getTotalPages())
                    .currentPage(assets.getNumber())
                    .size(assets.getSize())
                    .build();
        }
    }

    @Override
    @Transactional
    public void audit(Long auditLogId, int action, String comment, Long auditorId) {
        AuditLog auditLog = auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "审核记录不存在"));

        if (auditLog.getAuditStatus() != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该审核记录已处理");
        }

        SysUser auditor = sysUserRepository.findById(auditorId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "审核人不存在"));

        auditLog.setAuditor(auditor);
        auditLog.setAuditComment(comment);
        auditLog.setUpdatedAt(LocalDateTime.now());

        MediaAsset asset = auditLog.getTarget();

        if (action == 1) { // 同意
            auditLog.setAuditStatus(1);
            if (auditLog.getAction() == AuditLog.AuditAction.UPLOAD) {
                // 同意上传 → 内容状态设为已通过
                asset.setStatus(1);
                asset.setRejectReason(null);
                asset.setUpdatedAt(LocalDateTime.now());
                mediaAssetRepository.save(asset);
                log.info("审核通过(上传): assetId={}, auditorId={}", asset.getId(), auditorId);
            } else if (auditLog.getAction() == AuditLog.AuditAction.DELETE) {
                // 同意删除 → 软删除内容
                asset.setIsDeleted(1);
                asset.setUpdatedAt(LocalDateTime.now());
                mediaAssetRepository.save(asset);
                log.info("审核通过(删除): assetId={}, auditorId={}", asset.getId(), auditorId);
            }
        } else if (action == 2) { // 驳回
            auditLog.setAuditStatus(2);
            if (auditLog.getAction() == AuditLog.AuditAction.UPLOAD) {
                // 驳回上传 → 内容状态设为已驳回
                asset.setStatus(2);
                asset.setRejectReason(comment);
                asset.setUpdatedAt(LocalDateTime.now());
                mediaAssetRepository.save(asset);
                log.info("审核驳回(上传): assetId={}, reason={}", asset.getId(), comment);
            }
            // 驳回删除 → 日志状态改为驳回，内容保持正常
            log.info("审核驳回(删除): assetId={}, reason={}", asset.getId(), comment);
        } else {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的审核动作: action 必须为 1(同意) 或 2(驳回)");
        }

        auditLogRepository.save(auditLog);
    }
}

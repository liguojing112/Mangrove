package com.mangrove.service;

import com.mangrove.dto.response.PageResult;
import com.mangrove.dto.response.PendingAuditItem;
import com.mangrove.entity.MediaAsset;

public interface ContentService {

    /** 上传内容（普通用户/管理员均可调用） */
    MediaAsset upload(String title, String fileUrl, String thumbnailUrl,
                      MediaAsset.Category category, String description,
                      Long uploaderId, Long photoCategoryId);

    /** 管理员申请删除（插入审核日志） */
    void applyDelete(Long assetId, Long adminId);

    /** 超级管理员：获取所有待审核内容的分页列表（type: upload/delete） */
    PageResult<PendingAuditItem> getPendingList(int page, int size, String type);

    /** 超级管理员：审核（通过/驳回） */
    void audit(Long auditLogId, int action, String comment, Long auditorId);
}

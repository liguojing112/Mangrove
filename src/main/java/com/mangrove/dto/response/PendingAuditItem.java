package com.mangrove.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingAuditItem {
    private Long auditLogId;       // 审核日志ID（用于审核接口）
    private Long assetId;          // 资产ID
    private String title;          // 标题
    private String fileUrl;        // 文件URL
    private String thumbnailUrl;   // 缩略图
    private String category;       // 分类
    private String action;         // 审核动作类型: UPLOAD / DELETE
    private Long uploaderId;       // 上传者ID
    private String uploaderName;   // 上传者昵称
    private Long initiatorId;      // 发起人ID（删除申请场景下的管理员ID）
    private String initiatorName;  // 发起人昵称
    private String description;    // 描述
    private LocalDateTime createdAt;
}

package com.mangrove.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequest {
    @NotNull(message = "审核日志ID不能为空")
    private Long auditId;

    @NotNull(message = "审核动作不能为空")
    private Integer action;  // 1-同意, 2-驳回

    private String comment;  // 审核备注/驳回理由
}

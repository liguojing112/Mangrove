package com.mangrove.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteApplyRequest {
    @NotNull(message = "内容ID不能为空")
    private Long assetId;
}

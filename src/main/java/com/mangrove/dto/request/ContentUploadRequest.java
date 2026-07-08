package com.mangrove.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentUploadRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    private String thumbnailUrl;

    @NotNull(message = "分类不能为空")
    private String category;

    private String description;

    private Long photoCategoryId;
}

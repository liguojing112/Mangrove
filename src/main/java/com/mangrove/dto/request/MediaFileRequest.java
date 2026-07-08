package com.mangrove.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFileRequest {

    @NotNull(message = "所属相册ID不能为空")
    private Long albumId;

    private String title;

    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    private String fileType;

    private Long fileSize;

    private String thumbnailUrl;

    private Integer sortOrder;
}

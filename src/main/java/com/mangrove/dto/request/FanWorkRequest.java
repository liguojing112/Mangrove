package com.mangrove.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FanWorkRequest {

    @NotBlank(message = "作品标题不能为空")
    private String title;

    private String description;

    @NotBlank(message = "作品分类不能为空")
    private String category;

    @NotBlank(message = "作品文件URL不能为空")
    private String fileUrl;

    private String coverUrl;

    private Integer duration;

    private Long artistId;
}

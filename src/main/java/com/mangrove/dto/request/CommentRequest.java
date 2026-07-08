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
public class CommentRequest {

    @NotBlank(message = "评论目标类型不能为空")
    private String targetType;

    @NotNull(message = "评论目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;
}

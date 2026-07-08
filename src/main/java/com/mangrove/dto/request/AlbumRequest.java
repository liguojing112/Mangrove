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
public class AlbumRequest {

    @NotBlank(message = "相册标题不能为空")
    private String title;

    private String description;

    private String coverUrl;

    private String category;

    private Long artistId;
}

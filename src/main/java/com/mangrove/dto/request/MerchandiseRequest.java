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
public class MerchandiseRequest {

    @NotBlank(message = "周边名称不能为空")
    private String name;

    @NotBlank(message = "周边分类不能为空")
    private String category;

    private String subCategory;

    private String description;

    @NotBlank(message = "高清图URL不能为空")
    private String highResImageUrl;

    private String thumbnailUrl;

    private String tags;

    private Long artistId;

    private String publishDate;

    private String rarity;
}

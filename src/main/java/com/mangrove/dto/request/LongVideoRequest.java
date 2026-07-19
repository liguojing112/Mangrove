package com.mangrove.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LongVideoRequest {
    private String title;
    private String category;
    private String description;
    private String localVideoUrl;
    private String bilibiliUrl;
    private String coverUrl;
    private LocalDate publishedDate;
    private Integer sortOrder;
    private Integer status;
}

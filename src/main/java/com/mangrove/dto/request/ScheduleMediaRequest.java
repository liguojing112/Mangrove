package com.mangrove.dto.request;

import lombok.Data;

@Data
public class ScheduleMediaRequest {
    private String mediaType;
    private String mediaUrl;
    private String title;
    private Integer sortOrder;
}

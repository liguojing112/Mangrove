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
public class ScheduleRequest {

    @NotBlank(message = "行程标题不能为空")
    private String title;

    private String description;

    private String scheduleType;

    private String location;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    private String endTime;

    private Long artistId;

    private String coverUrl;
}

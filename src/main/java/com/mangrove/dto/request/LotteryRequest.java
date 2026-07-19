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
public class LotteryRequest {

    @NotBlank(message = "抽奖标题不能为空")
    private String title;

    private String description;

    private String imageUrl;

    private String conditions;

    @NotNull(message = "中奖人数不能为空")
    private Integer winnerCount;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;
}

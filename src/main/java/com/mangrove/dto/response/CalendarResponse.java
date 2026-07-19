package com.mangrove.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarResponse {

    private Long aliveDays;
    private Long loveDays;
    private Long debutDays;
    private Long nextBirthdayDays;
    private String nextBirthdayDate;
    private String birthDate;
    private String debutDate;
    private String loveStartDate;
}

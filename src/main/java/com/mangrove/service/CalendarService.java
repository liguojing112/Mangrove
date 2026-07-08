package com.mangrove.service;

import com.mangrove.dto.response.CalendarResponse;

public interface CalendarService {

    CalendarResponse getDays(Long userId, Long artistId);
}

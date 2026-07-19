package com.mangrove.dto.request;

import lombok.Data;

@Data
public class MusicTrackCatalogRequest {
    private String title;
    private String category;
    private String localAudioUrl;
    private String qqMusicUrl;
    private String coverUrl;
    private Integer durationSeconds;
    private Integer sortOrder;
    private Integer status;
}

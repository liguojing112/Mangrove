package com.mangrove.dto.request;

import lombok.Data;

@Data
public class MusicPhotoAlbumRequest {
    private String title;
    private String description;
    private String coverUrl;
    private Integer sortOrder;
    private Integer status;
}

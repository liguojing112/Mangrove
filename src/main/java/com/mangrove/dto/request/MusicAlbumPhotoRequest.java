package com.mangrove.dto.request;

import lombok.Data;

@Data
public class MusicAlbumPhotoRequest {
    private String imageUrl;
    private String caption;
    private Integer sortOrder;
}

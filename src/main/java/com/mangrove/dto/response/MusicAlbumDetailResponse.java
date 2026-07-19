package com.mangrove.dto.response;

import com.mangrove.entity.MusicAlbumPhoto;
import com.mangrove.entity.MusicPhotoAlbum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MusicAlbumDetailResponse {
    private MusicPhotoAlbum album;
    private List<MusicAlbumPhoto> photos;
}

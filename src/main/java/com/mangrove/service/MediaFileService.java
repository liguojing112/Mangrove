package com.mangrove.service;

import com.mangrove.dto.request.MediaFileRequest;
import com.mangrove.entity.MediaFile;

import java.util.List;

public interface MediaFileService {

    List<MediaFile> listByAlbum(Long albumId);

    MediaFile getById(Long id);

    MediaFile create(MediaFileRequest req);

    MediaFile update(Long id, MediaFileRequest req);

    void delete(Long id);

    void deleteByAlbum(Long albumId);
}

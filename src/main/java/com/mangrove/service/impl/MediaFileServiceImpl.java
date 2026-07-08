package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.MediaFileRequest;
import com.mangrove.entity.Album;
import com.mangrove.entity.MediaFile;
import com.mangrove.repository.AlbumRepository;
import com.mangrove.repository.MediaFileRepository;
import com.mangrove.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFileRepository mediaFileRepository;
    private final AlbumRepository albumRepository;

    @Override
    public List<MediaFile> listByAlbum(Long albumId) {
        return mediaFileRepository.findByAlbumIdOrderBySortOrderAsc(albumId);
    }

    @Override
    public MediaFile getById(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public MediaFile create(MediaFileRequest req) {
        Album album = albumRepository.findById(req.getAlbumId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        MediaFile mediaFile = MediaFile.builder()
                .album(album)
                .title(req.getTitle())
                .fileUrl(req.getFileUrl())
                .fileType(req.getFileType())
                .fileSize(req.getFileSize())
                .thumbnailUrl(req.getThumbnailUrl())
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .uploadDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        return mediaFileRepository.save(mediaFile);
    }

    @Override
    @Transactional
    public MediaFile update(Long id, MediaFileRequest req) {
        MediaFile mediaFile = getById(id);
        if (req.getTitle() != null) {
            mediaFile.setTitle(req.getTitle());
        }
        if (req.getFileUrl() != null) {
            mediaFile.setFileUrl(req.getFileUrl());
        }
        if (req.getFileType() != null) {
            mediaFile.setFileType(req.getFileType());
        }
        if (req.getFileSize() != null) {
            mediaFile.setFileSize(req.getFileSize());
        }
        if (req.getThumbnailUrl() != null) {
            mediaFile.setThumbnailUrl(req.getThumbnailUrl());
        }
        if (req.getSortOrder() != null) {
            mediaFile.setSortOrder(req.getSortOrder());
        }
        if (req.getAlbumId() != null) {
            Album album = albumRepository.findById(req.getAlbumId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            mediaFile.setAlbum(album);
        }
        return mediaFileRepository.save(mediaFile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!mediaFileRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        mediaFileRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByAlbum(Long albumId) {
        mediaFileRepository.deleteByAlbumId(albumId);
    }
}

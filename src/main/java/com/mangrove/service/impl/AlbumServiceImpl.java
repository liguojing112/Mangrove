package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.AlbumRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Album;
import com.mangrove.entity.Artist;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.AlbumRepository;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SysUserRepository sysUserRepository;

    @Override
    public PageResult<Album> list(Integer page, Integer size, String category) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (category != null && !category.isBlank()) {
            List<Album> allFiltered = albumRepository.findByStatusAndCategory(
                    1, Album.Category.valueOf(category), Pageable.unpaged());
            return buildPageResult(allFiltered, page, size);
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Album> albumPage = albumRepository.findAll(pageable);
        return PageResult.of(albumPage);
    }

    @Override
    public Album getById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public Album create(AlbumRequest req, Long userId) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        Album album = Album.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .coverUrl(req.getCoverUrl())
                .status(1)
                .viewCount(0)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
        if (req.getCategory() != null && !req.getCategory().isBlank()) {
            album.setCategory(Album.Category.valueOf(req.getCategory()));
        }
        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            album.setArtist(artist);
        }
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public Album update(Long id, AlbumRequest req) {
        Album album = getById(id);
        if (req.getTitle() != null) {
            album.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            album.setDescription(req.getDescription());
        }
        if (req.getCoverUrl() != null) {
            album.setCoverUrl(req.getCoverUrl());
        }
        if (req.getCategory() != null && !req.getCategory().isBlank()) {
            album.setCategory(Album.Category.valueOf(req.getCategory()));
        }
        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            album.setArtist(artist);
        }
        album.setUpdatedAt(LocalDateTime.now());
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        albumRepository.deleteById(id);
    }

    private PageResult<Album> buildPageResult(List<Album> allFiltered, int page, int size) {
        int totalSize = allFiltered.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalSize);
        List<Album> pagedContent = fromIndex < totalSize
                ? allFiltered.subList(fromIndex, toIndex)
                : List.of();
        int totalPages = totalSize == 0 ? 0 : (int) Math.ceil((double) totalSize / size);
        return PageResult.<Album>builder()
                .content(pagedContent)
                .totalElements(totalSize)
                .totalPages(totalPages)
                .currentPage(page)
                .size(size)
                .build();
    }
}

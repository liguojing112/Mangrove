package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.ArtistRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.service.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Override
    public PageResult<Artist> list(Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "sortOrder").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Artist> artistPage = artistRepository.findAll(pageable);
        return PageResult.of(artistPage);
    }

    @Override
    public List<Artist> listAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "sortOrder");
        return artistRepository.findAll(sort);
    }

    @Override
    public Artist getById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public Artist create(ArtistRequest req) {
        Artist artist = Artist.builder()
                .name(req.getName())
                .stageName(req.getStageName())
                .nationality(req.getNationality())
                .bio(req.getBio())
                .company(req.getCompany())
                .avatarUrl(req.getAvatarUrl())
                .coverUrl(req.getCoverUrl())
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (req.getBirthDate() != null && !req.getBirthDate().isBlank()) {
            artist.setBirthDate(LocalDate.parse(req.getBirthDate()));
        }
        if (req.getDebutDate() != null && !req.getDebutDate().isBlank()) {
            artist.setDebutDate(LocalDate.parse(req.getDebutDate()));
        }

        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public Artist update(Long id, ArtistRequest req) {
        Artist artist = getById(id);
        if (req.getName() != null) {
            artist.setName(req.getName());
        }
        if (req.getStageName() != null) {
            artist.setStageName(req.getStageName());
        }
        if (req.getBirthDate() != null && !req.getBirthDate().isBlank()) {
            artist.setBirthDate(LocalDate.parse(req.getBirthDate()));
        }
        if (req.getDebutDate() != null && !req.getDebutDate().isBlank()) {
            artist.setDebutDate(LocalDate.parse(req.getDebutDate()));
        }
        if (req.getNationality() != null) {
            artist.setNationality(req.getNationality());
        }
        if (req.getBio() != null) {
            artist.setBio(req.getBio());
        }
        if (req.getCompany() != null) {
            artist.setCompany(req.getCompany());
        }
        if (req.getAvatarUrl() != null) {
            artist.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getCoverUrl() != null) {
            artist.setCoverUrl(req.getCoverUrl());
        }
        if (req.getSortOrder() != null) {
            artist.setSortOrder(req.getSortOrder());
        }
        artist.setUpdatedAt(LocalDateTime.now());
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        artistRepository.deleteById(id);
    }
}

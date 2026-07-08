package com.mangrove.service.impl;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.MerchandiseRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;
import com.mangrove.entity.Merchandise;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.MerchandiseRepository;
import com.mangrove.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;
    private final ArtistRepository artistRepository;

    @Override
    public PageResult<Merchandise> list(Integer page, Integer size, String category, String rarity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasRarity = rarity != null && !rarity.isBlank();

        if (hasCategory && hasRarity) {
            List<Merchandise> allFiltered = merchandiseRepository
                    .findByCategoryAndStatus(Merchandise.Category.valueOf(category), 1, Pageable.unpaged());
            allFiltered = allFiltered.stream()
                    .filter(m -> m.getRarity() == Merchandise.Rarity.valueOf(rarity))
                    .toList();
            return buildPageResult(allFiltered, page, size);
        } else if (hasCategory) {
            List<Merchandise> allFiltered = merchandiseRepository
                    .findByCategoryAndStatus(Merchandise.Category.valueOf(category), 1, Pageable.unpaged());
            return buildPageResult(allFiltered, page, size);
        } else if (hasRarity) {
            List<Merchandise> allFiltered = merchandiseRepository
                    .findByRarity(Merchandise.Rarity.valueOf(rarity), Pageable.unpaged());
            return buildPageResult(allFiltered, page, size);
        }

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Merchandise> merchPage = merchandiseRepository.findAll(pageable);
        return PageResult.of(merchPage);
    }

    @Override
    public Merchandise getById(Long id) {
        return merchandiseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public Merchandise create(MerchandiseRequest req) {
        Merchandise merchandise = Merchandise.builder()
                .name(req.getName())
                .description(req.getDescription())
                .highResImageUrl(req.getHighResImageUrl())
                .thumbnailUrl(req.getThumbnailUrl())
                .tags(req.getTags())
                .subCategory(req.getSubCategory())
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        merchandise.setCategory(Merchandise.Category.valueOf(req.getCategory()));

        if (req.getRarity() != null && !req.getRarity().isBlank()) {
            merchandise.setRarity(Merchandise.Rarity.valueOf(req.getRarity()));
        } else {
            merchandise.setRarity(Merchandise.Rarity.COMMON);
        }

        if (req.getPublishDate() != null && !req.getPublishDate().isBlank()) {
            merchandise.setPublishDate(LocalDate.parse(req.getPublishDate()));
        }

        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            merchandise.setArtist(artist);
        }

        return merchandiseRepository.save(merchandise);
    }

    @Override
    @Transactional
    public Merchandise update(Long id, MerchandiseRequest req) {
        Merchandise merchandise = getById(id);
        if (req.getName() != null) {
            merchandise.setName(req.getName());
        }
        if (req.getCategory() != null && !req.getCategory().isBlank()) {
            merchandise.setCategory(Merchandise.Category.valueOf(req.getCategory()));
        }
        if (req.getSubCategory() != null) {
            merchandise.setSubCategory(req.getSubCategory());
        }
        if (req.getDescription() != null) {
            merchandise.setDescription(req.getDescription());
        }
        if (req.getHighResImageUrl() != null) {
            merchandise.setHighResImageUrl(req.getHighResImageUrl());
        }
        if (req.getThumbnailUrl() != null) {
            merchandise.setThumbnailUrl(req.getThumbnailUrl());
        }
        if (req.getTags() != null) {
            merchandise.setTags(req.getTags());
        }
        if (req.getRarity() != null && !req.getRarity().isBlank()) {
            merchandise.setRarity(Merchandise.Rarity.valueOf(req.getRarity()));
        }
        if (req.getPublishDate() != null && !req.getPublishDate().isBlank()) {
            merchandise.setPublishDate(LocalDate.parse(req.getPublishDate()));
        }
        if (req.getArtistId() != null) {
            Artist artist = artistRepository.findById(req.getArtistId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            merchandise.setArtist(artist);
        }
        merchandise.setUpdatedAt(LocalDateTime.now());
        return merchandiseRepository.save(merchandise);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!merchandiseRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        merchandiseRepository.deleteById(id);
    }

    private PageResult<Merchandise> buildPageResult(List<Merchandise> allFiltered, int page, int size) {
        int totalSize = allFiltered.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalSize);
        List<Merchandise> pagedContent = fromIndex < totalSize
                ? allFiltered.subList(fromIndex, toIndex)
                : List.of();
        int totalPages = totalSize == 0 ? 0 : (int) Math.ceil((double) totalSize / size);
        return PageResult.<Merchandise>builder()
                .content(pagedContent)
                .totalElements(totalSize)
                .totalPages(totalPages)
                .currentPage(page)
                .size(size)
                .build();
    }
}

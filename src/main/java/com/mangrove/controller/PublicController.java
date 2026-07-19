package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Album;
import com.mangrove.entity.Artist;
import com.mangrove.entity.Merchandise;
import com.mangrove.entity.Schedule;
import com.mangrove.repository.AlbumRepository;
import com.mangrove.repository.ArtistRepository;
import com.mangrove.repository.FanWorkRepository;
import com.mangrove.repository.MerchandiseRepository;
import com.mangrove.repository.ScheduleRepository;
import com.mangrove.repository.SysConfigRepository;
import com.mangrove.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ArtistRepository artistRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final AlbumRepository albumRepository;
    private final ScheduleRepository scheduleRepository;
    private final SysUserRepository sysUserRepository;
    private final FanWorkRepository fanWorkRepository;
    private final SysConfigRepository sysConfigRepository;

    @GetMapping("/artists")
    public Result<List<Artist>> listArtists() {
        List<Artist> artists = artistRepository.findAllByStatus(1, Pageable.unpaged());
        return Result.success(artists);
    }

    @GetMapping("/merchandise")
    public Result<PageResult<Merchandise>> listMerchandise(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Merchandise> list;
        if (category != null && !category.isBlank()) {
            try {
                Merchandise.Category cat = Merchandise.Category.valueOf(category.toUpperCase());
                list = merchandiseRepository.findByCategoryAndStatus(cat, 1, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的周边分类");
            }
        } else {
            list = merchandiseRepository.findAll(pageable).getContent().stream()
                    .filter(m -> m.getStatus() == 1)
                    .toList();
        }
        PageResult<Merchandise> result = PageResult.<Merchandise>builder()
                .content(list)
                .totalElements(list.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @GetMapping("/albums")
    public Result<PageResult<Album>> listAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Album> list;
        if (category != null && !category.isBlank()) {
            try {
                Album.Category cat = Album.Category.valueOf(category.toUpperCase());
                list = albumRepository.findByStatusAndCategory(1, cat, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的相册分类");
            }
        } else {
            list = albumRepository.findAll(pageable).getContent().stream()
                    .filter(a -> a.getStatus() == 1)
                    .toList();
        }
        PageResult<Album> result = PageResult.<Album>builder()
                .content(list)
                .totalElements(list.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @GetMapping("/schedules")
    public Result<List<Schedule>> listSchedules() {
        List<Schedule> schedules = scheduleRepository.findByStatusOrderByStartTimeAsc(1, Pageable.unpaged());
        return Result.success(schedules);
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("fanCount", sysUserRepository.count());
        data.put("artistCount", artistRepository.count());
        data.put("workCount", fanWorkRepository.count());
        data.put("merchandiseCount", merchandiseRepository.count());
        return Result.success(data);
    }

    @GetMapping("/config/{key}")
    public Result<String> getConfig(@PathVariable String key) {
        return sysConfigRepository.findByConfigKey(key)
                .map(c -> Result.success(c.getConfigValue()))
                .orElse(Result.success(null));
    }
}

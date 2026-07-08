package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Music;
import com.mangrove.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicRepository musicRepository;

    @GetMapping
    public Result<PageResult<Music>> list(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Music> musicList;
        if (keyword != null && !keyword.isBlank()) {
            musicList = musicRepository.findByTitleContaining(keyword, pageable);
        } else {
            musicList = musicRepository.findByStatus(1, pageable);
        }
        PageResult<Music> result = PageResult.<Music>builder()
                .content(musicList)
                .totalElements(musicList.size())
                .totalPages(1)
                .currentPage(page)
                .size(size)
                .build();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Music> getById(@PathVariable Long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "音乐不存在"));
        music.setPlayCount(music.getPlayCount() + 1);
        musicRepository.save(music);
        return Result.success(music);
    }
}

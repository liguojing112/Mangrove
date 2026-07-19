package com.mangrove.service;

import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.LongVideoRequest;
import com.mangrove.entity.LongVideo;
import com.mangrove.repository.LongVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LongVideoService {

    private final LongVideoRepository repository;

    public List<LongVideo> listPublic(String category) {
        return isBlank(category)
                ? repository.findByStatusOrderBySortOrderAscCreatedAtDesc(1)
                : repository.findByStatusAndCategoryOrderBySortOrderAscCreatedAtDesc(1, category.trim());
    }

    public List<LongVideo> listAdmin() {
        return repository.findAllByOrderBySortOrderAscCreatedAtDesc();
    }

    @Transactional
    public LongVideo save(Long id, LongVideoRequest request) {
        validate(request);
        LongVideo video = id == null ? new LongVideo() : repository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "长视频不存在"));
        video.setTitle(request.getTitle().trim());
        video.setCategory(request.getCategory().trim());
        video.setDescription(trimToNull(request.getDescription()));
        video.setLocalVideoUrl(trimToNull(request.getLocalVideoUrl()));
        video.setBilibiliUrl(trimToNull(request.getBilibiliUrl()));
        video.setCoverUrl(trimToNull(request.getCoverUrl()));
        video.setPublishedDate(request.getPublishedDate());
        video.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        video.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return repository.save(video);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "长视频不存在");
        }
        repository.deleteById(id);
    }

    private void validate(LongVideoRequest request) {
        if (isBlank(request.getTitle())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请填写视频标题");
        }
        if (isBlank(request.getCategory())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择视频分类");
        }
        if (isBlank(request.getLocalVideoUrl()) && isBlank(request.getBilibiliUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "本地视频和 B站链接至少填写一个");
        }
        if (!isBlank(request.getBilibiliUrl()) && !isValidBilibiliUrl(request.getBilibiliUrl())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请输入有效的 B站 HTTPS 链接");
        }
    }

    private boolean isValidBilibiliUrl(String value) {
        try {
            URI uri = URI.create(value.trim());
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || host == null) return false;
            String normalized = host.toLowerCase(Locale.ROOT);
            return normalized.equals("bilibili.com") || normalized.endsWith(".bilibili.com")
                    || normalized.equals("b23.tv") || normalized.endsWith(".b23.tv");
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}

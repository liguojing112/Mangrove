package com.mangrove.service;

import com.mangrove.dto.request.AlbumRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Album;

public interface AlbumService {

    PageResult<Album> list(Integer page, Integer size, String category);

    Album getById(Long id);

    Album create(AlbumRequest req, Long userId);

    Album update(Long id, AlbumRequest req);

    void delete(Long id);
}

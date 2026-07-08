package com.mangrove.service;

import com.mangrove.dto.request.ArtistRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Artist;

import java.util.List;

public interface ArtistService {

    PageResult<Artist> list(Integer page, Integer size);

    List<Artist> listAll();

    Artist getById(Long id);

    Artist create(ArtistRequest req);

    Artist update(Long id, ArtistRequest req);

    void delete(Long id);
}

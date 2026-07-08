package com.mangrove.service;

import com.mangrove.dto.request.MerchandiseRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Merchandise;

public interface MerchandiseService {

    PageResult<Merchandise> list(Integer page, Integer size, String category, String rarity);

    Merchandise getById(Long id);

    Merchandise create(MerchandiseRequest req);

    Merchandise update(Long id, MerchandiseRequest req);

    void delete(Long id);
}

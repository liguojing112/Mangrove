package com.mangrove.repository;

import com.mangrove.entity.HomepageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomepageItemRepository extends JpaRepository<HomepageItem, Long> {
    List<HomepageItem> findBySectionOrderBySortOrderAsc(HomepageItem.Section section);
    List<HomepageItem> findBySectionAndContentTypeOrderBySortOrderAsc(HomepageItem.Section section, HomepageItem.ContentType contentType);
}

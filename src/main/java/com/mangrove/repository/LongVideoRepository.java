package com.mangrove.repository;

import com.mangrove.entity.LongVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LongVideoRepository extends JpaRepository<LongVideo, Long> {
    List<LongVideo> findByStatusOrderBySortOrderAscCreatedAtDesc(Integer status);
    List<LongVideo> findByStatusAndCategoryOrderBySortOrderAscCreatedAtDesc(Integer status, String category);
    List<LongVideo> findAllByOrderBySortOrderAscCreatedAtDesc();
}

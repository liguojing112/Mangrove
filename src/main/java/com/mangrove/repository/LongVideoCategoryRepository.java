package com.mangrove.repository;

import com.mangrove.entity.LongVideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LongVideoCategoryRepository extends JpaRepository<LongVideoCategory, Long> {
    List<LongVideoCategory> findAllByOrderBySortOrderAscIdAsc();
    boolean existsByName(String name);
}

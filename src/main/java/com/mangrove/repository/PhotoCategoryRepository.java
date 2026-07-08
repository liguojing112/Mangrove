package com.mangrove.repository;

import com.mangrove.entity.PhotoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoCategoryRepository extends JpaRepository<PhotoCategory, Long> {
    List<PhotoCategory> findAllByOrderBySortOrderAsc();
}

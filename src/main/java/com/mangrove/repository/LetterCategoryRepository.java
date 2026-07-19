package com.mangrove.repository;

import com.mangrove.entity.LetterCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterCategoryRepository extends JpaRepository<LetterCategory, Long> {

    List<LetterCategory> findByStatusOrderBySortOrderAsc(Integer status);
}

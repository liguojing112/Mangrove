package com.mangrove.repository;

import com.mangrove.entity.WorkSectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkSectionItemRepository extends JpaRepository<WorkSectionItem, Long> {
    List<WorkSectionItem> findBySectionOrderBySortOrderAsc(WorkSectionItem.Section section);
    boolean existsBySectionAndItemTypeAndTargetId(WorkSectionItem.Section section, WorkSectionItem.ItemType itemType, Long targetId);
    void deleteBySectionAndItemTypeAndTargetId(WorkSectionItem.Section section, WorkSectionItem.ItemType itemType, Long targetId);
}

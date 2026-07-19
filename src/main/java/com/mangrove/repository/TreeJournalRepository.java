package com.mangrove.repository;

import com.mangrove.entity.TreeJournal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeJournalRepository extends JpaRepository<TreeJournal, Long> {

    Page<TreeJournal> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

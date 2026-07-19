package com.mangrove.repository;

import com.mangrove.entity.UserNote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNoteRepository extends JpaRepository<UserNote, Long> {

    List<UserNote> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, Integer status, Pageable pageable);

    long countByUserIdAndStatus(Long userId, Integer status);

    Optional<UserNote> findByIdAndUserId(Long id, Long userId);
}

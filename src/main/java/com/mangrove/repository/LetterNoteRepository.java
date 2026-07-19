package com.mangrove.repository;

import com.mangrove.entity.LetterNote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterNoteRepository extends JpaRepository<LetterNote, Long> {

    List<LetterNote> findByArtistIdAndStatusOrderByNoteDateDesc(Long artistId, Integer status, Pageable pageable);

    List<LetterNote> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    List<LetterNote> findByStatus(Integer status, Pageable pageable);

    long countByArtistIdAndStatus(Long artistId, Integer status);

    // Simple methods without Pageable
    List<LetterNote> findByArtistIdAndStatusOrderByNoteDateDesc(Long artistId, Integer status);

    List<LetterNote> findByCategoryAndStatusOrderByNoteDateDesc(String category, Integer status);

    List<LetterNote> findByStatusOrderByNoteDateDesc(Integer status);
}
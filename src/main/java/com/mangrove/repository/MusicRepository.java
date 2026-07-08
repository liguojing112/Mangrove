package com.mangrove.repository;

import com.mangrove.entity.Music;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    List<Music> findByStatus(Integer status, Pageable pageable);

    List<Music> findByArtistId(Long artistId, Pageable pageable);

    List<Music> findByTitleContaining(String keyword, Pageable pageable);
}

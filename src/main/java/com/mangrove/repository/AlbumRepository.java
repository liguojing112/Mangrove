package com.mangrove.repository;

import com.mangrove.entity.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByStatusAndCategory(Integer status, Album.Category category, Pageable pageable);

    List<Album> findByArtistId(Long artistId, Pageable pageable);

    List<Album> findByUserId(Long userId, Pageable pageable);
}

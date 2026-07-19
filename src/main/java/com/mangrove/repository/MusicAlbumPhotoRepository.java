package com.mangrove.repository;

import com.mangrove.entity.MusicAlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicAlbumPhotoRepository extends JpaRepository<MusicAlbumPhoto, Long> {

    List<MusicAlbumPhoto> findByAlbumIdOrderBySortOrderAscCreatedAtAsc(Long albumId);

    void deleteByAlbumId(Long albumId);
}

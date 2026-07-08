package com.mangrove.repository;

import com.mangrove.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findByAlbumIdOrderBySortOrderAsc(Long albumId);

    void deleteByAlbumId(Long albumId);
}

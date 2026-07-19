package com.mangrove.repository;

import com.mangrove.entity.MusicPhotoAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicPhotoAlbumRepository extends JpaRepository<MusicPhotoAlbum, Long> {

    List<MusicPhotoAlbum> findByStatusOrderBySortOrderAscCreatedAtDesc(Integer status);

    List<MusicPhotoAlbum> findAllByOrderBySortOrderAscCreatedAtDesc();
}

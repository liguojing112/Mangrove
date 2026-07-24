package com.mangrove.repository;

import com.mangrove.entity.ArtistBioSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistBioSectionRepository extends JpaRepository<ArtistBioSection, Long> {

    List<ArtistBioSection> findByArtistIdAndStatusOrderBySortOrderAsc(Long artistId, Integer status);

    long countByArtistId(Long artistId);

    List<ArtistBioSection> findByUserIdOrderByCreatedAtDesc(Long userId);
}

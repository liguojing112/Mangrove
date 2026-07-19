package com.mangrove.repository;

import com.mangrove.entity.MusicTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MusicTrackRepository extends JpaRepository<MusicTrack, Long> {

    List<MusicTrack> findByStatusOrderBySortOrderAscCreatedAtDesc(Integer status);

    List<MusicTrack> findByStatusAndCategoryOrderBySortOrderAscCreatedAtDesc(Integer status, String category);

    List<MusicTrack> findAllByOrderBySortOrderAscCreatedAtDesc();

    @Modifying
    @Query("update MusicTrack m set m.playCount = m.playCount + 1 where m.id = :id")
    int incrementPlayCount(@Param("id") Long id);
}

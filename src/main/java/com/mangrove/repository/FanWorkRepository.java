package com.mangrove.repository;

import com.mangrove.entity.FanWork;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FanWorkRepository extends JpaRepository<FanWork, Long> {

    List<FanWork> findByStatusAndCategory(FanWork.Status status, FanWork.Category category, Pageable pageable);

    List<FanWork> findByCreatorId(Long creatorId, Pageable pageable);

    List<FanWork> findByArtistId(Long artistId, Pageable pageable);

    List<FanWork> findByStatus(FanWork.Status status, Pageable pageable);
}

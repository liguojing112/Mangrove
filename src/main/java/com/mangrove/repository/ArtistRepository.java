package com.mangrove.repository;

import com.mangrove.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByStatusOrderBySortOrderAsc(Integer status);

    List<Artist> findAllByStatus(Integer status, Pageable pageable);
}

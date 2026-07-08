package com.mangrove.repository;

import com.mangrove.entity.BirthdayWish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthdayWishRepository extends JpaRepository<BirthdayWish, Long> {

    List<BirthdayWish> findByArtistIdAndStatusOrderByCreatedAtDesc(Long artistId, Integer status, Pageable pageable);

    List<BirthdayWish> findByStatus(Integer status, Pageable pageable);

    long countByArtistIdAndStatus(Long artistId, Integer status);
}

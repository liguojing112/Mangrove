package com.mangrove.repository;

import com.mangrove.entity.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(
            Long userId, Favorite.TargetType type, Long targetId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, Favorite.TargetType type, Long targetId);
}

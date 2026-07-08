package com.mangrove.repository;

import com.mangrove.entity.UserDecoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDecorationRepository extends JpaRepository<UserDecoration, Long> {

    List<UserDecoration> findByUserIdAndIsEquipped(Long userId, Integer isEquipped);

    Optional<UserDecoration> findByUserIdAndDecorationId(Long userId, Long decorationId);
}

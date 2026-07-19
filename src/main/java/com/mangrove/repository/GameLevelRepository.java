package com.mangrove.repository;

import com.mangrove.entity.GameLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLevelRepository extends JpaRepository<GameLevel, Long> {

    List<GameLevel> findByStatusOrderByLevelNumberAsc(Integer status);
}

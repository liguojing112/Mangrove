package com.mangrove.repository;

import com.mangrove.entity.BarrageMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarrageMessageRepository extends JpaRepository<BarrageMessage, Long> {
    List<BarrageMessage> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);
}

package com.mangrove.repository;

import com.mangrove.entity.LetterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterImageRepository extends JpaRepository<LetterImage, Long> {

    List<LetterImage> findByLetterIdOrderByCreatedAtDesc(Long letterId);
}

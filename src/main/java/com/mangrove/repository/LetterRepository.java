package com.mangrove.repository;

import com.mangrove.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByStatusOrderByCreatedAtDesc(Integer status);

    List<Letter> findByCategoryAndStatusOrderByCreatedAtDesc(Letter.Category category, Integer status);
}

package com.mangrove.repository;

import com.mangrove.entity.Merchandise;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {

    List<Merchandise> findByCategoryAndStatus(Merchandise.Category category, Integer status, Pageable pageable);

    List<Merchandise> findByArtistId(Long artistId, Pageable pageable);

    List<Merchandise> findByRarity(Merchandise.Rarity rarity, Pageable pageable);
}

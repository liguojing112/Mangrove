package com.mangrove.repository;

import com.mangrove.entity.TreeDecoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreeDecorationRepository extends JpaRepository<TreeDecoration, Long> {

    List<TreeDecoration> findByTypeAndStatus(TreeDecoration.Type type, Integer status);

    List<TreeDecoration> findByUnlockLevelLessThanEqual(Integer level);
}

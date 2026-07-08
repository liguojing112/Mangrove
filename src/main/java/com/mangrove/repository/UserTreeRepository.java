package com.mangrove.repository;

import com.mangrove.entity.UserTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTreeRepository extends JpaRepository<UserTree, Long> {

    Optional<UserTree> findByUserId(Long userId);
}

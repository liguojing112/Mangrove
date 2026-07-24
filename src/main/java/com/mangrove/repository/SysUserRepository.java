package com.mangrove.repository;

import com.mangrove.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByNickname(String nickname);

    boolean existsByUsername(String username);

    boolean existsByPublicId(String publicId);

    boolean existsByEmail(String email);

    Page<SysUser> findByUsernameContainingOrNicknameContaining(String username, String nickname, Pageable pageable);
}

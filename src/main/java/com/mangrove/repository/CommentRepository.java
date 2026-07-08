package com.mangrove.repository;

import com.mangrove.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTargetTypeAndTargetIdAndStatusOrderByCreatedAtDesc(
            Comment.TargetType type, Long targetId, Integer status, Pageable pageable);

    List<Comment> findByParentId(Long parentId);

    long countByTargetTypeAndTargetIdAndStatus(Comment.TargetType type, Long targetId, Integer status);
}

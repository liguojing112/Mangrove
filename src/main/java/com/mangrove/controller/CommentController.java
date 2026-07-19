package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.CommentRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Comment;
import com.mangrove.entity.LikeRecord;
import com.mangrove.entity.SysUser;
import com.mangrove.repository.CommentRepository;
import com.mangrove.repository.LikeRecordRepository;
import com.mangrove.repository.SysUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final SysUserRepository sysUserRepository;
    private final LikeRecordRepository likeRecordRepository;

    @GetMapping
    public Result<PageResult<Comment>> list(@RequestParam String targetType,
                                            @RequestParam Long targetId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Comment.TargetType type;
        try {
            type = Comment.TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的评论目标类型");
        }

        List<Comment> commentList = commentRepository
                .findByTargetTypeAndTargetIdAndStatusOrderByCreatedAtDesc(type, targetId, 1, pageable);
        long total = commentRepository.countByTargetTypeAndTargetIdAndStatus(type, targetId, 1);

        PageResult<Comment> pageResult = PageResult.<Comment>builder()
                .content(commentList)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();

        return Result.success(pageResult);
    }

    @PostMapping
    @Transactional
    public Result<Comment> create(@Valid @RequestBody CommentRequest req, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);

        Comment.TargetType targetType;
        try {
            targetType = Comment.TargetType.valueOf(req.getTargetType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的评论目标类型");
        }

        Comment parentComment = null;
        if (req.getParentId() != null) {
            parentComment = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "父评论不存在"));
        }

        Comment comment = Comment.builder()
                .user(user)
                .targetType(targetType)
                .targetId(req.getTargetId())
                .content(req.getContent())
                .imageUrl(req.getImageUrl())
                .parent(parentComment)
                .likeCount(0)
                .status(1)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return Result.success(commentRepository.save(comment));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != SysUser.Role.ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此评论");
        }

        commentRepository.delete(comment);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    @Transactional
    public Result<String> toggleLike(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));

        Optional<LikeRecord> existing = likeRecordRepository
                .findByUserIdAndTargetTypeAndTargetId(user.getId(), LikeRecord.TargetType.COMMENT, id);

        if (existing.isPresent()) {
            likeRecordRepository.deleteByUserIdAndTargetTypeAndTargetId(user.getId(), LikeRecord.TargetType.COMMENT, id);
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            commentRepository.save(comment);
            return Result.success("取消点赞");
        } else {
            LikeRecord record = LikeRecord.builder()
                    .user(user)
                    .targetType(LikeRecord.TargetType.COMMENT)
                    .targetId(id)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            likeRecordRepository.save(record);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
            return Result.success("点赞成功");
        }
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

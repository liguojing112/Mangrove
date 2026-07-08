package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.Comment;
import com.mangrove.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {

    private final CommentRepository commentRepository;

    @GetMapping
    public Result<PageResult<Comment>> listAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentRepository.findAll(pageable);
        return Result.success(PageResult.of(comments));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));
        commentRepository.delete(comment);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Comment> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评论不存在"));
        comment.setStatus(status);
        commentRepository.save(comment);
        return Result.success(comment);
    }
}

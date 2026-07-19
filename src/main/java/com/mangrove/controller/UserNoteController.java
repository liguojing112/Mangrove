package com.mangrove.controller;

import com.mangrove.common.Result;
import com.mangrove.common.ResultCode;
import com.mangrove.common.exception.BusinessException;
import com.mangrove.dto.request.NoteRequest;
import com.mangrove.dto.response.PageResult;
import com.mangrove.entity.SysUser;
import com.mangrove.entity.UserNote;
import com.mangrove.repository.SysUserRepository;
import com.mangrove.repository.UserNoteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class UserNoteController {

    private final UserNoteRepository userNoteRepository;
    private final SysUserRepository sysUserRepository;

    @GetMapping
    public Result<PageResult<UserNote>> list(Authentication authentication,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        SysUser user = getCurrentUser(authentication);
        Pageable pageable = PageRequest.of(page, size);
        List<UserNote> notes = userNoteRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(user.getId(), 1, pageable);
        long total = userNoteRepository.countByUserIdAndStatus(user.getId(), 1);

        PageResult<UserNote> pageResult = PageResult.<UserNote>builder()
                .content(notes)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .size(size)
                .build();

        return Result.success(pageResult);
    }

    @PostMapping
    @Transactional
    public Result<UserNote> create(@Valid @RequestBody NoteRequest req, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);

        UserNote note = UserNote.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return Result.success(userNoteRepository.save(note));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<UserNote> update(@PathVariable Long id, @Valid @RequestBody NoteRequest req, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        UserNote note = userNoteRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "记事不存在"));

        note.setTitle(req.getTitle());
        note.setContent(req.getContent());
        note.setUpdatedAt(LocalDateTime.now());

        return Result.success(userNoteRepository.save(note));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        SysUser user = getCurrentUser(authentication);
        UserNote note = userNoteRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "记事不存在"));

        note.setStatus(0);
        userNoteRepository.save(note);
        return Result.success();
    }

    private SysUser getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}

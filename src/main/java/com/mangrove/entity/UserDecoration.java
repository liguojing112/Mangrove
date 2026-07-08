package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_decoration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDecoration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decoration_id", nullable = false)
    private TreeDecoration decoration;

    @Column(name = "is_equipped", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer isEquipped;

    @Column(name = "acquired_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime acquiredAt;
}

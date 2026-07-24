package com.mangrove.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "work_section_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "sort_order", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;

    @Convert(converter = com.mangrove.config.JsonMapConverter.class)
    @Column(name = "extra_data", columnDefinition = "LONGTEXT")
    private Map<String, Object> extraData;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum Section {
        PREVIEW, RANK, SHOWCASE, HONOR
    }

    public enum ItemType {
        WORK, CREATOR
    }
}

package com.mangrove.repository;

import com.mangrove.entity.AuditLog;
import com.mangrove.entity.MediaAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {

    /** 前台获取已通过且未删除的内容 */
    @Query("SELECT m FROM MediaAsset m WHERE m.status = 1 AND m.isDeleted = 0 ORDER BY m.createdAt DESC")
    List<MediaAsset> findPublished();

    @Query("SELECT m FROM MediaAsset m WHERE m.status = 1 AND m.isDeleted = 0 AND m.category = :category ORDER BY m.createdAt DESC")
    List<MediaAsset> findPublishedByCategory(@Param("category") MediaAsset.Category category);

    /** 超级管理员：获取所有待审核的新上传内容 */
    @Query("SELECT m FROM MediaAsset m WHERE m.status = 0 AND m.isDeleted = 0 ORDER BY m.createdAt DESC")
    Page<MediaAsset> findPendingUploads(Pageable pageable);

    /** 超级管理员：获取所有待审核的删除申请（status=1 表示该内容已通过审核，但有人申请删除它） */
    @Query("SELECT m FROM MediaAsset m JOIN AuditLog a ON a.target = m " +
           "WHERE a.auditStatus = 0 AND a.action = :action " +
           "AND m.isDeleted = 0 ORDER BY a.createdAt DESC")
    Page<MediaAsset> findPendingDeletions(@Param("action") AuditLog.AuditAction action, Pageable pageable);

    /** 统计某分类已通过的内容数量 */
    long countByCategoryAndStatus(MediaAsset.Category category, int status);

    /** 最近上传的已通过内容 */
    List<MediaAsset> findTop6ByStatusAndIsDeletedOrderByCreatedAtDesc(int status, int isDeleted);
}

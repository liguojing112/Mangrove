package com.mangrove.repository;

import com.mangrove.entity.AuditLog;
import com.mangrove.entity.MediaAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** 获取某资产最新的待审核删除申请 */
    @Query("SELECT a FROM AuditLog a WHERE a.target = :target AND a.action = :action AND a.auditStatus = 0 ORDER BY a.createdAt DESC")
    Optional<AuditLog> findPendingDeleteByTarget(@Param("target") MediaAsset target, @Param("action") AuditLog.AuditAction action);

    /** 超级管理员：获取所有待审核日志（上传+删除申请） */
    @Query("SELECT a FROM AuditLog a WHERE a.auditStatus = 0 ORDER BY a.createdAt DESC")
    Page<AuditLog> findPendingAudits(Pageable pageable);

    /** 获取某资产关联的所有审核日志 */
    @Query("SELECT a FROM AuditLog a WHERE a.target = :target AND a.action = :action AND a.auditStatus = 0")
    Optional<AuditLog> findPendingByTargetAndAction(@Param("target") MediaAsset target, @Param("action") AuditLog.AuditAction action);
}

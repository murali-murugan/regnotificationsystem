package com.srm.prj.publicationextractor.repository;

import com.srm.prj.publicationextractor.domain.ExtractionRunAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractionRunAuditLogRepo extends JpaRepository<ExtractionRunAuditLog, Long> {
}

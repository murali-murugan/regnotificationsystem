package com.srm.prj.publicationextractor.repository;

import com.srm.prj.publicationextractor.domain.ExtractionRunSyncPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractionRunSyncPointRepo extends JpaRepository<ExtractionRunSyncPoint, Long> {
}

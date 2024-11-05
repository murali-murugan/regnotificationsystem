package com.srm.prj.publicationextractor.repository;

import com.srm.prj.publicationextractor.domain.ExtractionRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractionRunStatusRepo extends JpaRepository<ExtractionRunStatus, String> {
}

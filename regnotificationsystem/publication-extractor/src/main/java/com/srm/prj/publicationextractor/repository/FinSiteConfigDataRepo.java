package com.srm.prj.publicationextractor.repository;

import com.srm.prj.publicationextractor.domain.FinSiteConfigData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinSiteConfigDataRepo extends JpaRepository<FinSiteConfigData, String> {
}

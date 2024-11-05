package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.Exception.IllegalExecutionException;
import com.srm.prj.publicationextractor.domain.FinSiteConfigData;

import java.time.LocalDateTime;
import java.util.List;

public interface SiteConfigDataService {

    public List<FinSiteConfigData> getAllConfigurations();

    public PublishedNewsItem updateStatus(String siteId, String status);

    public PublishedNewsItem updateStartStatus(FinSiteConfigData finSiteConfigData);

    public PublishedNewsItem updateCompletionStatus(PublishedNewsItem publishedNewsItem);

    public void addExtractionAuditLog(PublishedNewsItem publishedNewsItem, LocalDateTime startDateTime, String status);

    public void lockExecution() throws IllegalExecutionException;
    public void unlockExecution() throws IllegalExecutionException;

}

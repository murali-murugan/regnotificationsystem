package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.Exception.IllegalExecutionException;
import com.srm.prj.publicationextractor.domain.ExtractionRunAuditLog;
import com.srm.prj.publicationextractor.domain.ExtractionRunStatus;
import com.srm.prj.publicationextractor.domain.ExtractionRunSyncPoint;
import com.srm.prj.publicationextractor.domain.FinSiteConfigData;
import com.srm.prj.publicationextractor.repository.ExtractionRunAuditLogRepo;
import com.srm.prj.publicationextractor.repository.ExtractionRunStatusRepo;
import com.srm.prj.publicationextractor.repository.ExtractionRunSyncPointRepo;
import com.srm.prj.publicationextractor.repository.FinSiteConfigDataRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class SiteConfigDataServiceImpl implements SiteConfigDataService{

    private final FinSiteConfigDataRepo finSiteConfigDataRepo;
    private final ExtractionRunStatusRepo extractionRunStatusRepo;
    private final ExtractionRunAuditLogRepo extractionRunAuditLogRepo;
    private final ExtractionRunSyncPointRepo extractionRunSyncPointRepo;

    @Value("${update.lastpubdate.flag}")
    private boolean updateLastPubdateFlg = true;

    public SiteConfigDataServiceImpl(FinSiteConfigDataRepo finSiteConfigDataRepo,
                                     ExtractionRunStatusRepo extractionRunStatusRepo,
                                     ExtractionRunAuditLogRepo extractionRunAuditLogRepo,
                                     ExtractionRunSyncPointRepo extractionRunSyncPointRepo) {

        this.finSiteConfigDataRepo = finSiteConfigDataRepo;
        this.extractionRunStatusRepo = extractionRunStatusRepo;
        this.extractionRunAuditLogRepo = extractionRunAuditLogRepo;
        this.extractionRunSyncPointRepo = extractionRunSyncPointRepo;
    }


    @Override
    public List<FinSiteConfigData> getAllConfigurations() {
        return finSiteConfigDataRepo.findAll();
    }


    @Override
    public PublishedNewsItem updateStatus(String siteId, String status) {

        ExtractionRunStatus extractionRunStatus = extractionRunStatusRepo.findById(siteId).orElse(null);
        if (extractionRunStatus != null) {
            extractionRunStatus.setUpdateDate(LocalDateTime.now(ZoneId.systemDefault()));
            extractionRunStatus.setLastRunDate(LocalDateTime.now(ZoneId.systemDefault()));
            extractionRunStatus.setRunStatus(status);
            extractionRunStatusRepo.save(extractionRunStatus);
        }

        return null;
    }

    @Override
    public PublishedNewsItem updateStartStatus(FinSiteConfigData finSiteConfigData) {

        ExtractionRunStatus extractionRunStatus = extractionRunStatusRepo.findById(finSiteConfigData.getSiteId()).orElse(null);
        if (extractionRunStatus != null) {
            extractionRunStatus.setUpdateDate(LocalDateTime.now(ZoneId.systemDefault()));
            extractionRunStatus.setRunStatus(ExtractionRunStatus.EXTRACTION_RUN_STATUS_RUNNING);
            extractionRunStatusRepo.save(extractionRunStatus);
        }

        return null;
    }

    @Override
    public PublishedNewsItem updateCompletionStatus(PublishedNewsItem publishedNewsItem) {

        ExtractionRunStatus extractionRunStatus = extractionRunStatusRepo.findById(publishedNewsItem.getSiteId()).orElse(null);
        if (extractionRunStatus != null) {
            extractionRunStatus.setLastRunDate(LocalDateTime.now(ZoneId.systemDefault()));
            extractionRunStatus.setUpdateDate(LocalDateTime.now(ZoneId.systemDefault()));
            if (updateLastPubdateFlg) { extractionRunStatus.setLastPublishDate(publishedNewsItem.getPublishedDate()); }
            extractionRunStatus.setRunStatus(ExtractionRunStatus.EXTRACTION_RUN_STATUS_COMPLETED);

            extractionRunStatusRepo.save(extractionRunStatus);
        }

        return null;
    }

    @Override
    public void addExtractionAuditLog(PublishedNewsItem publishedNewsItem, LocalDateTime startDateTime, String completionStatus) {
        ExtractionRunAuditLog extractionRunAuditLog = ExtractionRunAuditLog.builder().siteId(publishedNewsItem.getSiteId())
                .runStartDate(startDateTime)
                .runCompletionDate(LocalDateTime.now(ZoneId.systemDefault() ) )
                .runCompletionStatus(completionStatus)
                .build();

        extractionRunAuditLogRepo.save(extractionRunAuditLog);

    }

    @Override
    public void lockExecution() throws IllegalExecutionException {

        Optional<ExtractionRunSyncPoint> optionalExtractionRunSyncPoint = extractionRunSyncPointRepo.findById(1L);
        if (optionalExtractionRunSyncPoint.isPresent()) {
            ExtractionRunSyncPoint extractionRunSyncPoint = optionalExtractionRunSyncPoint.get();

            if (extractionRunSyncPoint.getRunStatus().equals(ExtractionRunStatus.EXTRACTION_RUN_STATUS_RUNNING)) {
                throw new IllegalExecutionException("Extraction run already running");
            } else {
                extractionRunSyncPoint.setRunStatus(ExtractionRunStatus.EXTRACTION_RUN_STATUS_RUNNING);
                extractionRunSyncPointRepo.save(extractionRunSyncPoint);
            }
        }
        else {
            throw new IllegalExecutionException("Entry not found in SyncPointTable for id=1");
        }
    }

    @Override
    public void unlockExecution() throws IllegalExecutionException {
        Optional<ExtractionRunSyncPoint> optionalExtractionRunSyncPoint = extractionRunSyncPointRepo.findById(1L);
        if (optionalExtractionRunSyncPoint.isPresent()) {
            ExtractionRunSyncPoint extractionRunSyncPoint = optionalExtractionRunSyncPoint.get();

            extractionRunSyncPoint.setRunStatus(ExtractionRunStatus.EXTRACTION_RUN_STATUS_READY);
            extractionRunSyncPointRepo.save(extractionRunSyncPoint);
        }
        else {
            throw new IllegalExecutionException("Entry not found in SyncPointTable for id=1");
        }
    }


}

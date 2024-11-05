package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.Exception.IllegalExecutionException;
import com.srm.prj.publicationextractor.domain.ExtractionRunAuditLog;
import com.srm.prj.publicationextractor.domain.ExtractionRunStatus;
import com.srm.prj.publicationextractor.domain.FinSiteConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AllNewsSiteRunner  {

    @Autowired
    private ExtractionWebDriver extractionWebDriver;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SiteConfigDataService siteConfigDataService;

    public void extractAllPublications() {


        try {
            siteConfigDataService.lockExecution();
        } catch (IllegalExecutionException e) {
            log.debug( "Unable to lock for execution: " +  e.getMessage());
            return;
        }

        List<FinSiteConfigData> siteConfigDataList = siteConfigDataService.getAllConfigurations();

        for (FinSiteConfigData finSiteConfigData : siteConfigDataList) {

            if (!finSiteConfigData.isEnabled()) continue;

            //Convenience
            finSiteConfigData.setLastExtractionDate(finSiteConfigData.getExtractionRunStatus().getLastPublishDate());

            PubExtractionService pubExtractionService = PubExtractionService.getInstance(finSiteConfigData);

            LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
            siteConfigDataService.updateStartStatus(finSiteConfigData);

            List<PublishedNewsItem> publishedNewsItems = pubExtractionService.getExtractedNews(extractionWebDriver);

            extractionWebDriver.close();

            PublishedNewsItem lastPublishedNewsItem = null;

            int row=1;
            for (PublishedNewsItem publishedNewsItem : publishedNewsItems) {
                if (row==1) {lastPublishedNewsItem = publishedNewsItem;}

                String fullHtmlContent = EmailFormatter.formatEmailHtml(finSiteConfigData, publishedNewsItem);
                try {
                    List recepientList = Arrays.asList(finSiteConfigData.getToRecipientList().split(";"));
                    emailService.sendHtmlEmail(publishedNewsItem.getTitle(), fullHtmlContent, recepientList);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                row++;
            }

            if (lastPublishedNewsItem != null) {
                siteConfigDataService.updateCompletionStatus(lastPublishedNewsItem);
            } else {
                siteConfigDataService.updateStatus(finSiteConfigData.getSiteId(), ExtractionRunStatus.EXTRACTION_RUN_STATUS_COMPLETED);
            }

            writeAuditLog(finSiteConfigData, startTime, ExtractionRunStatus.EXTRACTION_RUN_STATUS_COMPLETED);
        }

        siteConfigDataService.unlockExecution();
    }

    private void writeAuditLog(FinSiteConfigData finSiteConfigData, LocalDateTime startTime, String status) {
        ExtractionRunAuditLog extractionRunAuditLog = ExtractionRunAuditLog.builder().siteId(finSiteConfigData.getSiteId())
                .runStartDate(startTime)
                .runCompletionDate(LocalDateTime.now(ZoneId.systemDefault()))
                .runCompletionStatus(status)
                .build();

        PublishedNewsItem publishedNewsItem = PublishedNewsItem.builder().siteId(finSiteConfigData.getSiteId())
                        .build();

        siteConfigDataService.addExtractionAuditLog(publishedNewsItem, startTime, status);

    }
}

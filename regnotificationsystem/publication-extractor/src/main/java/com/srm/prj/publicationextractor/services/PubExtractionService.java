package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.domain.FinSiteConfigData;

import java.util.List;

public interface PubExtractionService {

    public List<PublishedNewsItem> getExtractedNews(ExtractionWebDriver extractionWebDriver);

    public static PubExtractionService getInstance(FinSiteConfigData finSiteConfigData) {

        PubExtractionService pubExtractionService;
        String templateId = finSiteConfigData.getTemplateId();


        switch (templateId) {
            case "RBI1":
                pubExtractionService = new RBI1ExtractionService(finSiteConfigData);
                break;
            default:
                pubExtractionService = new RBI1ExtractionService(finSiteConfigData);
        }

        return pubExtractionService;

    }

}

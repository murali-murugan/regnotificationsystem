package com.srm.prj.publicationextractor.web.service;

import com.srm.prj.publicationextractor.domain.FinSiteConfigData;
import com.srm.prj.publicationextractor.services.SiteConfigDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppMonitorService {

    private final SiteConfigDataService siteConfigDataService;

    public AppMonitorService(SiteConfigDataService siteConfigDataService) {
        this.siteConfigDataService = siteConfigDataService;
    }

    public List<FinSiteConfigData> getSiteConfigData() {

        List<FinSiteConfigData>  siteConfigDataList = siteConfigDataService.getAllConfigurations();


        return siteConfigDataList;

    }




}

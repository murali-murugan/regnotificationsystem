package com.srm.prj.publicationextractor.web.service;

import com.srm.prj.publicationextractor.services.AllNewsSiteRunner;
import org.springframework.stereotype.Service;

@Service
public class ExtractionWebService {

    private AllNewsSiteRunner allNewsSiteRunner;

    public ExtractionWebService(AllNewsSiteRunner allNewsSiteRunner) {
        this.allNewsSiteRunner = allNewsSiteRunner;
    }

    public void extractAllConfiguredSites() {
        allNewsSiteRunner.extractAllPublications();
    }


}

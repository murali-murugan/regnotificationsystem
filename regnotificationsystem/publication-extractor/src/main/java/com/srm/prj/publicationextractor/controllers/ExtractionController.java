package com.srm.prj.publicationextractor.controllers;

import com.srm.prj.publicationextractor.web.service.AppMonitorService;
import com.srm.prj.publicationextractor.web.service.ExtractionWebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class ExtractionController {

    public final ExtractionWebService extractionWebService;
    private final AppMonitorService appMonitorService;

    public ExtractionController(ExtractionWebService extractionWebService, AppMonitorService appMonitorService) {
        this.extractionWebService = extractionWebService;
        this.appMonitorService = appMonitorService;
    }


    @RequestMapping(value = "/extractpub", method = RequestMethod.GET)
    public  String getIndexPage(Model model) {

        log.debug("Inside Extraction Cotroller Page.getIndex Page");

        extractionWebService.extractAllConfiguredSites();


        model.addAttribute("siteconfiglist", appMonitorService.getSiteConfigData());
        return "index";
    }


}

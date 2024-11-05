package com.srm.prj.publicationextractor.controllers;

import com.srm.prj.publicationextractor.web.service.AppMonitorService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class IndexController {

    private final AppMonitorService appMonitorService;

    public IndexController(AppMonitorService appMonitorService) {
        this.appMonitorService = appMonitorService;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public  String getIndexPage(Model model) {

        log.debug("Inside IndexController.getIndex Page");

        model.addAttribute("siteconfiglist", appMonitorService.getSiteConfigData());

        return "index";
    }
}

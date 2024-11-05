package com.srm.prj.publicationextractor.services;

import org.openqa.selenium.WebDriver;

public interface ExtractionWebDriver {

    public WebDriver getWebDriver();
    public void close();

}

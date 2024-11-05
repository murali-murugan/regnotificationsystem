package com.srm.prj.publicationextractor.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExtractionWebDriverChrome implements ExtractionWebDriver {

    WebDriver chromeWebDriver=null;

    @Value("${chrome.driver.path}")
    private String driverLocation;

    @Override
    public WebDriver getWebDriver() {

        synchronized (this) {
            if (chromeWebDriver == null) {
                System.setProperty("webdriver.chrome.driver", driverLocation);
                chromeWebDriver = new ChromeDriver();
            }
        }
        return chromeWebDriver;
    }

    @Override
    public void close() {
        synchronized (this) {
            if (chromeWebDriver != null) chromeWebDriver.close();
            chromeWebDriver = null;
        }
    }
}

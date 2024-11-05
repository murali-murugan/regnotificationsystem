package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.domain.FinSiteConfigData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class RBI1ExtractionService implements PubExtractionService {

    FinSiteConfigData finSiteConfigData=null;
    ExtractionWebDriver extractionWebDriver;

    public RBI1ExtractionService(FinSiteConfigData finSiteConfigData) {
        this.finSiteConfigData = finSiteConfigData;
    }

    @Override
    public List<PublishedNewsItem> getExtractedNews(ExtractionWebDriver extractionWebDriver) {

        WebDriver driver = extractionWebDriver.getWebDriver();

        driver.get(finSiteConfigData.getUrl());
        System.out.println(driver.getTitle());

        List<PublishedNewsItem> publishedNewsItems = extractSummaryNews(driver);

        return publishedNewsItems;
    }


    private List<PublishedNewsItem>  extractSummaryNews(WebDriver driver) {
        List<PublishedNewsItem> publishedNewsItems = new ArrayList<>();

        WebElement simpleTable = driver.findElement(By.xpath(finSiteConfigData.getTableStartXPath()));

        // Get all rows
        List<WebElement> rows = simpleTable.findElements(By.tagName("tr"));


        PublishedNewsItem publishedNewsItem=null;
        LocalDate prevPublishDate = null;
        // Print data from each row

        //for (WebElement row : rows) {
        int rowCount = rows.size();
        for (int rowi=0; rowi< rowCount ; rowi++) {
            WebElement row = rows.get(rowi);

            List<WebElement> cols = row.findElements(By.tagName("td"));

            //- if header row, get the date
            LocalDate publishDate =  getPublishDate(cols.get(0));

            //-- If header row skip, just get date
            if (publishDate != null) {
                prevPublishDate = publishDate;
                if (publishDate.isAfter(finSiteConfigData.getLastExtractionDate())) {
                    continue;
                } else {
                    break;
                }
            } else {
                //-- if header row, but has data other than date
                if (isTableHeaderRow(cols.get(0))) { continue; }

                publishedNewsItem = new PublishedNewsItem();
                publishedNewsItem.setSiteId(finSiteConfigData.getSiteId());
                publishedNewsItem.setPublishedDate(prevPublishDate);
                if (cols.size() >= 2) {

                    WebElement titleElement = cols.get(0);
                    String titleText = titleElement.getText();
                    publishedNewsItem.setTitle(titleText);

                    List<WebElement> anchorS = cols.get(1).findElements(By.tagName("a"));
                    if (anchorS.size() > 0) {
                        String downloadUrl = anchorS.get(0).getAttribute("href");
                        publishedNewsItem.setDownLoadUrl(downloadUrl);
                    }

                    gotoDetailsAndExtract(driver, titleElement, publishedNewsItem);

                    //-- Get back to current state after the back navigation
                    simpleTable = driver.findElement(By.xpath(finSiteConfigData.getTableStartXPath()));
                    rows = simpleTable.findElements(By.tagName("tr"));
                    row = rows.get(rowi);
                    cols = row.findElements(By.tagName("td"));

                }
                publishedNewsItems.add(publishedNewsItem);
                publishedNewsItem = null;
            }
        }
        return publishedNewsItems;
    }

    private void gotoDetailsAndExtract(WebDriver driver, WebElement titleCol, PublishedNewsItem publishedNewsItem) {
        List<WebElement> anchorS = titleCol.findElements(By.tagName("a"));
        if (anchorS.size() <= 0) {
            return;
        }
        WebElement titleAnchor = anchorS.get(0);
        titleAnchor.click();

        //titleCol.click();
        WebElement detailContent = driver.findElement(By.xpath(finSiteConfigData.getDetailContentXPath()));
        String detailsHtml = detailContent.getAttribute("innerHTML");
        publishedNewsItem.setDetailHtml(detailsHtml);
        publishedNewsItem.setDetailText(detailContent.getText());

        driver.navigate().back();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(finSiteConfigData.getTableStartXPath())) );
    }


    private LocalDate getPublishDate(WebElement col) {
        LocalDate publishDate = null;

        String rowClassName = col.getAttribute("class");
        if (rowClassName != null) {
                if (rowClassName.equalsIgnoreCase(finSiteConfigData.getHeaderRowClassName())) {
                    String dateString = col.getText().trim();

                    try {
                        publishDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(finSiteConfigData.getDateFormat()));
                    } catch (DateTimeParseException e) {
                        //ignore exception
                    }
                }
        }

        return publishDate;
    }

    private boolean isTableHeaderRow(WebElement col) {

        String rowClassName = col.getAttribute("class");
        if (rowClassName != null) {
            if (rowClassName.equalsIgnoreCase(finSiteConfigData.getHeaderRowClassName())) {
                return true;
            }
        }

        return false;
    }
}

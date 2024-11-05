package com.srm.prj.publicationextractor.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "extr_site_configuration")
public class FinSiteConfigData {

    @Id
    @Column(name="site_id")
    private String siteId;

    @Column(name="site_name")
    private String siteName;

    @Column(name="url")
    private String url;

    @Column(name="template_id")
    private String templateId;

    @Column(name="base_url")
    private String baseUrl;

    private LocalDate lastExtractionDate;

    @Column(name="date_format")
    private String dateFormat;

    @Column(name="table_start_xpath")
    private String tableStartXPath;

    @Column(name="header_row_classname")
    private String headerRowClassName;

    @Column(name="detail_content_xpath")
    private String detailContentXPath;

    @Column(name="head_htmltag")
    private String headHtmlTag;

    @Column(name="to_recipient_list")
    private String toRecipientList;

    @Column(name="enabled")
    private boolean enabled;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private ExtractionRunStatus extractionRunStatus;

}

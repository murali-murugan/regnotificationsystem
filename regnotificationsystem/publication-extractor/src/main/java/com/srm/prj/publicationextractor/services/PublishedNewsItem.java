package com.srm.prj.publicationextractor.services;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishedNewsItem {

    private String siteId;
    private LocalDate publishedDate;
    private String title;
    private String detailHtml;
    private String detailText;
    private String downLoadUrl;


}

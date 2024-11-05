package com.srm.prj.publicationextractor.services;

import com.srm.prj.publicationextractor.domain.FinSiteConfigData;

import java.time.format.DateTimeFormatter;

public class EmailFormatter {


    public static String formatEmailHtml(FinSiteConfigData finSiteConfigData, PublishedNewsItem publishedNewsItem) {

        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append(finSiteConfigData.getHeadHtmlTag());
        sb.append("<body>");
        sb.append(getEmailMessage(finSiteConfigData,publishedNewsItem));
        sb.append(publishedNewsItem.getDetailHtml());
        sb.append("</body>");
        sb.append("<html>");

        return sb.toString();

    }

    private static String getEmailMessage(FinSiteConfigData finSiteConfigData, PublishedNewsItem publishedNewsItem){

        StringBuffer sb = new StringBuffer();
        sb.append("""
                <table class="content" width="600" border="0" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border: 0px solid #cccccc;">
                             <!-- Header -->
                             <tr>
                                 <td class="header" style="background-color: #345C72; padding: 20px; text-align: left; color: white; font-size: 18px;">
                                     <b>Important !!</b><br><br><br>
                                     Dear user, <br><br>
                                     Please be informed of as there has been a new publication in the regulatory web site.<br><br>
                                     Following are the details:
                                 </td>
                             </tr>
                             <tr><td>
                             <table  class="styled-table" cellpadding="5" cellspacing="0" border="1" margin="0">
                                 <tr>
                                 <td width="130"  style="background-color:goldenrod; font-size:small; font-weight: bold;">
                                     Subject
                                 </td>
                                 <td><p>
                """);

        sb.append(publishedNewsItem.getTitle());
        sb.append("""
                </p>
                                </td>
                                </tr>
                                <tr>
                                    <td style="background-color:goldenrod; font-size:small; font-weight: bold;">
                                    Published Date
                                    </td>
                                    <td>
                """);

        sb.append(publishedNewsItem.getPublishedDate().format(DateTimeFormatter.ofPattern( "MMM dd, yyyy")) );
        sb.append("""
                    </p>
                    </td>
                </tr>
                <tr>
                    <td style="background-color:goldenrod; font-size:small; font-weight: bold;">
                    Url
                    </td>
                    <td>
                    <p><a href="
                """);
        sb.append(finSiteConfigData.getUrl());
        sb.append("\" >");
        sb.append(finSiteConfigData.getUrl());

        sb.append("""
                </a> </a> </p>
                                    </td>
                                </tr>
                            </table>
                        </td></tr>
                        </table>
                                
                         <hr>
                """);

        return sb.toString();

    }
}

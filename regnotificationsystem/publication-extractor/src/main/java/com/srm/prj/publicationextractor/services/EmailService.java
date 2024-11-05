package com.srm.prj.publicationextractor.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;

@Slf4j
@Component
public class EmailService {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "SRM Mini Project";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    //private static final String TOKENS_DIRECTORY_PATH = "C:\\murali\\personal\\education\\srm\\2023\\notes\\semester-3\\MiniPrj\\myWork\\gmail\\tokens";
    private static final String TOKENS_DIRECTORY_PATH = "C:\\murali\\personal\\education\\srm\\2023\\notes\\semester-4\\project-work\\dev\\gmail\\tokens";

    private static final String EMAIL_ADDRESS = "muralisrmminiproj@gmail.com";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
    //private static final String CREDENTIALS_FILE_PATH = "/client_secret_619277633507-sicpk0u8nka4v6gq8t1ok96r0ng9tbqs.apps.googleusercontent.com.json";
    //private static final String CREDENTIALS_FILE_PATH = "/client_secret_619277633507-9oq7adougfdnmfskmde3cldft9lvblkm.apps.googleusercontent.com.json";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret_619277633507-s2ssud56sjbg1g9vsbsske5tnpvcs0te.apps.googleusercontent.com.json";

    private final Gmail gmailSservice;

    public EmailService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        gmailSservice = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = EmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Set.of(GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8889).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public static void main(String[] args) {

        String txtMessage = """
        Dear reader,
        
        This is a message body.
        
        Best Regards,
        
        """;

        List<String> recipientList = Arrays.asList(new String[] {"muralisrmminiproj@gmail.com"});


        try {
            new EmailService().sendHtmlEmail("Subject", getHTMLTestStr(), recipientList );


        } catch (GeneralSecurityException | IOException | MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message sendEmail(String subject, String msg) throws GeneralSecurityException, IOException, MessagingException {

        // Create the email content
        String messageSubject = subject;
        String bodyText = msg;

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(EMAIL_ADDRESS));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(EMAIL_ADDRESS));
        email.setSubject(messageSubject);
        email.setText(bodyText);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            // Create send message
            message = gmailSservice.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
            return message;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            log.error("Error Occurred while sending email:{}", error.toPrettyString());
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
        return null;
    }

    public Message sendHtmlEmail(String subject, String msgHtml, List<String> recipients)
            throws GeneralSecurityException, IOException, MessagingException {

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(EMAIL_ADDRESS));

        recipients.forEach(s -> {
            try {
                if (s != null) {
                    email.addRecipient(javax.mail.Message.RecipientType.TO,
                            new InternetAddress(s));
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        email.setSubject(subject);

        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(msgHtml, "text/html");
        mp.addBodyPart(htmlPart);
        email.setContent(mp);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        Message retMessage = null;

        try {
            // Create send message
            retMessage = gmailSservice.users().messages().send("me", message).execute();
            //System.out.println("Message id: " + message.getId());
            //System.out.println(message.toPrettyString());
            System.out.println("Mail sent successfully!");
            return message;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            log.error("Error Occurred while sending email:{}", error.toPrettyString());
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
        return retMessage;
    }

    private static String getHTMLTestStr() {
        return """
                <html>
                <head>
                    <base href="https://www.sebi.gov.in/" target="_blank">
                    <link rel="stylesheet" href="https://www.sebi.gov.in/fonts/fonts.css" type="text/css">
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/date_picker2.css" type="text/css" media="all" />
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/reset.css" type="text/css" media="all">
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/style.css" type="text/css" media="all">
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/modal.css" type="text/css" media="all" />
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/webslidemenu.css" type="text/css" media="all" />
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/prettyPhoto.css" type="text/css" media="screen" />
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/flexslider.css" type="text/css" media="screen" />
                    <link rel="stylesheet" href="https://www.sebi.gov.in/css/responsive.css" type="text/css" media="all">
                </head>
                <body>
                 <section class="department-slider news-detail-slider news_main">
                                        <h1>List of All SEBI Regulations (Updated)
                                            <div class="social-share-btn">
                                                <script type="text/javascript" src="https://www.sebi.gov.in/js/sharenew.js"></script>
                                            </div>
                                        </h1>
                                        <div class="social-share clearfix">
                                            <section class="main_section top_blank">
                                                <div class="main_full">
                                                    <div class="navcentre">
                                                        <ul>
                                                            <li><a class="active"><i class="fa fa-sitemap"></i> Updated List </a></li>
                                                            <li><a href="javascript: showHistory();"><i class="fa  fa-table"></i> Historical Data </a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                                <div class="main_full cause_detail top_space">
                                                    <div class="table-scrollable">
                                                        <table class="table table-striped table-bordered table-hover" id="sample_1">
                                                            <thead>
                                                                <tr>
                                                                    <th width="15%"> Issued Year </th>
                                                                    <th width="85%"> Regulations </th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                
                                                            <tr>
                                                                <td>2024</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/mar-2024/securities-and-exchange-board-of-india-index-providers-regulations-2024_82144.html" target="_blank">Securities and Exchange Board of India (Index Providers) Regulations, 2024</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2021</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2021/securities-and-exchange-board-of-india-delisting-of-equity-shares-regulations-2021-last-amended-on-august-3-2021-_50517.html" target="_blank">Securities and Exchange Board of India (Delisting of Equity Shares) Regulations, 2021 [Last amended on August 3, 2021]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2021</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/jul-2023/web-upload-of-the-securities-and-exchange-board-of-india-issue-and-listing-of-non-convertible-securities-regulations-2021-last-amended-on-july-6-2023-_73664.html" target="_blank">Securities and Exchange Board of India (Issue and Listing of Non-Convertible Securities) Regulations, 2021 [Last amended on July 06, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2021</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2021/securities-and-exchange-board-of-india-share-based-employee-benefits-and-sweat-equity-regulations-2021_51889.html" target="_blank">Securities and Exchange Board of India (Share Based Employee Benefits and Sweat Equity) Regulations, 2021</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2021</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-vault-managers-regulations-2021-last-amended-on-august-18-2023-_76368.html" target="_blank">Securities and Exchange Board of India (Vault Managers) Regulations, 2021 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2020</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-portfolio-managers-regulations-2020-last-amended-on-august-18-2023-_76366.html" target="_blank">Securities and Exchange Board of India (Portfolio Managers) Regulations, 2020 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2019</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-foreign-portfolio-investors-regulations-2019-last-amended-on-august-10-2023-_75747.html" target="_blank">Securities and Exchange Board of India (Foreign Portfolio Investors) Regulations, 2019 [Last amended on August 10, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/oct-2018/securities-and-exchange-board-of-india-appointment-of-administrator-and-procedure-for-refunding-to-the-investors-regulations-2018_40621.html" target="_blank">Securities and Exchange Board of India (Appointment of Administrator and Procedure for Refunding to the Investors) Regulations, 2018</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/feb-2023/securities-and-exchange-board-of-india-buy-back-of-securities-regulations-2018-last-amended-on-07-february-2023-_69229.html" target="_blank">Securities and Exchange Board of India (Buy-back of Securities) Regulations 2018 [Last amended on February 07, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-depositories-and-participants-regulation-2018-last-amended-on-august-23-2023-_76490.html" target="_blank">Securities and Exchange Board of India (Depositories and Participants) Regulations, 2018 [Last amended on August 23, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/dec-2023/securities-and-exchange-board-of-india-issue-of-capital-and-disclosure-requirements-regulations-2018-last-amended-on-december-21-2023-_80421.html" target="_blank">Securities and Exchange Board of India (Issue of Capital and Disclosure Requirements) Regulations 2018 [Last amended on December 21, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-settlement-proceedings-regulations-2018-as-amended-on-august-09-2023-_75281.html" target="_blank">Securities and Exchange Board of India (Settlement Proceedings) Regulations, 2018 [Last amended on August 09, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2018</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-contracts-regulation-stock-exchanges-and-clearing-corporations-regulations-2018-last-amended-on-august-23-2023-_76375.html" target="_blank">Securities Contracts (Regulation) (Stock Exchanges and Clearing Corporations) Regulations, 2018 [Last amended on August 23, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2015</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-issue-and-listing-of-municipal-debt-securities-regulations-2015-last-amended-on-august-18-2023-_76363.html" target="_blank">Securities and Exchange Board of India (Issue and Listing of Municipal Debt Securities) Regulations, 2015 [Last amendment on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2015</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/dec-2023/securities-and-exchange-board-of-india-listing-obligations-and-disclosure-requirements-regulations-2015-last-amended-on-december-21-2023-_80422.html" target="_blank">Securities and Exchange Board of India (Listing Obligations and Disclosure Requirements) Regulations, 2015 [Last amended on December 21, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2015</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/nov-2022/securities-and-exchange-board-of-india-prohibition-of-insider-trading-regulations-2015-last-amended-on-november-24-2022_65864.html" target="_blank">Securities and Exchange Board of India (Prohibition of Insider Trading) Regulations, 2015 [Last amended on November 24, 2022]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2014</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/oct-2023/securities-and-exchange-board-of-india-infrastructure-investment-trusts-regulations-2014-last-amended-on-october-23-2023-_78922.html" target="_blank">Securities and Exchange Board of India (Infrastructure Investment Trusts) Regulations, 2014 [Last amended on October 23, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2014</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/mar-2024/securities-and-exchange-board-of-india-real-estate-investment-trusts-regulations-2014-last-amended-on-march-8-2024-_82430.html" target="_blank">Securities and Exchange Board of India (Real Estate Investment Trusts) Regulations, 2014 [Last amended on March 8, 2024]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2014</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-research-analysts-regulations-2014-last-amended-on-august-18-2023-_76362.html" target="_blank">Securities and Exchange Board of India (Research Analysts) Regulations, 2014 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2013</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/oct-2023/securities-and-exchange-board-of-india-investment-advisers-regulations-2013-last-amended-on-october-09-2023-_78544.html" target="_blank">Securities and Exchange Board of India (Investment Advisers) Regulations, 2013 [Last amended on October 09, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2012</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/apr-2024/securities-and-exchange-board-of-india-alternative-investment-funds-regulations-2012-last-amended-on-april-25-2024-_83190.html" target="_blank">Securities and Exchange Board of India (Alternative Investment Funds) Regulations, 2012 [Last amended on April 25, 2024]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2011</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/feb-2023/securities-and-exchange-board-of-india-substantial-acquisition-of-shares-and-takeovers-regulations-2011-last-amended-on-february-07-2023-_69218.html" target="_blank">Securities and Exchange Board of India (Substantial Acquisition of Shares and Takeovers) Regulations, 2011  [Last amended on February 07, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2011</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-kyc-know-your-client-registration-agency-regulations-2011-last-amended-on-august-18-2023-_76337.html" target="_blank">Securities and Exchange Board of India {KYC (Know Your Client) Registration Agency} Regulations, 2011 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2009</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/oct-2023/securities-and-exchange-board-of-india-investor-protection-and-education-fund-regulations-2009-last-amended-on-october-23-2023-_78920.html" target="_blank">Securities and Exchange Board of India (Investor Protection and Education Fund) Regulations, 2009 [Last amended on October 23, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2008</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2022/securities-and-exchange-board-of-india-intermediaries-regulations-2008-last-amended-on-august-1-2022-_61700.html" target="_blank">Securities and Exchange Board of India (Intermediaries) Regulations, 2008 [Last amended on August 01, 2022]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2008</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/sebi-issue-and-listing-of-securitised-debt-instruments-and-security-receipts-regulations-2008-last-amended-on-august-18-2023-_76336.html" target="_blank">Securities and Exchange Board of India (Issue and Listing of Securitised Debt Instruments and Security Receipts) Regulations, 2008 [Last amended on August 18, 2020]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2007</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/oct-2007/sebi-certification-of-associated-persons-in-the-securities-markets-regulations-2007-last-amended-on-february-07-2014-_34629.html" target="_blank">SEBI (Certification of Associated Persons in the Securities Markets) Regulations, 2007 [Last amended on February 07, 2014]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2004</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/feb-2023/securities-and-exchange-board-of-india-self-regulatory-organisations-regulations-2004-last-amended-on-february-7-2023-_69216.html" target="_blank">Securities and Exchange Board of India (Self Regulatory Organisations) Regulations, 2004 [last amended on February 07, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2003</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/jan-2022/securities-and-exchange-board-of-india-prohibition-of-fraudulent-and-unfair-trade-practices-relating-to-securities-market-regulations-2003-last-amended-on-january-25-2022-_55604.html" target="_blank">SEBI (Prohibition of Fraudulent and Unfair Trade Practices relating to Securities Market) Regulations, 2003 [Last amended on January 25, 2022]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2001</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/dec-2022/securities-and-exchange-board-of-india-procedure-for-board-meetings-regulations-2001-last-amended-on-december-9-2022-_66463.html" target="_blank">SEBI (Procedure for Board Meetings) Regulations, 2001 [Last amended on December 9, 2022]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2001</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/may-2024/securities-and-exchange-board-of-india-employees-service-regulations-2001-last-amended-on-may-6-2024-_83323.html" target="_blank">Securities and Exchange Board of India (Employees' Service) Regulations, 2001 [Last amended on May 06, 2024]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>2000</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/feb-2023/securities-and-exchange-board-of-india-foreign-venture-capital-investor-regulations-2000-last-amended-on-february-07-2023-_69489.html" target="_blank">Securities and Exchange Board of India (Foreign Venture Capital Investor) Regulations, 2000 [Last amended on February 07, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1999</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-collective-investment-scheme-regulations-1999-last-amended-on-august-18-2023-_76335.html" target="_blank">Securities and Exchange Board of India (Collective Investment Scheme) Regulations, 1999 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1999</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/jul-2023/securities-and-exchange-board-of-india-credit-rating-agencies-regulations-1999-last-amended-on-july-4-2023-_74002.html" target="_blank">Securities and Exchange Board of India (Credit Rating Agencies) Regulations, 1999 [Last amended on July 4, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1996</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/jul-2023/securities-and-exchange-board-of-india-custodian-regulations-1996-last-amended-on-july-4-2023-_74001.html" target="_blank">Securities and Exchange Board of India (Custodian) Regulations, 1996 [Last amendment on July 4, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1996</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-mutual-funds-regulations-1996-last-amended-on-august-18-2023-_76333.html" target="_blank">Securities and Exchange Board of India (Mutual Funds) Regulations, 1996 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1994</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-bankers-to-an-issue-regulations-1994-last-amended-on-august-18-2023-_76331.html" target="_blank">Securities and Exchange Board of India (Bankers to an Issue) Regulations, 1994 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1993</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-debenture-trustees-regulations-1993-last-amended-on-august-18-2023-_76330.html" target="_blank">Securities and Exchange Board of India (Debenture Trustees) Regulations, 1993 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1993</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-registrars-to-an-issue-and-share-transfer-agents-regulations-1993-last-amended-on-august-18-2023-_76329.html" target="_blank">Securities and Exchange Board of India (Registrars to an Issue and Share Transfer Agents) Regulations, 1993 - [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1992</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-merchant-bankers-regulations-1992-last-amended-on-august-18-2023-_76327.html" target="_blank">Securities and Exchange Board of India (Merchant Bankers) Regulations, 1992 [Last amended on  August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                            <tr>
                                                                <td>1992</td>
                                                                <td><a href="https://www.sebi.gov.in/legal/regulations/aug-2023/securities-and-exchange-board-of-india-stock-brokers-regulations-1992-last-amended-on-august-18-2023-_76325.html" target="_blank">Securities and Exchange Board of India (Stock Brokers) Regulations, 1992 [Last amended on August 18, 2023]</a></td>
                                                          \s
                                                            </tr>
                                
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </section>
                                </div>
                            </section>
                            </body>
                            </html>                
                
                """;
    }
}

package org.recap.util;

import org.apache.commons.collections.bidimap.AbstractBidiMapDecorator;
import org.apache.commons.collections.map.HashedMap;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.reports.TitleMatchedReport;
import org.recap.model.reports.TitleMatchedReports;
import org.recap.model.search.ReportsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by rajeshbabuk on 13/1/17.
 */
@Service
public class ReportsServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReportsServiceUtil.class);

    @Value("${" + PropertyKeyConstants.SCSB_GATEWAY_URL + "}")
    private String scsbUrl;


    @Value("${" + PropertyKeyConstants.TITLE_MATCH_REPORT_EXPORT_LIMIT + "}")
    private Integer titleReportExportLimit;


    @Value("${" + PropertyKeyConstants.SCSB_BUCKET_NAME + "}")
    private String s3BucketName;

    @Value("${" + PropertyKeyConstants.TITLE_MATCH_REPORT_DIR + "}")
    private String titleReportDir;


    @Autowired
    private ReportsUtil reportsUtil;

    /**
     * This method will call scsb microservice to get the reports response for the accession/deaccession reports.
     *
     * @param reportsForm the reports form
     * @return the reports response
     */
    public ReportsResponse requestAccessionDeaccessionCounts(ReportsForm reportsForm) {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate(reportsForm.getAccessionDeaccessionFromDate());
        reportsRequest.setAccessionDeaccessionToDate(reportsForm.getAccessionDeaccessionToDate());
        reportsRequest.setOwningInstitutions(reportsUtil.getInstitutions());
        reportsRequest.setCollectionGroupDesignations(reportsForm.getCollectionGroupDesignations());
        return getReportsResponse(reportsRequest, ScsbConstants.SCSB_REPORTS_ACCESSION_DEACCESSION_COUNTS_URL);
    }

    /**
     * This method will call scsb microservice to get the reports response for the collection group designation reports.
     *
     * @param reportsForm the reports form
     * @return the reports response
     */
    public ReportsResponse requestCgdItemCounts(ReportsForm reportsForm) {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setOwningInstitutions(reportsUtil.getInstitutions());
        reportsRequest.setCollectionGroupDesignations(reportsForm.getCollectionGroupDesignations());
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers =  HelperUtil.getSwaggerHeaders();
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, headers);

            ResponseEntity<ReportsResponse> responseEntity = restTemplate.exchange(scsbUrl + ScsbConstants.SCSB_REPORTS_CGD_ITEM_COUNTS_URL, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
            return reportsResponse;
        } catch (Exception e) {
            logger.error(ScsbCommonConstants.LOG_ERROR, e);
            reportsResponse.setMessage(e.getMessage());
            return reportsResponse;
        }
    }

    /**
     * This method will call scsb microservice to get the deaccessioned item results to display them as rows in the deaccesion reports UI page.
     *
     * @param reportsForm the reports form
     * @return the reports response
     */
    public ReportsResponse requestDeaccessionResults(ReportsForm reportsForm) {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate(reportsForm.getAccessionDeaccessionFromDate());
        reportsRequest.setAccessionDeaccessionToDate(reportsForm.getAccessionDeaccessionToDate());
        reportsRequest.setDeaccessionOwningInstitution(reportsForm.getDeaccessionOwnInst());
        reportsRequest.setPageNumber(reportsForm.getPageNumber());
        reportsRequest.setPageSize(reportsForm.getPageSize());
        return getReportsResponse(reportsRequest, ScsbConstants.SCSB_REPORTS_DEACCESSION_RESULTS_URL);
    }

    /**
     * This method will call scsb microservice to get the incomplete item results to display them as rows in the incomplete reports UI page.
     *
     * @param reportsForm the reports form
     * @return the reports response
     */
    public ReportsResponse requestIncompleteRecords(ReportsForm reportsForm) {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setIncompleteRequestingInstitution(reportsForm.getIncompleteRequestingInstitution());
        reportsRequest.setIncompletePageNumber(reportsForm.getIncompletePageNumber());
        reportsRequest.setIncompletePageSize(reportsForm.getIncompletePageSize());
        reportsRequest.setExport(reportsForm.isExport());
        return getReportsResponse(reportsRequest, ScsbConstants.SCSB_REPORTS_INCOMPLETE_RESULTS_URL);
    }

    private ReportsResponse getReportsResponse(ReportsRequest reportsRequest, String reportUrl) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = HelperUtil.getSwaggerHeaders();
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, headers);
            ResponseEntity<ReportsResponse> responseEntity = restTemplate.exchange(scsbUrl + reportUrl, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
            return reportsResponse;
        } catch (Exception e) {
            logger.error(ScsbCommonConstants.LOG_ERROR, e);
            reportsResponse.setMessage(e.getMessage());
            return reportsResponse;
        }
   }

    public TitleMatchedReport getTitleMatchReport(TitleMatchedReport titleMatchedReport, boolean isCOUNT, boolean isExport) {
        ResponseEntity<TitleMatchedReport> responseEntity = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = HelperUtil.getSwaggerHeaders();
            HttpEntity<TitleMatchedReport> httpEntity = new HttpEntity<>(titleMatchedReport, headers);
            if(!isExport) {
                responseEntity = (isCOUNT) ? restTemplate.exchange(scsbUrl + ScsbConstants.MATCHING_COUNT_URI, HttpMethod.POST, httpEntity, TitleMatchedReport.class)
                        : restTemplate.exchange(scsbUrl + ScsbConstants.MATCHING_REPORTS_URI, HttpMethod.POST, httpEntity, TitleMatchedReport.class);
            } else {
                if (titleMatchedReport.getTotalRecordsCount() <= titleReportExportLimit) {
                    responseEntity = restTemplate.exchange(scsbUrl + ScsbConstants.MATCHING_REPORTS_URI_EXPORT, HttpMethod.POST, httpEntity, TitleMatchedReport.class);
                } else {
                    responseEntity = restTemplate.exchange(scsbUrl + ScsbConstants.MATCHING_REPORTS_URI_EXPORT_S3, HttpMethod.POST, httpEntity, TitleMatchedReport.class);
                }
            }
            titleMatchedReport = responseEntity.getBody();
            if (isCOUNT) {
                if (titleMatchedReport.getTitleMatchCounts() == null) {
                    titleMatchedReport.setMessage(ScsbConstants.REPORTS_INCOMPLETE_RECORDS_NOT_FOUND);
                }
            }else {
                if (titleMatchedReport.getTitleMatchedReports() == null && titleMatchedReport.getMessage() == null && titleMatchedReport.getReportMessage() == null) {
                    titleMatchedReport.setMessage(ScsbConstants.REPORTS_INCOMPLETE_RECORDS_NOT_FOUND);
                }
            }
            if(isExport || isCOUNT)
                return titleMatchedReport;
            else
                return mapData(titleMatchedReport);
        } catch (Exception e) {
            logger.error(ScsbCommonConstants.LOG_ERROR, e);
            titleMatchedReport.setMessage(ScsbConstants.REPORTS_INCOMPLETE_RECORDS_NOT_FOUND);
            return titleMatchedReport;
        }
    }

    private TitleMatchedReport mapData(TitleMatchedReport titleMatchedReport) {
        int count = 0;
        List<TitleMatchedReports> dataList = titleMatchedReport.getTitleMatchedReports();
        titleMatchedReport.setTitleMatchedReports(null);
        dataList.sort((TitleMatchedReports t1, TitleMatchedReports t2)->t2.getDuplicateCode().compareTo(t1.getDuplicateCode()));
        for (int i = 0; i<dataList.size();i++){
            if(i ==0) {
                dataList.get(i).setId(count);
            } else {
                if(dataList.get(i).getDuplicateCode().equalsIgnoreCase(dataList.get(i-1).getDuplicateCode())){
                    dataList.get(i).setId(count);
                } else {
                    dataList.get(i).setId(++count);
                }
            }
        }
        titleMatchedReport.setTitleMatchedReports(dataList);
        return titleMatchedReport;
    }
}


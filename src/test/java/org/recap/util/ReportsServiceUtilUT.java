package org.recap.util;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.reports.TitleMatchedReport;
import org.recap.model.reports.TitleMatchedReports;
import org.recap.model.search.ReportsForm;
import org.recap.service.RestHeaderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class ReportsServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    ReportsServiceUtil reportsServiceUtil;

    @Mock
    ReportsUtil reportsUtil;

    @Mock
    RestTemplate restTemplate;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    HttpHeaders httpHeaders;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(reportsServiceUtil,"titleReportExportLimit",10);
    }

    @Test
    public void requestAccessionDeaccessionCountsTest(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setPageNumber(10);
        reportsForm.setExport(true);
        reportsForm.setErrorMessage("Error");
        reportsForm.setAccessionDeaccessionFromDate(new Date().toString());
        reportsForm.setAccessionDeaccessionToDate(new Date().toString());
        reportsForm.setOwningInstitutions(Arrays.asList("CUL","PUL"));
        reportsForm.setPageSize(10);
        reportsForm.setCollectionGroupDesignations(Arrays.asList("1","2"));
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        ResponseEntity responseEntity = new ResponseEntity(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        reportsServiceUtil.requestAccessionDeaccessionCounts(reportsForm);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void requestCgdItemcounts(){
        ReportsForm reportsForm = new ReportsForm();
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setMessage("sucess");
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        ReportsResponse response = reportsServiceUtil.requestCgdItemCounts(reportsForm);
        assertEquals(responseEntity.getBody(), response);
    }

    @Test
    public void requestDeaccessionResultsTest(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setPageNumber(10);
        reportsForm.setExport(true);
        reportsForm.setErrorMessage("Error");
        reportsForm.setAccessionDeaccessionFromDate(new Date().toString());
        reportsForm.setAccessionDeaccessionToDate(new Date().toString());
        reportsForm.setOwningInstitutions(Arrays.asList("CUL","PUL"));
        reportsForm.setPageSize(10);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        ResponseEntity responseEntity = new ResponseEntity(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        reportsServiceUtil.requestDeaccessionResults(reportsForm);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void requestIncompleteRecordsTest(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setPageNumber(10);
        reportsForm.setExport(true);
        reportsForm.setErrorMessage("Error");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        ResponseEntity responseEntity = new ResponseEntity(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        reportsServiceUtil.requestIncompleteRecords(reportsForm);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void getReportsResponsenotnull(){
        ReportsRequest reportsRequest = new ReportsRequest();
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setMessage("sucess");
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        ReflectionTestUtils.invokeMethod(reportsServiceUtil, "getReportsResponse", reportsRequest, "test");
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void getReportsResponseTest(){
        ReportsRequest reportsRequest = new ReportsRequest();
        String reportUrl = "http://localhost:9090/test";
        reportsRequest.setPageNumber(10);
        reportsRequest.setExport(true);
        reportsRequest.setExport(true);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        ResponseEntity responseEntity = new ResponseEntity(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ReportsResponse>>any());
        ReflectionTestUtils.invokeMethod(reportsServiceUtil, "getReportsResponse", reportsRequest, reportUrl);
        assertNotNull(responseEntity.getBody());
    }


    @Test
    public void getReportsResponseException(){
        ReportsRequest reportsRequest = new ReportsRequest();
        String reportUrl = "http://localhost:9090/test";
        reportsRequest.setPageNumber(10);
        reportsRequest.setExport(true);
        reportsRequest.setExport(true);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ReportsResponse>>any());
        ReflectionTestUtils.invokeMethod(reportsServiceUtil, "getReportsResponse", reportsRequest, reportUrl);
    }

    @Test
    public void requestCgdItemCountsTest(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setPageNumber(10);
        reportsForm.setExport(true);
        reportsForm.setErrorMessage("Error");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        ResponseEntity responseEntity = new ResponseEntity(reportsResponse, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        reportsServiceUtil.requestCgdItemCounts(reportsForm);
        assertNotNull(responseEntity.getBody());
    }

    @Test

    public void mapData(){
        TitleMatchedReport titleMatchedReport = getTitleMatchedReport();
        ReflectionTestUtils.invokeMethod(reportsServiceUtil,"mapData",titleMatchedReport);
    }

    @Test
    public void getTitleMatchReportException(){
        TitleMatchedReport titleMatchedReport = getTitleMatchedReport();
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport,true,true);
    }

    private TitleMatchedReport getTitleMatchedReport() {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        TitleMatchedReports titleMatchedReports = new TitleMatchedReports();
        titleMatchedReports.setId(1);
        titleMatchedReports.setDuplicateCode("DB");
        TitleMatchedReports titleMatchedReports2 = new TitleMatchedReports();
        titleMatchedReports2.setId(1);
        titleMatchedReports2.setDuplicateCode("DB");
        TitleMatchedReports titleMatchedReports3 = new TitleMatchedReports();
        titleMatchedReports3.setId(1);
        titleMatchedReports3.setDuplicateCode("BB");
        titleMatchedReport.setTitleMatchedReports(Arrays.asList(titleMatchedReports,titleMatchedReports2,titleMatchedReports3));
        return titleMatchedReport;
    }

    public void requestCgdItemCountsException(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setPageNumber(10);
        reportsForm.setExport(true);
        reportsForm.setErrorMessage("Error");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ReportsResponse reportsResponse = new ReportsResponse();
        doThrow(new NumberFormatException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ReportsResponse>>any());
        reportsServiceUtil.requestCgdItemCounts(reportsForm);
    }

    @Test
    public void getTitleMatchReportTestIsCountExportTrue(){
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ResponseEntity responseEntity = new ResponseEntity(titleMatchedReport, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<TitleMatchedReport>>any());
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport, true, true);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void getTitleMatchReportTestIsCountExportFalse(){
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        titleMatchedReport.setTotalPageCount(10000);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ResponseEntity responseEntity = new ResponseEntity(titleMatchedReport, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<TitleMatchedReport>>any());
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport, false, false);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void getTitleMatchReportTest(){
        TitleMatchedReport titleMatchedReport = getTitleMatchReportWithList();
        titleMatchedReport.setTotalPageCount(10000);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ResponseEntity responseEntity = new ResponseEntity(titleMatchedReport, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<TitleMatchedReport>>any());
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport, false, false);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void getTitleMatchReportTestThrowException(){
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        titleMatchedReport.setTotalPageCount(10000);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<TitleMatchedReport>>any());
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport, false, false);
    }



    private TitleMatchedReport getTitleMatchReport() {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        titleMatchedReport.setTitleMatch("ABC");
        titleMatchedReport.setCgd(Arrays.asList("ABC","DEF"));
        titleMatchedReport.setMessage("Title match report");
        titleMatchedReport.setOwningInst("PUL");
        titleMatchedReport.setPageNumber(1);
        titleMatchedReport.setTotalPageCount(10);
        titleMatchedReport.setTotalRecordsCount(1000);
        return titleMatchedReport;
    }

    private TitleMatchedReport getTitleMatchReportWithList() {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        titleMatchedReport.setTitleMatch("ABC");
        titleMatchedReport.setCgd(Arrays.asList("ABC","DEF"));
        titleMatchedReport.setMessage("Title match report");
        titleMatchedReport.setOwningInst("PUL");
        titleMatchedReport.setPageNumber(1);
        titleMatchedReport.setTotalPageCount(10);
        titleMatchedReport.setTotalRecordsCount(1000);

        TitleMatchedReports titleMatchedReports = new TitleMatchedReports();
        titleMatchedReports.setCgd("1");
        titleMatchedReports.setTitle("TITLE");
        titleMatchedReports.setDuplicateCode("ABC");

        TitleMatchedReports titleMatchedReports1 = new TitleMatchedReports();
        titleMatchedReports1.setCgd("2");
        titleMatchedReports1.setTitle("TITLE2");
        titleMatchedReports1.setDuplicateCode("ABC");

         List<TitleMatchedReports> list = new ArrayList<>();
         list.add(titleMatchedReports);
         list.add(titleMatchedReports1);

         titleMatchedReport.setTitleMatchedReports(list);
        return titleMatchedReport;
    }

}

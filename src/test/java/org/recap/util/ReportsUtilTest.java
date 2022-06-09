package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.reports.ReportsInstitutionForm;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class ReportsUtilTest extends BaseTestCaseUT {

    @InjectMocks
    ReportsUtil reportsUtil;

    @Mock
    ReportsServiceUtil reportsServiceUtil;

    @Mock
    RestTemplate restTemplate;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(reportsUtil,"supportInstitution","PUL");
    }

    @Test
    public void populatePartnersCountForRequest() throws ParseException {
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getInstitutionEntity()));
        Mockito.when(requestItemDetailsRepository.getPhysicalAndEDDCounts(any(), any(), any(), any(), any(), any(),any())).thenReturn(1L);
        ReportsForm form = reportsUtil.populatePartnersCountForRequest(reportsForm,fromDate,toDate);
        assertNotNull(form);
    }

    @Test
    public void populatePartnersCountForRequestTest() throws ParseException {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(institutionEntities);
        Mockito.when(requestItemDetailsRepository.getPhysicalAndEDDCounts(any(), any(), any(), any(), any(), any(),any())).thenReturn(1L);
        ReportsForm form = reportsUtil.populatePartnersCountForRequest(reportsForm,fromDate,toDate);
        assertNotNull(form);
    }
    @Test
    public void populateRequestTypeInformation() throws ParseException {
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getInstitutionEntity()));
        ReportsForm form = reportsUtil.populateRequestTypeInformation(reportsForm,fromDate,toDate);
        assertNotNull(form);
    }


    @Test
    public void populateRequestTypeInformationTest() throws ParseException {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(institutionEntities);
        ReportsForm form = reportsUtil.populateRequestTypeInformation(reportsForm,fromDate,toDate);
        assertNotNull(form);
    }

    @Test
    public void populateAccessionDeaccessionItemCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        ReportsResponse reportsResponse = getReportsResponse();
        Mockito.when(reportsServiceUtil.requestAccessionDeaccessionCounts(any())).thenReturn(reportsResponse);
        ReportsForm form = reportsUtil.populateAccessionDeaccessionItemCounts(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void populateCGDItemCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        ReportsResponse reportsResponse = getReportsResponse();
        Mockito.when(reportsServiceUtil.requestCgdItemCounts(any())).thenReturn(reportsResponse);
        ReportsForm form = reportsUtil.populateCGDItemCounts(reportsForm);
        assertNotNull(form);
    }

    @Test
    public void deaccessionReportFieldsInformation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        ReportsResponse reportsResponse = getReportsResponse();
        Mockito.when(reportsServiceUtil.requestDeaccessionResults(any())).thenReturn(reportsResponse);
        List<DeaccessionItemResultsRow> deaccessionItemResultsRows = reportsUtil.deaccessionReportFieldsInformation(reportsForm);
        assertNotNull(deaccessionItemResultsRows);
    }

    @Test
    public void incompleteRecordsReportFieldsInformation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        ReportsResponse reportsResponse = getReportsResponse();
        Mockito.when(reportsServiceUtil.requestIncompleteRecords(any())).thenReturn(reportsResponse);
        List<IncompleteReportResultsRow> incompleteReportResultsRows = reportsUtil.incompleteRecordsReportFieldsInformation(reportsForm);
        assertNotNull(incompleteReportResultsRows);
    }

    @Test
    public void exportIncompleteRecords(){
        List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
        incompleteReportResultsRows.add(new IncompleteReportResultsRow());
        File file = reportsUtil.exportIncompleteRecords(incompleteReportResultsRows,".xml");
        assertNotNull(file);
    }

    @Test
    public void exportIncompleteRecordsTest(){
        List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
        File file = reportsUtil.exportIncompleteRecords(incompleteReportResultsRows,".xml");
        assertNotNull(file);
    }

    @Test
    public void getInstitutions(){
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getInstitutionEntity()));
        List<String> stringList = reportsUtil.getInstitutions();
        assertNotNull(stringList);
    }

    private ReportsResponse getReportsResponse() {
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setReportsInstitutionFormList(Arrays.asList(new ReportsInstitutionForm()));
        reportsResponse.setMessage("test");
        return reportsResponse;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }

}

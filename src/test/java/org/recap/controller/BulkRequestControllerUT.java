package org.recap.controller;

import junit.framework.TestCase;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.search.BulkRequestForm;
import org.recap.model.search.BulkSearchResultRow;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.service.BulkRequestService;
import org.recap.service.RequestService;
import org.recap.util.UserAuthUtil;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class BulkRequestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    BulkRequestController bulkRequestController;

    @Mock
    HttpServletRequest request;

    @Mock
    RequestService requestService;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    BulkRequestService bulkRequestService;

    @Mock
    HttpServletResponse response;

    @Mock
    Model model;

    @Mock
    MultipartFile file;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    UserManagementService userManagementService;

    /*@Test
    public void testBulkRequest() {
        when(request.getSession(false)).thenReturn(session);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken();
        usernamePasswordToken.setUsername("token");
        when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_BULK_REQUEST_URL)).thenReturn(Boolean.TRUE);
        boolean response = bulkRequestController.bulkRequest(request);
        assertNotNull(response);
    }
    @Test
    public void testBulkRequestFailure() {
        when(request.getSession(false)).thenReturn(session);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken();
        usernamePasswordToken.setUsername("token");
        when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_BULK_REQUEST_URL)).thenReturn(Boolean.FALSE);
        boolean response = bulkRequestController.bulkRequest(request);
        assertNotNull(response);
    }*/

    @Test
    public void refreshStatus() {
        String reqJson = "{\"status\":[\"29-0\",\"5-1\"]}";
        Mockito.when(requestService.getRefreshedStatus(reqJson, ScsbConstants.TRUE)).thenReturn(ScsbCommonConstants.COMPLETE_STATUS);
        String result = bulkRequestController.refreshStatus(reqJson);
        TestCase.assertNotNull(result);
        assertEquals("Complete", result);
    }
    @Test
    public void loadCreateRequest(){
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        BulkRequestForm bulkRequestFormResponse = bulkRequestController.loadCreateRequest(bulkRequestForm);
        assertNotNull(bulkRequestFormResponse);
    }
    @Test
    public void createRequest() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.processCreateBulkRequest(any(), any())).thenReturn(bulkRequestForm);
        JSONObject jsonObject = bulkRequestController.createRequest(file,"PA","1","1436778","bulkRequest","testFile","test@gmail.com","Test Notes",request,response);
        assertNotNull(jsonObject);
    }
    @Test
    public void createRequestWithoutErrorMessage() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        bulkRequestForm.setErrorMessage(null);
        Mockito.when(bulkRequestService.processCreateBulkRequest(any(), any())).thenReturn(bulkRequestForm);
        JSONObject jsonObject = bulkRequestController.createRequest(file,"PA","1","1436778","bulkRequest","testFile","test@gmail.com","Test Notes",request,response);
        assertNotNull(jsonObject);
    }
    @Test
    public void createRequestException() throws Exception{
        Mockito.when(bulkRequestService.processCreateBulkRequest(any(), any())).thenThrow(new NullPointerException());
        JSONObject jsonObject = bulkRequestController.createRequest(file,"PA","1","1436778","bulkRequest","testFile","test@gmail.com","Test Notes",request,response);
        assertNotNull(jsonObject);
    }
    @Test
    public void searchRequest(){
        BulkSearchResultRow bulkSearchResultRow = getBulkSearchResultRow();
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        bulkRequestForm.setBulkSearchResultRows(Arrays.asList(bulkSearchResultRow));
        Mockito.when(bulkRequestService.processSearchRequest(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.searchRequest(bulkRequestForm,request);
        assertNotNull(form);
    }

    @Test
    public void onPageSizeChange() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.processOnPageSizeChange(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.onPageSizeChange(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void searchFirst() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.getPaginatedSearchResults(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.searchFirst(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void searchPrevious() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.getPaginatedSearchResults(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.searchPrevious(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void searchNext() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.getPaginatedSearchResults(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.searchNext(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void searchLast() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.getPaginatedSearchResults(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.searchLast(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void populateDeliveryLocations() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.processDeliveryLocations(any())).thenReturn(bulkRequestForm);
        BulkRequestForm form = bulkRequestController.populateDeliveryLocations(bulkRequestForm);
        assertNotNull(form);
    }
    @Test
    public void searchRequestByPatronBarcode() throws Exception{
        String patronBarcodeInRequest = "2453343";
        ModelAndView modelAndView = bulkRequestController.searchRequestByPatronBarcode(patronBarcodeInRequest,model);
        assertNotNull(modelAndView);
    }
    @Test
    public void loadCreateRequestForSamePatron(){
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        Mockito.when(bulkRequestService.processDeliveryLocations(any())).thenReturn(bulkRequestForm);
        ModelAndView modelAndView = bulkRequestController.loadCreateRequestForSamePatron(bulkRequestForm,model);
        assertNotNull(modelAndView);
    }
    @Test
    public void downloadReports() throws Exception{
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        String bulkRequestId = bulkRequestForm.getRequestId().toString();
        Mockito.when(bulkRequestService.saveUpadatedRequestStatus(any())).thenReturn(bulkRequestItemEntity);
        bulkRequestController.downloadReports(bulkRequestId);

    }

    private BulkRequestItemEntity getBulkRequestItemEntity() {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestFileName("test");
        bulkRequestItemEntity.setBulkRequestName("test");
        bulkRequestItemEntity.setBulkRequestStatus("SUCCESS");
        bulkRequestItemEntity.setCreatedBy("test");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setEmailId("test@gmail.com");
        bulkRequestItemEntity.setLastUpdatedDate(new Date());
        bulkRequestItemEntity.setNotes("test");
        bulkRequestItemEntity.setPatronId("123");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setStopCode("test");
        bulkRequestItemEntity.setBulkRequestFileData(new byte[1]);
        return bulkRequestItemEntity;
    }

    private BulkSearchResultRow getBulkSearchResultRow() {
        BulkSearchResultRow bulkSearchResultRow = new BulkSearchResultRow();

        bulkSearchResultRow.setBulkRequestId(1);
        bulkSearchResultRow.setBulkRequestName("test");
        bulkSearchResultRow.setBulkRequestNotes("test");
        bulkSearchResultRow.setCreatedBy("test");
        bulkSearchResultRow.setCreatedDate(new Date());
        bulkSearchResultRow.setEmailAddress("test@gmail.com");
        bulkSearchResultRow.setDeliveryLocation("test");
        bulkSearchResultRow.setFileName("test");
        bulkSearchResultRow.setPatronBarcode("123");
        bulkSearchResultRow.setRequestingInstitution("PUL");
        bulkSearchResultRow.setStatus("SUCCESS");
        return bulkSearchResultRow;
    }

    private BulkRequestForm getBulkRequestForm(){

        BulkRequestForm bulkRequestForm = new BulkRequestForm();
        bulkRequestForm.setBulkRequestName("Test");
        bulkRequestForm.setDeliveryLocation("test");
        bulkRequestForm.setDeliveryLocationInRequest("test");
        bulkRequestForm.setDisableRequestingInstitution(false);
        bulkRequestForm.setDisableSearchInstitution(true);
        bulkRequestForm.setErrorMessage("ERROR");
        bulkRequestForm.setFileName("test");
        bulkRequestForm.setInstitution("NYPL");
        bulkRequestForm.setItemBarcode("123");
        bulkRequestForm.setPatronBarcode("23456");
        bulkRequestForm.setPatronBarcodeInRequest("23456");
        bulkRequestForm.setItemOwningInstitution("PUL");
        bulkRequestForm.setRequestingInstitution("PUL");
        bulkRequestForm.setRequestingInstituionHidden("PUL");
        bulkRequestForm.setRequestId(1);
        bulkRequestForm.setRequestIdSearch("1");
        bulkRequestForm.setItemTitle("test");
        bulkRequestForm.setMessage("test");
        bulkRequestForm.setPageNumber(200);
        bulkRequestForm.setPageSize(20);
        bulkRequestForm.setTotalRecordsCount("40");
        bulkRequestForm.setTotalPageCount(2);
        bulkRequestForm.setStatus("PROCESSED");
        bulkRequestForm.setSubmitted(true);

        return bulkRequestForm;
    }

}

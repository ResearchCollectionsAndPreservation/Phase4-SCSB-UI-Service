package org.recap.controller;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.CancelRequestResponse;
import org.recap.model.jpa.*;
import org.recap.model.reports.TransactionReport;
import org.recap.model.reports.TransactionReports;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.request.ItemResponseInformation;
import org.recap.model.request.ReplaceRequest;
import org.recap.model.search.RequestForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.DeliveryCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.service.RequestService;
import org.recap.service.RestHeaderService;
import org.recap.service.SCSBService;
import org.recap.util.RequestServiceUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class RequestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    RequestController requestController;

    @Mock
    RequestService requestService;

    @Mock
    HttpServletRequest request;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    HttpHeaders httpHeaders;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    SCSBService scsbService;

    @Mock
    ReportsController reportsController;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    DeliveryCodeDetailsRepository deliveryCodeDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestServiceUtil requestServiceUtil;

    String scsbUrl = "http://localhost:9040/testurl";

    String scsbShiro = "testscsb";

    @Test
    public void searchRequests() {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchRequests(requestForm);
        assertNotNull(form);
    }

    @Test
    public void searchRequestsException() {
        RequestForm form = requestController.searchRequests(null);
        assertNull(form);
    }

    @Test
    public void goToSearchRequest() {
        RequestForm requestForm = getRequestForm();
        UserDetailsForm userDetailsForm = getUserDetails();
        InstitutionEntity institutionEntity = getRequestItemEntity().getInstitutionEntity();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        Mockito.when(userAuthUtil.getUserDetails(session, ScsbConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.doNothing().when(requestService).findAllRequestStatusExceptProcessing(any());
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        RequestForm form = requestController.goToSearchRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void goToSearchRequestException() {
        RequestForm requestForm = getRequestForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.doThrow(new NullPointerException()).when(userAuthUtil).getUserDetails(any(), anyString());
        RequestForm form = requestController.goToSearchRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void searchFirst() {
        RequestForm requestForm = getRequestForm();
        requestForm.setInstitution("UC");
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchFirst(requestForm);
        assertNotNull(form);
    }

    @Test
    public void searchNext() {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchNext(requestForm);
        assertNotNull(form);
    }

    @Test
    public void searchLast() {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchLast(requestForm);
        assertNotNull(form);
    }

    @Test
    public void searchPrevious() {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchPrevious(requestForm);
        assertNotNull(form);
    }

    @Test
    public void onRequestPageSizeChange() {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.onRequestPageSizeChange(requestForm);
        assertNotNull(form);
    }

    @Test
    public void loadCreateRequest() {
        RequestForm requestForm = getRequestForm();
        UserDetailsForm userDetailsForm = getUserDetails();
        userDetailsForm.setSuperAdmin(Boolean.TRUE);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(session, ScsbConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(requestService.setDefaultsToCreateRequest(any())).thenReturn(requestForm);
        RequestForm form = requestController.loadCreateRequest(request);
        assertNotNull(form);
    }

    @Test
    public void loadSearchRequest() {
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.doNothing().when(requestService).findAllRequestStatusExceptProcessing(any());
        Mockito.doNothing().when(requestService).getInstitutionForSuperAdmin(any());
        Mockito.when(userAuthUtil.getUserDetails(session, ScsbConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        RequestForm form = requestController.loadSearchRequest(request);
        assertNotNull(form);
    }

    @Test
    public void populateItem() throws JSONException {
        RequestForm requestForm = getRequestForm();
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn("test");
        String result = requestController.populateItem(requestForm, request);
        assertNotNull(result);
        assertEquals("test", result);
    }

    @Test
    public void populateItemException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenThrow(new NullPointerException());
        String result = requestController.populateItem(requestForm, request);
        assertNotNull(result);
    }

    @Test
    public void createRequest() throws JSONException {
        RequestForm requestForm = getRequestForm();
        ResponseEntity responseEntity1 = new ResponseEntity<>(getItemResponseInformation(), HttpStatus.OK);
        OwnerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getRequestItemEntity().getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void createRequestException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        OwnerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getRequestItemEntity().getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void createRequestHttpClientErrorException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        requestForm.setVolumeNumber(null);
        OwnerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getRequestItemEntity().getInstitutionEntity());
        Mockito.when(deliveryCodeDetailsRepository.findByDeliveryCodeAndOwningInstitutionIdAndActive(any(), any(), anyChar())).thenReturn(getDeliveryCodeEntity());
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void createRequestWithNoPermissionErrorMessage() throws JSONException {
        RequestForm requestForm = getRequestForm();
        JSONObject json = getJsonObject();
        String message = json.toString();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(message);
        RequestForm form = requestController.createRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void createRequestWithErrorMessage() throws JSONException {
        RequestForm requestForm = getRequestForm();
        JSONObject json = getJsonObject1();
        String message = json.toString();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(message);
        RequestForm form = requestController.createRequest(requestForm, request);
        assertNotNull(form);
    }

    @Test
    public void cancelRequest() {
        RequestForm requestForm = getRequestForm();
        CancelRequestResponse cancelRequestResponse = getCancelRequestResponse();
        ResponseEntity<CancelRequestResponse> responseEntity = new ResponseEntity<CancelRequestResponse>(cancelRequestResponse, HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(requestItemDetailsRepository.findById(any())).thenReturn(Optional.of(getRequestItemEntity()));
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        String result = requestController.cancelRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void cancelRequestException() {
        RequestForm requestForm = getRequestForm();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        String result = requestController.cancelRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void resubmitRequest() {
        RequestForm requestForm = getRequestForm();
        Map<String, String> map = new HashMap<>();
        map.put("1", ScsbCommonConstants.SUCCESS);
        ResponseEntity responseEntity = new ResponseEntity<>(map, HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void resubmitInvalidRequest() {
        RequestForm requestForm = getRequestForm();
        Map<String, String> map = new HashMap<>();
        map.put(ScsbCommonConstants.INVALID_REQUEST, "Invalid Request");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void resubmitRequestFailed() {
        RequestForm requestForm = getRequestForm();
        Map<String, String> map = new HashMap<>();
        map.put(ScsbCommonConstants.FAILURE, "Resubmit Failed");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void resubmitRequestException() {
        RequestForm requestForm = getRequestForm();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void refreshStatus() {
        String reqJson = "{\"status\":[\"29-0\",\"5-1\"]}";
        Mockito.when(requestService.getRefreshedStatus(reqJson)).thenReturn(ScsbCommonConstants.COMPLETE_STATUS);
        String result = requestController.refreshStatus(reqJson);
        assertNotNull(result);
        assertEquals("Complete", result);

    }

    @Test
    public void exportExceptionReports() throws ParseException {
        String institution = "PUL";
        String fromDate = "03/11/2009";
        String toDate = "04/12/2011";
        Page<RequestItemEntity> requestItemEntities = new PageImpl<>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getRequestItemEntity().getInstitutionEntity()));
        ResponseEntity<RequestForm> responseEntity = requestController.exportExceptionReports(institution, fromDate, toDate);
        assertNotNull(responseEntity);
    }

    @Test
    public void exportExceptionReportsWithDateRange() throws ParseException {
        String institution = "PUL";
        String fromDate = "03/11/2009";
        String toDate = "04/12/2011";
        Page<RequestItemEntity> requestItemEntities = new PageImpl<>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getRequestItemEntity().getInstitutionEntity()));
        Mockito.when(requestServiceUtil.exportExceptionReportsWithDate(any(), any(), any(), any(), any())).thenReturn(requestItemEntities);
        ResponseEntity<RequestForm> responseEntity = requestController.exportExceptionReportsWithDateRange(institution, fromDate, toDate);
        assertNotNull(responseEntity);
    }

    @Test
    public void pageSizeChange() throws Exception {
        String institution = "PUL";
        String fromDate = "03/11/2009";
        String toDate = "04/12/2011";
        Page<RequestItemEntity> requestItemEntities = new PageImpl<>(Arrays.asList(getRequestItemEntity()));
        Map<String,Date> dateMap = new HashMap<>();
        dateMap.put("fromDate",new Date());
        dateMap.put("toDate",new Date());
        Mockito.when(scsbService.dateFormatter(any(),any())).thenReturn(dateMap);
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getRequestItemEntity().getInstitutionEntity()));
        Mockito.when(requestServiceUtil.exportExceptionReportsWithDate(any(), any(), any(), any(), any())).thenReturn(requestItemEntities);
        ResponseEntity<RequestForm> responseEntity = requestController.pageSizeChange(institution, fromDate, toDate, "10");
        assertNotNull(responseEntity);
    }

    @Test
    public void nextCallException() throws Exception {
        String institution = "PUL";
        String fromDate = "03/11/2009";
        String toDate = "04/12/2011";
        Page<RequestItemEntity> requestItemEntities = new PageImpl<>(Arrays.asList(getRequestItemEntity()));
        Map<String,Date> dateMap = new HashMap<>();
        dateMap.put("fromDate",new Date());
        dateMap.put("toDate",new Date());
        Mockito.when(scsbService.dateFormatter(any(),any())).thenReturn(dateMap);
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getRequestItemEntity().getInstitutionEntity()));
        Mockito.when(requestServiceUtil.exportExceptionReportsWithDate(any(), any(), any(), any(), any())).thenReturn(requestItemEntities);
        ResponseEntity<RequestForm> responseEntity = requestController.nextCallException(institution, fromDate, toDate, "2", "10");
        assertNotNull(responseEntity);
    }

    @Test
    public void pullTransactionRportCount() {
        TransactionReports transactionReports = getTransactionReports();
        transactionReports.setTrasactionCallType(ScsbConstants.COUNT);
        TransactionReport transactionReport = new TransactionReport(transactionReports.getTrasactionCallType(), "PUL", "PUL", "Complete", 1L);
        Mockito.when(requestServiceUtil.getTransactionReportCount(any(), any(), any())).thenReturn(Arrays.asList(transactionReport));
        ResponseEntity<TransactionReports> responseEntity = requestController.pullTransactionRportCount(transactionReports);
        assertNotNull(responseEntity);
    }

    @Test
    public void pullTransactionRepodrts() {
        TransactionReports transactionReports = getTransactionReports();
        transactionReports.setTrasactionCallType(ScsbConstants.REPORTS);
        ResponseEntity<TransactionReports> responseEntity = requestController.pullTransactionReports(transactionReports);
        assertNotNull(responseEntity);
    }

    @Test
    public void pullTransactionReportsExport() {
        TransactionReports transactionReports = getTransactionReports();
        transactionReports.setTrasactionCallType(ScsbConstants.EXPORT);
        ResponseEntity<TransactionReports> responseEntity = requestController.pullTransactionReportsExport(transactionReports);
        assertNotNull(responseEntity);
    }

    @Test
    public void pullTransactionReportsException() {
        TransactionReports transactionReports = new TransactionReports();
        ResponseEntity<TransactionReports> responseEntity = requestController.pullTransactionReportsExport(transactionReports);
        assertNotNull(responseEntity);
    }

    private TransactionReports getTransactionReports() {
        TransactionReports transactionReports = new TransactionReports();
        transactionReports.setMessage("test");
        transactionReports.setPageSize(1);
        transactionReports.setTotalPageCount(10);
        transactionReports.setOwningInsts(Arrays.asList("PUL"));
        transactionReports.setFromDate("03/11/2009");
        transactionReports.setToDate("04/12/2011");
        return transactionReports;
    }

    private CancelRequestResponse getCancelRequestResponse() {
        CancelRequestResponse cancelRequestResponse = new CancelRequestResponse();
        cancelRequestResponse.setSuccess(true);
        cancelRequestResponse.setScreenMessage("Request cancelled.");
        return cancelRequestResponse;
    }

    private OwnerCodeEntity getCustomerCodeEntity() {
        OwnerCodeEntity customerCodeEntity = new OwnerCodeEntity();
        customerCodeEntity.setOwnerCode("PG");
        customerCodeEntity.setDescription("Test");
        customerCodeEntity.setInstitutionId(1);
        customerCodeEntity.setId(1);
        return customerCodeEntity;
    }

    private JSONObject getJsonObject() {
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        jsonObject.put("1", "PA");
        jsonObject.put("2", "PB");
        array.put("RECALL");
        array.put("RETRIEVE");
        json.put("error", "No Error");
        json.put("noPermissionErrorMessage", "No");
        json.put("itemTitle", "testName");
        json.put("itemOwningInstitution", "CUL");
        json.put("deliveryLocation", jsonObject);
        json.put("requestTypes", array);
        return json;
    }

    private JSONObject getJsonObject1() {
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        jsonObject.put("1", "PA");
        jsonObject.put("2", "PB");
        array.put("RECALL");
        array.put("RETRIEVE");
        json.put("error", "No Error");
        json.put("errorMessage", "Request Error");
        json.put("itemTitle", "testName");
        json.put("itemOwningInstitution", "CUL");
        json.put("deliveryLocation", jsonObject);
        json.put("requestTypes", array);
        return json;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setRepositoryUser(true);
        userDetailsForm.setSuperAdmin(true);
        return userDetailsForm;
    }

    private RequestItemEntity getRequestItemEntity() {
        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusDescription("RECALL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RECALL");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setRequestStatusId(15);
        requestItemEntity.setId(16);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        requestItemEntity.setInstitutionEntity(institutionEntity);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Complete");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("CU12513083");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setOwningInstitutionId(3);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setItemEntity(itemEntity);
        requestItemEntity.setRequestingInstitutionId(2);
        return requestItemEntity;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }

    private ReplaceRequest getReplaceRequest() {
        ReplaceRequest replaceRequest = new ReplaceRequest();
        RequestForm requestForm = getRequestForm();
        replaceRequest.setEndRequestId("10");
        replaceRequest.setFromDate((new Date()).toString());
        replaceRequest.setToDate((new Date()).toString());
        replaceRequest.setReplaceRequestByType(ScsbCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus(ScsbConstants.EXCEPTION);
        String requestId = String.valueOf(requestForm.getRequestId());
        replaceRequest.setRequestIds(requestId);
        replaceRequest.setStartRequestId("1");
        return replaceRequest;
    }

    private RequestForm getRequestForm() {
        RequestForm requestForm = new RequestForm();
        requestForm.setRequestId(1);
        requestForm.setPatronBarcode("43265854");
        requestForm.setSubmitted(true);
        requestForm.setItemBarcode("32101074849843");
        requestForm.setStatus("active");
        requestForm.setDeliveryLocation("PB");
        requestForm.setVolumeNumber("1");
        requestForm.setMessage("testing");
        requestForm.setErrorMessage("error");
        requestForm.setIssue("issue");
        requestForm.setTotalPageCount(1);
        requestForm.setTotalRecordsCount("10");
        requestForm.setPageSize(1);
        requestForm.setPageNumber(1);
        requestForm.setInstitutionList(Arrays.asList("CU"));
        requestForm.setRequestingInstitutions(Arrays.asList("PUL"));
        requestForm.setRequestTypes(Arrays.asList("Recall"));
        requestForm.setItemBarcodeInRequest("123");
        requestForm.setPatronBarcodeInRequest("46259871");
        requestForm.setRequestingInstitution("CUL");
        requestForm.setPatronEmailAddress("test@email.com");
        requestForm.setInstitution("CU");
        requestForm.setItemTitle("test");
        requestForm.setItemOwningInstitution("PUL");
        requestForm.setRequestType("recall");
        requestForm.setRequestNotes("test");
        requestForm.setStartPage("2");
        requestForm.setEndPage("5");
        requestForm.setArticleAuthor("john");
        requestForm.setArticleTitle("test");
        requestForm.setDeliveryLocationInRequest("PB");
        return requestForm;
    }

    private DeliveryCodeEntity getDeliveryCodeEntity() {
        DeliveryCodeEntity deliveryCodeEntity = new DeliveryCodeEntity();
        deliveryCodeEntity.setId(1);
        deliveryCodeEntity.setDescription("Test");
        deliveryCodeEntity.setDeliveryCode("PA");
        deliveryCodeEntity.setAddress("Test");
        deliveryCodeEntity.setDeliveryCodeTypeId(1);
        deliveryCodeEntity.setActive('Y');
        return deliveryCodeEntity;
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setUsername("Admin");
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("46259871");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("test@email.com");
        itemRequestInformation.setTitle("test");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("recall");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setStartPage("2");
        itemRequestInformation.setEndPage("5");
        itemRequestInformation.setAuthor("john");
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setDeliveryLocation("PB");
        return itemRequestInformation;
    }

    private ItemResponseInformation getItemResponseInformation() {
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        itemResponseInformation.setPatronBarcode("46259871");
        itemResponseInformation.setItemBarcode("123");
        itemResponseInformation.setSuccess(Boolean.FALSE);
        return itemResponseInformation;
    }

    private UserDetailsForm getUserDetails() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setLoginInstitutionId(2);
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setRecapPermissionAllowed(false);
        userDetailsForm.setRepositoryUser(false);
        return userDetailsForm;
    }


    public Page getPage() {
        Page<RequestItemEntity> page = new Page<RequestItemEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 2;
            }

            @Override
            public <R> Page<R> map(Function<? super RequestItemEntity, ? extends R> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<RequestItemEntity> getContent() {

                return Arrays.asList(getRequestItemEntity());
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<RequestItemEntity> iterator() {
                return null;
            }
        };
        return page;
    }
}

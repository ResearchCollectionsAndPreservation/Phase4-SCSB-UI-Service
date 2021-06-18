package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.*;
import org.recap.model.search.*;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.service.SCSBService;
import org.recap.util.CollectionServiceUtil;
import org.recap.util.MarcRecordViewUtil;
import org.recap.util.SearchUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class CollectionControllerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    CollectionController collectionController;

    @Mock
    SearchUtil searchUtil;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    MarcRecordViewUtil marcRecordViewUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    CollectionServiceUtil collectionServiceUtil;

    @Mock
    SCSBService scsbService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    UserManagementService userManagementService;

    @Test
    public void displayRecords() throws Exception {
        SearchRecordsResponse searchRecordsResponse = getSearchRecordsResponse();
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setErrorMessage("No results found.");
        Mockito.when(searchUtil.requestSearchResults(any())).thenReturn(searchRecordsResponse);
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(getCollectionGroupEntity()));
        CollectionForm form = collectionController.displayRecords(collectionForm,request);
        assertNotNull(form);
    }

    @Test
    public void openMarcView() {
        CollectionForm collectionForm = new CollectionForm();
        collectionForm.setBibId(1);
        collectionForm.setItemId(1);
        BibliographicMarcForm bibliographicMarcForm = getBibilographicMarcForm();
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(request.getSession(false), ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(userDetailsRepository.findInstitutionCodeByUserName(any())).thenReturn("test");
        Mockito.when(itemDetailsRepository.findInstitutionCodeByBarcode(any())).thenReturn("test");
        Mockito.when(userDetailsRepository.getUserRoles(any())).thenReturn(Arrays.asList("test"));
        Mockito.when(scsbService.validateUserRoles(any(),any(),any())).thenReturn(Boolean.TRUE);
        Mockito.when(marcRecordViewUtil.buildBibliographicMarcForm(collectionForm.getBibId(), collectionForm.getItemId(), userDetailsForm)).thenReturn(bibliographicMarcForm);
        CollectionForm form = collectionController.openMarcView(collectionForm, request);
        assertNotNull(form);
    }

    @Test
    public void collectionUpdate() throws Exception {
        CollectionForm collectionForm = new CollectionForm();
        collectionForm.setCollectionAction("Update CGD");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("test");
        Mockito.doNothing().when(collectionServiceUtil).updateCGDForItem(collectionForm);
        CollectionForm form = collectionController.collectionUpdate(collectionForm, request);
        assertNotNull(form);
    }

    @Test
    public void collectionUpdateForDeaccession() throws Exception {
        CollectionForm collectionForm = new CollectionForm();
        collectionForm.setCollectionAction("Deaccession");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(ScsbConstants.USER_NAME)).thenReturn("test");
        CollectionForm form = collectionController.collectionUpdate(collectionForm, request);
        assertNotNull(form);
    }

    @Test
    public void checkCrossInstitutionBorrowed() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setBarcode("123556");
        collectionForm.setCollectionAction("Update CGD");
        Mockito.when(marcRecordViewUtil.getDeliveryLocationsList(any(), any())).thenReturn(Arrays.asList(getDeliveryCodeEntity()));
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getRequestItemEntity().getItemEntity()));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(anyString(), anyString())).thenReturn(new RequestItemEntity());
        collectionController.checkCrossInstitutionBorrowed(collectionForm);
    }

    @Test
    public void checkCrossInstitutionBorrowedWithNoActiveRecallRequest() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setBarcode("123556");
        collectionForm.setCollectionAction("Update CGD");
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Mockito.when(marcRecordViewUtil.getDeliveryLocationsList(any(), any())).thenReturn(Arrays.asList(getDeliveryCodeEntity()));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(collectionForm.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestItemEntity);
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getRequestItemEntity().getItemEntity()));
        collectionController.checkCrossInstitutionBorrowed(collectionForm);
    }

    @Test
    public void checkCrossInstitutionBorrowedWithNoActiveRecallRequestDiffInstCode() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setBarcode("123556");
        collectionForm.setCollectionAction("Update CGD");
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("HD");
        requestItemEntity.getItemEntity().setInstitutionEntity(institutionEntity);
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getRequestItemEntity().getItemEntity()));
        Mockito.when(marcRecordViewUtil.getDeliveryLocationsList(any(), any())).thenReturn(Arrays.asList(getDeliveryCodeEntity()));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(collectionForm.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)).thenReturn(requestItemEntity);
        collectionController.checkCrossInstitutionBorrowed(collectionForm);
    }

    @Test
    public void checkCrossInstitutionBorrowedWithNoActiveRetrievalRequest() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setBarcode("123556");
        collectionForm.setCollectionAction("Update CGD");
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Mockito.when(marcRecordViewUtil.getDeliveryLocationsList(any(), any())).thenReturn(Arrays.asList(getDeliveryCodeEntity()));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(collectionForm.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getRequestItemEntity().getItemEntity()));
        collectionController.checkCrossInstitutionBorrowed(collectionForm);
    }

    @Test
    public void checkCrossInstitutionBorrowedWithNoActiveRetrievalRequestDiffInstCode() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setBarcode("123556");
        collectionForm.setCollectionAction("DEACCESSION");
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("HD");
        requestItemEntity.getItemEntity().setInstitutionEntity(institutionEntity);
        Mockito.when(marcRecordViewUtil.getDeliveryLocationsList(any(), any())).thenReturn(Arrays.asList(getDeliveryCodeEntity()));
        Mockito.when(requestItemDetailsRepository.findByItemBarcodeAndRequestStaCode(collectionForm.getBarcode(), ScsbCommonConstants.REQUEST_STATUS_RECALLED)).thenReturn(requestItemEntity);
        collectionController.checkCrossInstitutionBorrowed(collectionForm);
    }

    @Test
    public void limitedBarcodes() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setItemBarcodes("1,1,1,2,2,2,3,3,3,4,4,4");
        ReflectionTestUtils.invokeMethod(collectionController, "limitedBarcodes", collectionForm);
    }

    @Test
    public void getMissingBarcodes() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setItemBarcodes("1,1,1,2,2,2,3,3,3,4,4,4");
        collectionForm.getSearchResultRows().get(0).setSearchItemResultRows(Arrays.asList(new SearchItemResultRow()));
        ReflectionTestUtils.invokeMethod(collectionController, "getMissingBarcodes", collectionForm);
    }

    @Test
    public void getMissingBarcodesWithoutItemBarcodes() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setItemBarcodes("");
        ReflectionTestUtils.invokeMethod(collectionController, "getMissingBarcodes", collectionForm);
    }

    @Test
    public void buildResultRowsWithoutItemBarcodes() {
        CollectionForm collectionForm = getCollectionForm();
        collectionForm.setItemBarcodes("");
        ReflectionTestUtils.invokeMethod(collectionController, "buildResultRows", collectionForm);
    }

    private SearchRecordsRequest getSearchRecordsRequest() {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldValue("23456");
        searchRecordsRequest.setFieldValue("BARCODE");
        searchRecordsRequest.setPageNumber(20);
        searchRecordsRequest.setSearchResultRows(Arrays.asList(getSearchResultRow()));
        searchRecordsRequest.setCatalogingStatus("Complete");
        searchRecordsRequest.setDeleted(false);
        searchRecordsRequest.setErrorMessage("ERROR");
        searchRecordsRequest.setTotalPageCount(1);
        return searchRecordsRequest;
    }

    private SearchRecordsResponse getSearchRecordsResponse() {
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        SearchResultRow searchResultRow1 = new SearchResultRow();
        searchResultRow1.setTitle("Title1");
        searchResultRow1.setAuthor("Author1");
        searchResultRow1.setPublisher("publisher1");
        searchResultRow1.setOwningInstitution("NYPL");
        searchResultRow1.setCollectionGroupDesignation("Shared");
        searchResultRow1.setSelected(true);
        searchRecordsResponse.setSearchResultRows(Arrays.asList(searchResultRow1));
        searchRecordsResponse.setTotalPageCount(2);
        searchRecordsResponse.setTotalBibRecordsCount("1");
        searchRecordsResponse.setTotalItemRecordsCount("1");
        searchRecordsResponse.setTotalRecordsCount("1");
        searchRecordsResponse.setShowTotalCount(false);
        searchRecordsResponse.setErrorMessage("test");
        return searchRecordsResponse;
    }

    private CollectionForm getCollectionForm() {
        CollectionForm collectionForm = new CollectionForm();
        CollectionForm collectionForm1 = new CollectionForm();
        collectionForm.setErrorMessage("test");
        collectionForm.setItemBarcodes("335454575437");
        collectionForm.setShowResults(false);
        collectionForm.setSelectAll(false);
        collectionForm.setBarcodesNotFoundErrorMessage("test");
        collectionForm.setIgnoredBarcodesErrorMessage("test");
        SearchResultRow searchResultRow = getSearchResultRow();
        collectionForm.setSearchResultRows(Arrays.asList(searchResultRow));
        collectionForm.setShowModal(false);
        collectionForm1.setSearchResultRows(null);
        assertFalse(collectionForm.isShowResults());
        assertFalse(collectionForm.isSelectAll());
        assertFalse(collectionForm.isShowModal());
        assertNotNull(collectionForm.getErrorMessage());
        assertNotNull(collectionForm.getBarcodesNotFoundErrorMessage());
        assertNotNull(collectionForm.getIgnoredBarcodesErrorMessage());
        return collectionForm;
    }

    private SearchResultRow getSearchResultRow() {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setShowItems(true);
        searchResultRow.setStatus("SUCCESS");
        searchResultRow.setRequestNotes("search");
        searchResultRow.setRequestType("search");
        searchResultRow.setRequestId(1);
        searchResultRow.setOwningInstitution("NYPL");
        searchResultRow.setDeliveryLocation("BA");
        searchResultRow.setItemId(1);
        searchResultRow.setLastUpdatedDate(new Date());
        searchResultRow.setCreatedDate(new Date());
        searchResultRow.setAuthor("test");
        return searchResultRow;
    }

    private BibliographicMarcForm getBibilographicMarcForm() {
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setTitle("test");
        bibliographicMarcForm.setAuthor("test");
        bibliographicMarcForm.setPublisher("test");
        bibliographicMarcForm.setPublishedDate(new Date().toString());
        bibliographicMarcForm.setOwningInstitution("PUL");
        bibliographicMarcForm.setCallNumber("123");
        bibliographicMarcForm.setMonographCollectionGroupDesignation("Complete");
        bibliographicMarcForm.setTag000("TAG000");
        bibliographicMarcForm.setControlNumber001("TAG001");
        bibliographicMarcForm.setControlNumber005("TAG005");
        bibliographicMarcForm.setControlNumber008("TAG008");
        BibDataField bibDataField = new BibDataField();
        bibDataField.setDataFieldValue("test");
        bibDataField.setDataFieldTag("TAG001");
        bibDataField.setIndicator1('y');
        bibDataField.setIndicator2('N');
        bibliographicMarcForm.setBibDataFields(Arrays.asList(bibDataField));
        bibliographicMarcForm.setAvailability("AVAILABLE");
        bibliographicMarcForm.setBarcode("12345");
        bibliographicMarcForm.setLocationCode("PA");
        bibliographicMarcForm.setUseRestriction("Allowed");
        bibliographicMarcForm.setCollectionGroupDesignation("Complete");
        bibliographicMarcForm.setNewCollectionGroupDesignation("Pending");
        bibliographicMarcForm.setCgdChangeNotes("CGDCHANGE#002");
        bibliographicMarcForm.setCustomerCode("3456");
        bibliographicMarcForm.setDeaccessionType("DENIED");
        bibliographicMarcForm.setDeaccessionNotes("test");
        DeliveryCodeEntity deliveryCodeEntity = new DeliveryCodeEntity();
        deliveryCodeEntity.setDeliveryCode("No");
        bibliographicMarcForm.setDeliveryLocations(Arrays.asList(deliveryCodeEntity));
        bibliographicMarcForm.setDeliveryLocation("PA");
        bibliographicMarcForm.setShared(true);
        bibliographicMarcForm.setSubmitted(true);
        bibliographicMarcForm.setMessage("SUCCESS");
        bibliographicMarcForm.setErrorMessage("ERROR");
        bibliographicMarcForm.setWarningMessage("WARN");
        bibliographicMarcForm.setCollectionAction("ACTIVE");
        bibliographicMarcForm.setAllowEdit(true);
        return bibliographicMarcForm;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(2);
        userDetailsForm.setRepositoryUser(false);
        return userDetailsForm;
    }

    private OwnerCodeEntity getCustomerCodeEntity() {
        OwnerCodeEntity customerCodeEntity = new OwnerCodeEntity();
        customerCodeEntity.setOwnerCode("PG");
        customerCodeEntity.setDescription("Test");
        customerCodeEntity.setInstitutionId(1);
        customerCodeEntity.setId(1);
        return customerCodeEntity;
    }

    private DeliveryCodeEntity getDeliveryCodeEntity() {
        DeliveryCodeEntity deliveryCodeEntity = new DeliveryCodeEntity();
        deliveryCodeEntity.setDeliveryCode("PG");
        deliveryCodeEntity.setDescription("Test");
        deliveryCodeEntity.setOwningInstitutionId(1);
        deliveryCodeEntity.setId(1);
        return deliveryCodeEntity;
    }

    private RequestItemEntity getRequestItemEntity() {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setCreatedBy("Test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setEmailId("test@gmail.com");
        requestItemEntity.setItemId(1);
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setPatronId("123");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setNotes("test");

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        requestItemEntity.setInstitutionEntity(institutionEntity);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("CU12513083");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setInstitutionEntity(institutionEntity);

        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("RecentlyReturned");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        requestItemEntity.setItemEntity(itemEntity);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("PA");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);

        return requestItemEntity;
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("GA");
        collectionGroupEntity.setCollectionGroupDescription("collection");
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setLastUpdatedDate(new Date());
        return collectionGroupEntity;
    }

}

package org.recap.service;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.*;
import org.recap.model.search.RequestForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.*;
import org.recap.util.PropertyUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.support.BindingAwareModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;

/**
 * Created by akulak on 24/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class RequestServiceUT extends BaseTestCaseUT {


    @InjectMocks
    @Spy
    RequestService requestService;

    @Mock
    BindingAwareModelMap model;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserDetailsForm userDetailsForm;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    InstitutionEntity institutionEntity;

    @Mock
    InstitutionEntity institutionEntityCUL;

    @Mock
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    RequestTypeEntity requestTypeEntity;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ItemEntity itemEntity;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    OwnerCodeEntity customerCodeEntity;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    UserDetailsForm userDetailsFormAuth;

    @Mock
    ItemStatusEntity itemStatusEntity;

    @Mock
    RequestStatusDetailsRepository requestStatusDetailsRepository;

    @Mock
    BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemEntity requestItemEntity;

    @Mock
    BulkRequestItemEntity bulkRequestItemEntity;

    @Mock
    RequestStatusEntity requestStatusEntity;

    @Value("${" + PropertyKeyConstants.SCSB_SUPPORT_INSTITUTION + "}")
    private String supportInstitution;

    @Before
    public void setup() {
        Mockito.when(requestService.getUserAuthUtil()).thenReturn(userAuthUtil);
    }

    @Test
    public void testGetRefreshedStatus() {
        List<String> listOfRequestStatusDesc = new ArrayList<>();
        listOfRequestStatusDesc.add(ScsbCommonConstants.REQUEST_STATUS_REFILED);
        Mockito.when(requestStatusDetailsRepository.findAllRequestStatusDescExceptProcessing()).thenReturn(listOfRequestStatusDesc);
        Mockito.when(bulkRequestDetailsRepository.findAllBulkRequestStatusDescExceptProcessing()).thenReturn(listOfRequestStatusDesc);
        String[] parameterValues = {"1-1"};
        Mockito.when(request.getParameterValues("status[]")).thenReturn(parameterValues);
        List<RequestItemEntity> requestItemEntityList = new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        List<BulkRequestItemEntity> bulkRequestItemEntitiyList = new ArrayList<>();
        bulkRequestItemEntitiyList.add(bulkRequestItemEntity);
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        Mockito.when(requestItemDetailsRepository.findByIdIn(any())).thenReturn(requestItemEntityList);
        Mockito.when(bulkRequestDetailsRepository.findByIdIn(any())).thenReturn(bulkRequestItemEntitiyList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusDescription()).thenReturn(ScsbCommonConstants.REQUEST_STATUS_REFILED);
        Mockito.when(bulkRequestItemEntity.getBulkRequestStatus()).thenReturn(ScsbCommonConstants.REQUEST_STATUS_REFILED);
        String reqJson = "{\"status\":[\"29-0\",\"5-1\"]}";
        String statusBulkRequest = requestService.getRefreshedStatus(reqJson,ScsbConstants.TRUE);
        assertNotNull(statusBulkRequest);
        String statusRequest = requestService.getRefreshedStatus(reqJson,ScsbConstants.FALSE);
        assertNotNull(statusRequest);
    }

    @Test
    public void testFindAllRequestStatusExceptProcessing() {
        List<RequestStatusEntity> requestStatusEntityList = new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestStatusDetailsRepository.findAllExceptProcessing()).thenReturn(requestStatusEntityList);
        Mockito.when(requestStatusEntity.getRequestStatusDescription()).thenReturn(ScsbCommonConstants.REQUEST_STATUS_REFILED);
        List<String> requestStatuses = new ArrayList<>();
        requestService.findAllRequestStatusExceptProcessing(requestStatuses);
        requestService.getRequestServiceUtil();
        assertTrue(true);
    }

    @Test
    public void sortDeliveryLocations() {
        Map<String, String> deliveryLocationsMap = new HashMap<>();
        deliveryLocationsMap.put("PA", "PC");
        deliveryLocationsMap.put("PB", "PC");
        Map<String, String> sortedDeliverLocationMap = requestService.sortDeliveryLocations(deliveryLocationsMap);
        assertNotNull(sortedDeliverLocationMap);
    }

    @Test
    public void testInstitutionForSuperAdmin() {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(institutionEntities);
        List<String> institutionList = new ArrayList<>();
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        requestService.getInstitutionForSuperAdmin(institutionList);
        assertTrue(true);
    }

    @Test
    public void testFormDetailsForRequestRecapUser() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(userDetailsForm.isRepositoryUser()).thenReturn(true);
        Mockito.when(userDetailsForm.isSuperAdmin()).thenReturn(true);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForItemTitle() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Map<String, String> deliveryLocationsMap = new LinkedHashMap<>();
        deliveryLocationsMap.put("deliveryLocations", "PA");
        deliveryLocationsMap.put("deliveryLocations", "CA");
        deliveryLocationsMap.put("deliveryLocations", "HD");
        deliveryLocationsMap.put("deliveryLocations", "CU");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ScsbConstants.REQUESTED_ITEM_TITLE, "Test Title");
        jsonObject.put(ScsbConstants.REQUESTED_ITEM_OWNING_INSTITUTION, "Test");
        jsonObject.put(ScsbConstants.REQUESTED_ITEM_STORAGE_LOCATION, "Test");
        jsonObject.put(ScsbConstants.DELIVERY_LOCATION, deliveryLocationsMap);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(userDetailsForm.isRepositoryUser()).thenReturn(true);
        Mockito.when(userDetailsForm.isSuperAdmin()).thenReturn(true);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        Mockito.doReturn(jsonObject.toString()).when(requestService).populateItemForRequest(any(), any());
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestUserNotPermitted() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestBlankBarcode() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("");
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put("UC", Boolean.TRUE.toString());
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        itemEntity.setBibliographicEntities(bibliographicEntities);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
    }

    @Test
    public void testFormDetailsForRequestPrivateItemsUserNotPermitted() throws Exception {
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put("UC", Boolean.TRUE.toString());
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        itemEntity.setBibliographicEntities(bibliographicEntities);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecord() throws Exception {
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(ScsbCommonConstants.NOT_AVAILABLE);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(getItemEntity().getBibliographicEntities().get(0));
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeIn(any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(userDetailsFormAuth.isRepositoryUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getOwnerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(anyString())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(ownerCodeDetailsRepository.findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(), any())).thenReturn(deliveryCodeObjects);
        ReflectionTestUtils.setField(requestService, "requestService", requestService);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecordCrossPartner() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntityCUL.getId()).thenReturn(2);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        Mockito.when(institutionEntityCUL.getInstitutionCode()).thenReturn("CUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(ScsbCommonConstants.NOT_AVAILABLE);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(getItemEntity().getBibliographicEntities().get(0));
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeIn(any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(userDetailsFormAuth.isRepositoryUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getOwnerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(anyString())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(ownerCodeDetailsRepository.findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(), any())).thenReturn(deliveryCodeObjects);
        ReflectionTestUtils.setField(requestService, "requestService", requestService);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecordExceptBorrowDirect() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(getItemEntity().getBibliographicEntities().get(0));
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeIn(any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(userDetailsFormAuth.isRepositoryUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getOwnerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(anyString())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(ownerCodeDetailsRepository.findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(),any())).thenReturn(deliveryCodeObjects);
        ReflectionTestUtils.setField(requestService, "requestService", requestService);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestMultiRecord() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptEDDAndBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345,12345");
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(ScsbCommonConstants.NOT_AVAILABLE);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(getItemEntity().getBibliographicEntities().get(0));
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeIn(any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(userDetailsFormAuth.isRepositoryUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getOwnerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(anyString())).thenReturn(getItemEntity().getInstitutionEntity());
        Mockito.when(ownerCodeDetailsRepository.findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(), any())).thenReturn(deliveryCodeObjects);
        ReflectionTestUtils.setField(requestService, "requestService", requestService);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
            RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
            assertNotNull(requestForm);
            assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestInvalidBarcode() throws Exception {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities = new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptEDDAndBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(ScsbCommonConstants.EDD);
        Mockito.when(model.get(ScsbConstants.REQUESTED_BARCODE)).thenReturn("12345,12345");
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(ScsbCommonConstants.NOT_AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(ScsbConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeIn(any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(userDetailsFormAuth.isRepositoryUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getOwnerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(any(), anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        ReflectionTestUtils.setField(requestService, "requestService", requestService);
        ReflectionTestUtils.setField(requestService, "supportInstitution", supportInstitution);
        RequestForm requestForm = requestService.setFormDetailsForRequest(model, request, userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void processCustomerAndDeliveryCodes() throws URISyntaxException, IOException {
        RequestForm requestForm = getRequestForm();
        Map<String, String> deliveryLocationsMap = new HashMap<>();
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        ItemEntity itemEntity = getItemEntity();
        Integer institutionId = 1;
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(any(), anyInt())).thenReturn(getOwnerCodeEntity());
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(requestForm.getRequestingInstitution())).thenReturn(itemEntity.getInstitutionEntity());
        Mockito.when(ownerCodeDetailsRepository.findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(), any())).thenReturn(deliveryCodeObjects);
        Mockito.when(ownerCodeDetailsRepository.findImsLocationDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(any(), any(), any())).thenReturn(deliveryCodeObjects);
        requestService.processCustomerAndDeliveryCodes(requestForm, deliveryLocationsMap, userDetailsForm, itemEntity, institutionId);
    }

    @Test
    public void sortDeliveryLocationForRecapUser() {
        Map<String, String> deliveryLocationsMap = new HashMap<>();
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        deliveryLocationsMap.put("deliveryLocations", "PA");
        deliveryLocationsMap.put("deliveryLocations", "CA");
        deliveryLocationsMap.put("deliveryLocations", "HD");
        deliveryLocationsMap.put("deliveryLocations", "CU");
        Mockito.when(requestService.sortDeliveryLocations(deliveryLocationsMap)).thenReturn(deliveryLocationsMap);
        ReflectionTestUtils.invokeMethod(requestService, "sortDeliveryLocationForRecapUser", deliveryLocationsMap, userDetailsForm);
    }

    @Test
    public void populateItemForRequest() throws JSONException, URISyntaxException, IOException {
        RequestForm requestForm = getRequestForm();
        Map<String, String> frozenInstitutionPropertyMap = new HashMap<>();
        frozenInstitutionPropertyMap.put("UC", Boolean.FALSE.toString());
        ItemEntity itemEntity = getItemEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when( userAuthUtil.getUserDetails(session, ScsbConstants.REQUEST_PRIVILEGE)).thenReturn(getUserDetailsForm());
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(anyString(), anyString())).thenReturn(itemEntities);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(anyString(), Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(frozenInstitutionPropertyMap);
        requestService.populateItemForRequest(requestForm,request);
    }


    @Test
    public void setDefaultsToCreateRequest() {
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        RequestForm requestForm = requestService.setDefaultsToCreateRequest(userDetailsForm);
        assertNotNull(requestForm);
    }

    private OwnerCodeEntity getOwnerCodeEntity() {
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setOwnerCode("PA");
        ownerCodeEntity.setDescription("test");
        ownerCodeEntity.setInstitutionId(1);
        return ownerCodeEntity;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(2);
        userDetailsForm.setRepositoryUser(true);
        return userDetailsForm;
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

    private File getBibContentFile() throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }

    private ItemEntity getItemEntity() throws URISyntaxException, IOException {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionId(2);
        itemEntity.setBarcode("CU12513083");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setCustomerCode("PA");
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setCollectionGroupId(ScsbConstants.CGD_PRIVATE);
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setImsLocationId(1);
        itemEntity.setImsLocationEntity(getImsLocationEntity());
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setOwningInstitutionId(3);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Success");
        itemStatusEntity.setStatusDescription("success");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        return itemEntity;
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
}

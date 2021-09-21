package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jpa.*;
import org.recap.model.reports.TransactionReport;
import org.recap.model.reports.TransactionReports;
import org.recap.model.search.RequestForm;
import org.recap.repository.jpa.*;
import org.recap.service.SCSBService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by rajeshbabuk on 29/10/16.
 */
public class RequestServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    RequestServiceUtil requestServiceUtil;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    private ImsLocationDetailRepository imsLocationDetailRepository;

    @Mock
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    SCSBService scsbService;

    @Test
    public void searchRequests()throws Exception {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntityPage = new PageImpl<RequestItemEntity>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(imsLocationDetailRepository.findByImsLocationCode(any())).thenReturn(getImsLocationEntity());
        Mockito.when(requestItemDetailsRepository.findByPatronBarcodeAndItemBarcodeAndActiveAndInstitution(any(),any(),any(),any(),any())).thenReturn(requestItemEntityPage);
        Page<RequestItemEntity> requestItemEntities = requestServiceUtil.searchRequests(requestForm);
        assertNotNull(requestItemEntities);
    }
    @Test
    public void searchRequestsWithNewInstitution()throws Exception {
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntityPage = new PageImpl<RequestItemEntity>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(imsLocationDetailRepository.findByImsLocationCode(any())).thenReturn(getImsLocationEntity());
        Mockito.when(requestItemDetailsRepository.findByPatronBarcodeAndItemBarcodeAndActiveAndInstitution(any(),any(),any(),any(),any())).thenReturn(requestItemEntityPage);
        Page<RequestItemEntity> requestItemEntities = requestServiceUtil.searchRequests(requestForm);
        assertNotNull(requestItemEntities);
    }
    @Test
    public void searchRequestsWithoutItemBarcode()throws Exception {
        RequestForm requestForm = getRequestForm();
        requestForm.setStatus(ScsbConstants.SEARCH_REQUEST_REFILED);
        requestForm.setItemBarcode("");
        requestForm.setPatronBarcode("");
        Page<RequestItemEntity> requestItemEntityPage = new PageImpl<RequestItemEntity>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(imsLocationDetailRepository.findByImsLocationCode(any())).thenReturn(getImsLocationEntity());
        Mockito.when(requestItemDetailsRepository.findByStatusAndInstitutionAndImslocation(any(),any(),any())).thenReturn(requestItemEntityPage);
        Page<RequestItemEntity> requestItemEntities = requestServiceUtil.searchRequests(requestForm);
        assertNotNull(requestItemEntities);
    }

    @Test
    public void exportExceptionReports(){
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(requestItemDetailsRepository.findByStatusAndInstitutionAndAll(any(), any(), any(), any())).thenReturn(Arrays.asList(getRequestItemEntity()));
        List<RequestItemEntity> requestItemEntities = requestServiceUtil.exportExceptionReports("PUL",new Date(),new Date());
        assertNotNull(requestItemEntities);
    }

    @Test
    public void exportExceptionReportsWithDate() throws ParseException {
        Page<RequestItemEntity> requestItemEntityPage = new PageImpl<RequestItemEntity>(Arrays.asList(getRequestItemEntity()));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(requestItemDetailsRepository.findByStatusAndInstitutionAndDateRange(any(), any(), any(), any(),any())).thenReturn(requestItemEntityPage);
        Page<RequestItemEntity> requestItemEntities = requestServiceUtil.exportExceptionReportsWithDate("PUL",new Date(),new Date(),1,10);
        assertNotNull(requestItemEntities);
    }

    @Test
    public void getTransactionReportCount(){
        TransactionReports transactionReports = getTransactionReports();
        Object[] object = {getTransactionReports(),1,2,1,1};
        List<Object[]> list = new ArrayList<>();
        list.add(object);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(Arrays.asList(getRequestItemEntity().getRequestTypeEntity()));
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(getCollectionGroupEntity()));
        Mockito.when(requestItemDetailsRepository.pullTransactionReportCount(any(), any(), any(), any(),any())).thenReturn(list);
        Mockito.when(scsbService.getKeysByValues(any(),any())).thenReturn(Arrays.asList(1));
        List<TransactionReport> transactionReportList = requestServiceUtil.getTransactionReportCount(transactionReports,new Date(),new Date());
        assertNotNull(transactionReportList);
    }

    @Test
    public void getTransactionReportss(){
        TransactionReports transactionReports = getTransactionReports();
        Object[] object = {getTransactionReports(),1,2,1,1,2,2,1,1,1,1};
        List<Object[]> list = new ArrayList<>();
        list.add(object);
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(getCollectionGroupEntity()));
        Mockito.when(requestItemDetailsRepository.findTransactionReportsByOwnAndReqInstWithStatus(any(),any(), any(), any(), any(),any(),any())).thenReturn(list);
        Mockito.when(scsbService.getKeysByValues(any(),any())).thenReturn(Arrays.asList(1));
        List<TransactionReport> transactionReportList = requestServiceUtil.getTransactionReports(transactionReports,new Date(),new Date());
        assertNotNull(transactionReportList);
    }

    @Test
    public void getTransactionReportsExport(){
        TransactionReports transactionReports = getTransactionReports();
        Object[] object = {getTransactionReports(),1,2,1,1,2,2,1,1,1,1};
        List<Object[]> list = new ArrayList<>();
        list.add(object);
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(getCollectionGroupEntity()));
        Mockito.when(requestItemDetailsRepository.findTransactionReportsByOwnAndReqInstWithStatusExport(any(),any(), any(), any(), any(),any())).thenReturn(list);
        Mockito.when(scsbService.getKeysByValues(any(),any())).thenReturn(Arrays.asList(1));
        Mockito.when(scsbService.pullCGDCodesList(any())).thenReturn(Arrays.asList("test"));
        List<TransactionReport> transactionReportList = requestServiceUtil.getTransactionReportsExport(transactionReports,new Date(),new Date());
        assertNotNull(transactionReportList);
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("GA");
        collectionGroupEntity.setCollectionGroupDescription("collection");
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setLastUpdatedDate(new Date());
        return collectionGroupEntity;
    }

    private TransactionReports getTransactionReports() {
        TransactionReports transactionReports = new TransactionReports();
        transactionReports.setMessage("test");
        transactionReports.setPageSize(1);
        transactionReports.setTotalPageCount(10);
        transactionReports.setOwningInsts(Arrays.asList("PUL"));
        transactionReports.setTrasactionCallType(ScsbConstants.REQUEST);
        return transactionReports;
    }

    private RequestForm getRequestForm() {
        RequestForm requestForm = new RequestForm();
        requestForm.setRequestId(1);
        requestForm.setPatronBarcode("43265854");
        requestForm.setSubmitted(true);
        requestForm.setItemBarcode("3324545547568535");
        requestForm.setStatus("active");
        requestForm.setDeliveryLocation("PB");
        requestForm.setVolumeNumber("1");
        requestForm.setMessage("testing");
        requestForm.setErrorMessage("testing");
        requestForm.setIssue("test");
        requestForm.setTotalPageCount(1);
        requestForm.setTotalRecordsCount("10");
        requestForm.setPageSize(1);
        requestForm.setPageNumber(1);
        requestForm.setRequestingInstitutions(Arrays.asList("PUL"));
        requestForm.setRequestTypes(Arrays.asList("Recall"));
        requestForm.setItemBarcodeInRequest("123");
        requestForm.setPatronBarcodeInRequest("46259871");
        requestForm.setRequestingInstitution("PUL");
        requestForm.setPatronEmailAddress("test@email.com");
        requestForm.setItemTitle("test");
        requestForm.setItemOwningInstitution("PUL");
        requestForm.setInstitution("PUL");
        requestForm.setRequestType("recall");
        requestForm.setRequestNotes("test");
        requestForm.setStartPage("2");
        requestForm.setEndPage("5");
        requestForm.setArticleAuthor("john");
        requestForm.setArticleTitle("test");
        requestForm.setDeliveryLocationInRequest("PB");
        requestForm.setDeliveryLocations(new ArrayList<OwnerCodeEntity>());
        requestForm.setSearchResultRows(new ArrayList<>());
        requestForm.setShowResults(true);
        requestForm.setStorageLocation("test");
        return requestForm;
    }
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setId(1);
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
    private RequestItemEntity getRequestItemEntity(){
        RequestStatusEntity requestStatusEntity=new RequestStatusEntity();
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
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setOwningInstitutionId(3);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);;
        requestItemEntity.setItemEntity(itemEntity);
        requestItemEntity.setRequestingInstitutionId(2);
        return requestItemEntity;
    }

}

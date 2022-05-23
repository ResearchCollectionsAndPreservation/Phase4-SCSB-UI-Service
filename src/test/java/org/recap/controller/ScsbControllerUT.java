package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.model.search.RequestForm;
import org.recap.model.search.SearchResultRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ScsbControllerUT extends BaseTestCaseUT {
    @Mock
    ScsbController scsbController;
    @Test
    public void setSearchResultRow(){
        RequestItemEntity requestItemEntity = getRequestItemEntity();
        Mockito.doCallRealMethod().when(scsbController).setSearchResultRow(requestItemEntity);
        SearchResultRow searchResultRow = scsbController.setSearchResultRow(requestItemEntity);
        assertNotNull(searchResultRow);
    }
    @Test
    public void disableRequestSearchInstitutionDropDown(){
        RequestForm requestForm = getRequestForm();
        Mockito.doCallRealMethod().when(scsbController).disableRequestSearchInstitutionDropDown(requestForm);
        scsbController.disableRequestSearchInstitutionDropDown(requestForm);
    }

    @Test
    public void disableRequestSearchInstitutionDropDownTest(){
        RequestForm requestForm = getRequestFormTest();
        Mockito.doCallRealMethod().when(scsbController).disableRequestSearchInstitutionDropDown(requestForm);
        scsbController.disableRequestSearchInstitutionDropDown(requestForm);
    }

    private RequestItemEntity getRequestItemEntity(){
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

        RequestStatusEntity requestStatusEntity=new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("RETRIEVAL_ORDER_PLACED");
        requestStatusEntity.setRequestStatusDescription("RETRIEVAL ORDER PLACED");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("PA");
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);

        return requestItemEntity;
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
        requestForm.setMessage("SUCCESS");
        requestForm.setErrorMessage("error");
        requestForm.setRequestingInstitutions(Arrays.asList("PUL"));
        requestForm.setRequestTypes(Arrays.asList("Recall"));
        requestForm.setItemBarcodeInRequest("123");
        requestForm.setPatronBarcodeInRequest("46259871");
        requestForm.setRequestingInstitution("PUL");
        requestForm.setPatronEmailAddress("test@email.com");
        requestForm.setInstitution("PUL");
        List<String> institutionList = new ArrayList<>();
        institutionList.add("PUL");
        requestForm.setInstitutionList(institutionList);
        return requestForm;
    }

    private RequestForm getRequestFormTest() {
        RequestForm requestForm = new RequestForm();
        requestForm.setRequestId(0);
        return requestForm;
    }
}
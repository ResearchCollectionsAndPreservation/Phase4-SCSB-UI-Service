package org.recap.service;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.search.BulkRequestForm;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akulak on 25/9/17.
 */
@Service
public class BulkSearchRequestService {

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Autowired
    private ImsLocationDetailRepository imsLocationDetailRepository;

    public Page<BulkRequestItemEntity> processSearchRequest(BulkRequestForm bulkRequestForm) {
        String requestId = StringUtils.isNotBlank(bulkRequestForm.getRequestIdSearch()) ? bulkRequestForm.getRequestIdSearch().trim() : bulkRequestForm.getRequestIdSearch();
        Integer bulkRequestId = 0;
        if(StringUtils.isNotBlank(requestId)){bulkRequestId = Integer.valueOf(requestId);}
        String bulkRequestName = StringUtils.isNotBlank(bulkRequestForm.getRequestNameSearch()) ? bulkRequestForm.getRequestNameSearch().trim() : bulkRequestForm.getRequestNameSearch();
        String patronId = StringUtils.isNotBlank(bulkRequestForm.getPatronBarcodeSearch()) ? bulkRequestForm.getPatronBarcodeSearch().trim() : bulkRequestForm.getPatronBarcodeSearch();
        bulkRequestName = (bulkRequestName == null)? "" : bulkRequestName;
        patronId = (patronId == null)? "" : patronId;
        String institution = StringUtils.isNotBlank(bulkRequestForm.getInstitution()) ? bulkRequestForm.getInstitution().trim() : bulkRequestForm.getInstitution();
        Integer storageLcoation = (!bulkRequestForm.getStorageLocation().isEmpty()) ? imsLocationDetailRepository.findByImsLocationCode(bulkRequestForm.getStorageLocation()).getId() : 0 ;
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institution);
        Integer requestingInstitutionId = 0;
        if (institutionEntity != null){
            requestingInstitutionId = institutionEntity.getId();
        }
        Pageable pageable = PageRequest.of(bulkRequestForm.getPageNumber(), bulkRequestForm.getPageSize(), Sort.Direction.DESC,"id");
        return bulkRequestDetailsRepository.findBulkRequestItems(pageable,bulkRequestId,bulkRequestName,patronId,requestingInstitutionId,storageLcoation);
    }
}

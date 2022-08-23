package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.DeliveryCodeEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.recap.model.search.BibDataField;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rajeshbabuk on 17/10/16.
 */
@Service
public class MarcRecordViewUtil {

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Autowired
    private RequestService requestService;

    @Value("${" + PropertyKeyConstants.NONHOLDINGID_INSTITUTION + "}")
    private String nonHoldInstitution;

    /**
     * This method used to get information about the item or bib from the scsb database for the given bib id or item id and
     * build that information in the bibliographic marc form.
     *
     * @param bibId           the bib id
     * @param itemId          the item id
     * @param userDetailsForm the user details form
     * @return the bibliographic marc form
     */
    public BibliographicMarcForm buildBibliographicMarcForm(Integer bibId, Integer itemId, UserDetailsForm userDetailsForm) {
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setCollectionAction(ScsbCommonConstants.UPDATE_CGD);
        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS);
        if (null == bibliographicEntity) {
            bibliographicMarcForm.setErrorMessage(ScsbConstants.RECORD_NOT_AVAILABLE);
        } else {
            InstitutionEntity institutionEntity = bibliographicEntity.getInstitutionEntity();
            bibliographicMarcForm.setAllowEdit(true);
            if(userDetailsForm!=null &&  !userDetailsForm.isSuperAdmin() && !userDetailsForm.getLoginInstitutionId().equals(institutionEntity.getId())) {
                bibliographicMarcForm.setErrorMessage(ScsbCommonConstants.ACCESS_RESTRICTED);
                bibliographicMarcForm.setAllowEdit(false);
            }
                bibliographicMarcForm.setBibId(bibliographicEntity.getId());
                String bibContent = new String(bibliographicEntity.getContent());
                BibJSONUtil bibJSONUtil = new BibJSONUtil();
                List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
                Record marcRecord = records.get(0);
                setBibliographicMarcFormWithBibValues(marcRecord, bibJSONUtil, bibliographicMarcForm);
                bibliographicMarcForm.setContent(bibContent);
                if (null != institutionEntity) {
                    bibliographicMarcForm.setOwningInstitution(institutionEntity.getInstitutionCode());
                }
                List<ItemEntity> nonDeletedItemEntities = bibliographicDetailsRepository.getNonDeletedItemEntities(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId(), ScsbCommonConstants.COMPLETE_STATUS);
                if (CollectionUtils.isNotEmpty(nonDeletedItemEntities)) {
                    if (nonDeletedItemEntities.size() == 1 && ScsbCommonConstants.MONOGRAPH.equals(bibliographicMarcForm.getLeaderMaterialType())) {
                        CollectionGroupEntity collectionGroupEntity = nonDeletedItemEntities.get(0).getCollectionGroupEntity();
                        if (null != collectionGroupEntity) {
                            bibliographicMarcForm.setMonographCollectionGroupDesignation(collectionGroupEntity.getCollectionGroupCode());
                            bibliographicMarcForm.setLocationCode(nonDeletedItemEntities.get(0).getImsLocationEntity().getImsLocationCode());
                        }
                    } else if (!bibliographicEntity.getInstitutionEntity().getInstitutionCode().equalsIgnoreCase(nonHoldInstitution)) {
                        if (nonDeletedItemEntities.size() > 1 && bibliographicEntity.getHoldingsEntities().size() >= 1 && ScsbCommonConstants.MONOGRAPH.equals(bibliographicMarcForm.getLeaderMaterialType())) {
                            bibliographicMarcForm.setLeaderMaterialType(ScsbConstants.MVM);
                        }
                    } else {
                        for (ItemEntity itemEntity : nonDeletedItemEntities)
                            if (itemEntity.getCopyNumber() != null && itemEntity.getCopyNumber() > 1) {
                                bibliographicMarcForm.setLeaderMaterialType(ScsbConstants.MVM);
                            }
                    }
                    bibliographicMarcForm.setCallNumber(nonDeletedItemEntities.get(0).getCallNumber());
                    if (null != itemId) {
                        for (ItemEntity itemEntity : nonDeletedItemEntities) {
                            if (itemEntity.getId().intValue() == itemId) {
                                bibliographicMarcForm.setItemId(itemEntity.getId());
                                bibliographicMarcForm.setBarcode(itemEntity.getBarcode());
                                bibliographicMarcForm.setUseRestriction(itemEntity.getUseRestrictions());
                                bibliographicMarcForm.setCallNumber(itemEntity.getCallNumber());
                                bibliographicMarcForm.setCustomerCode(itemEntity.getCustomerCode());
                                bibliographicMarcForm.setLocationCode(itemEntity.getImsLocationEntity().getImsLocationCode());
                                ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
                                if (null != itemStatusEntity) {
                                    bibliographicMarcForm.setAvailability(itemStatusEntity.getStatusCode());
                                }
                                CollectionGroupEntity collectionGroupEntity = itemEntity.getCollectionGroupEntity();
                                if (null != collectionGroupEntity) {
                                    bibliographicMarcForm.setCollectionGroupDesignation(collectionGroupEntity.getCollectionGroupCode());
                                    bibliographicMarcForm.setNewCollectionGroupDesignation(collectionGroupEntity.getCollectionGroupCode());
                                    if (ScsbCommonConstants.SHARED_CGD.equals(bibliographicMarcForm.getCollectionGroupDesignation())) {
                                        bibliographicMarcForm.setShared(Boolean.TRUE);
                                    }
                                }
                                if (StringUtils.isNotBlank(bibliographicMarcForm.getAvailability())) {
                                    if (ScsbCommonConstants.AVAILABLE.equals(bibliographicMarcForm.getAvailability())) {
                                        bibliographicMarcForm.setDeaccessionType(ScsbCommonConstants.PERMANENT_WITHDRAWAL_DIRECT);
                                    } else if (ScsbCommonConstants.NOT_AVAILABLE.equals(bibliographicMarcForm.getAvailability())) {
                                        bibliographicMarcForm.setDeaccessionType(ScsbCommonConstants.PERMANENT_WITHDRAWAL_INDIRECT);
                                    }
                                }
                                List<DeliveryCodeEntity> deliveryLocations = getDeliveryLocationsList(bibliographicMarcForm.getCustomerCode(), itemEntity.getOwningInstitutionId());
                                bibliographicMarcForm.setDeliveryLocations(deliveryLocations);
                            }
                        }
                        if (null == bibliographicMarcForm.getItemId()) {
                            bibliographicMarcForm.setErrorMessage(ScsbConstants.RECORD_NOT_AVAILABLE);
                        }
                    }
                }
        }
        return bibliographicMarcForm;
    }

    private static void setBibliographicMarcFormWithBibValues(Record marcRecord, BibJSONUtil bibJSONUtil, BibliographicMarcForm bibliographicMarcForm) {
        bibliographicMarcForm.setTitle(bibJSONUtil.getTitleDisplay(marcRecord));
        bibliographicMarcForm.setAuthor(bibJSONUtil.getAuthorDisplayValue(marcRecord));
        bibliographicMarcForm.setPublisher(bibJSONUtil.getPublisherValue(marcRecord));
        bibliographicMarcForm.setPublishedDate(bibJSONUtil.getPublicationDateValue(marcRecord));
        bibliographicMarcForm.setLeaderMaterialType(bibJSONUtil.getLeaderMaterialType(marcRecord.getLeader()));
        bibliographicMarcForm.setTag000(bibJSONUtil.getLeader(marcRecord));
        bibliographicMarcForm.setControlNumber001(bibJSONUtil.getControlFieldValue(marcRecord, "001"));
        bibliographicMarcForm.setControlNumber005(bibJSONUtil.getControlFieldValue(marcRecord, "005"));
        bibliographicMarcForm.setControlNumber008(bibJSONUtil.getControlFieldValue(marcRecord, "008"));
        bibliographicMarcForm.setBibDataFields(buildBibDataFields(marcRecord));
    }

    private static List<BibDataField> buildBibDataFields(Record marcRecord) {
        List<BibDataField> bibDataFields = new ArrayList<>();
        List<DataField> marcDataFields = marcRecord.getDataFields();
        if (!CollectionUtils.isEmpty(marcDataFields)) {
            for (DataField marcDataField : marcDataFields) {
                BibDataField bibDataField = new BibDataField();
                bibDataField.setDataFieldTag(marcDataField.getTag());
                if (Character.isWhitespace(marcDataField.getIndicator1())) {
                    bibDataField.setIndicator1('_');
                } else {
                    bibDataField.setIndicator1(marcDataField.getIndicator1());
                }
                if (Character.isWhitespace(marcDataField.getIndicator2())) {
                    bibDataField.setIndicator2('_');
                } else {
                    bibDataField.setIndicator2(marcDataField.getIndicator2());
                }
                List<Subfield> subfields = marcDataField.getSubfields();
                if (!CollectionUtils.isEmpty(subfields)) {
                    StringBuilder buffer = new StringBuilder();
                    for (Subfield subfield : subfields) {
                        buffer.append("|").append(subfield.getCode());
                        buffer.append(" ").append(subfield.getData()).append(" ");
                    }
                    bibDataField.setDataFieldValue(buffer.toString());
                }
                bibDataFields.add(bibDataField);
            }
        }
        Collections.sort(bibDataFields);
        return bibDataFields;
    }


    public List<DeliveryCodeEntity> getDeliveryLocationsList(String customerCode, Integer institutionId) {
        OwnerCodeEntity ownerCodeEntity = ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(customerCode, institutionId);
        List<DeliveryCodeEntity> deliveryLocations = new ArrayList<>();
        if (null != ownerCodeEntity) {
            List<DeliveryCodeEntity> deliveryCodeEntities = new ArrayList<>();
            List<Object[]> deliveryCodeObjects = ownerCodeDetailsRepository.findDeliveryRestrictionsByOwnerCodeIdAndDeliveryRestrictType(ownerCodeEntity.getId(), ScsbConstants.PWD_DELIVERY_RESTRICT_TYPE);
            deliveryCodeEntities = requestService.prepareDeliveryCodeEntities(deliveryCodeEntities, deliveryCodeObjects);
            Collections.sort(deliveryCodeEntities);
            return deliveryCodeEntities;
        }
        return deliveryLocations;
    }

}

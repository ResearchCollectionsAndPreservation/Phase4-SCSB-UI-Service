package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcException;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.search.CollectionForm;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchRecordsResponse;
import org.recap.model.search.SearchResultRow;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.CollectionServiceUtil;
import org.recap.util.MarcRecordViewUtil;
import org.recap.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rajeshbabuk on 12/10/16.
 */
@RestController
@RequestMapping("/collection")
@CrossOrigin
public class CollectionController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);

    @Autowired
    private SearchUtil searchUtil;

    @Autowired
    private MarcRecordViewUtil marcRecordViewUtil;

    @Autowired
    private CollectionServiceUtil collectionServiceUtil;


    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    /**
     * Gets marc record view util.
     *
     * @return the marc record view util
     */
    public MarcRecordViewUtil getMarcRecordViewUtil() {
        return marcRecordViewUtil;
    }

    /**
     * Gets collection service util.
     *
     * @return the collection service util
     */
    public CollectionServiceUtil getCollectionServiceUtil() {
        return collectionServiceUtil;
    }

    /**
     * Gets request item details repository.
     *
     * @return the request item details repository
     */
    public RequestItemDetailsRepository getRequestItemDetailsRepository() {
        return requestItemDetailsRepository;
    }


    /**
     * Render the collection UI page for the scsb application.
     *
     * @param model   the model
     * @param request the request
     * @return the stringCreating filter chain:
     */

    @RequestMapping(path = "/collection")
    public String collection(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_COLLECTION_URL);
        if (authenticated) {
            CollectionForm collectionForm = new CollectionForm();
            model.addAttribute(RecapConstants.COLLECTION_FORM, collectionForm);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.COLLECTION);
            return RecapConstants.VIEW_SEARCH_RECORDS;
        } else {
            return UserManagementService.unAuthorizedUser(session, "Collection", logger);
        }
    }

    /**
     * Perform search on solr based on the item barcodes and returns the results as rows to get displayed in the collection UI page.
     *
     * @param collectionForm the collection form
     * @param result         the result
     * @param model          the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/displayRecords")
    public CollectionForm displayRecords(@RequestBody CollectionForm collectionForm) throws Exception {
            return searchAndSetResults(collectionForm);
    }

    /**
     * Upon click on the title in the search result row in collection UI page, a popup box is opened with marc information as well as to perform collection updates.
     *
     * @param collectionForm the collection form
     * @param result         the result
     * @param model          the model
     * @param request        the request
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/openMarcView")
    public CollectionForm openMarcView(@RequestBody CollectionForm collectionForm) throws MarcException {

        //UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
        UserDetailsForm userDetailsForm = new UserDetailsForm(1,true,true,true);
        BibliographicMarcForm bibliographicMarcForm = getMarcRecordViewUtil().buildBibliographicMarcForm(collectionForm.getBibId(), collectionForm.getItemId(), userDetailsForm);
         CollectionForm collectionFormN =populateCollectionForm(collectionForm, bibliographicMarcForm);
        collectionFormN.setAvailability("In Recap");
        collectionFormN.setBarcode(collectionForm.getItemBarcodes());
        collectionFormN.setCustomerCode("CU");
        collectionFormN.setCollectionGroupDesignation(collectionForm.getCollectionGroupDesignation());
        collectionFormN.setDeaccessionType("Permanent Withdrawal Direct (PWD)");
        collectionFormN.setCollectionGroupDesignation(collectionForm.getMonographCollectionGroupDesignation());
        List<CustomerCodeEntity> customerCodeEntityList = new ArrayList<>();
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setPwdDeliveryRestrictions("ABC");
        CustomerCodeEntity customerCodeEntity1 = new CustomerCodeEntity();
        customerCodeEntity1.setPwdDeliveryRestrictions("AC");
        customerCodeEntityList.add(customerCodeEntity);
        customerCodeEntityList.add(customerCodeEntity1);
        collectionFormN.setDeliveryLocations(customerCodeEntityList);
        return collectionFormN;
    }

    /**
     * To perform operations update cgd or deaccession for the selected item in the collection UI page.
     *
     * @param collectionForm the collection form
     * @param result         the result
     * @param model          the model
     * @param request        the request
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/collectionUpdate")
    public CollectionForm collectionUpdate(@RequestBody CollectionForm collectionForm) throws Exception {
        //HttpSession session = request.getSession(false);
        //String username = (String) session.getAttribute(RecapConstants.USER_NAME);
        collectionForm.setUsername("dinakartest");
        if (RecapCommonConstants.UPDATE_CGD.equalsIgnoreCase(collectionForm.getCollectionAction())) {
            getCollectionServiceUtil().updateCGDForItem(collectionForm);
        } else if (RecapCommonConstants.DEACCESSION.equalsIgnoreCase(collectionForm.getCollectionAction())) {
            getCollectionServiceUtil().deAccessionItem(collectionForm);
        }
        collectionForm.setAllowEdit(true);
        return collectionForm;
       // return new ModelAndView("collection :: #itemDetailsSection", RecapConstants.COLLECTION_FORM, collectionForm);
    }

    /**
     * This method is to check whether the item is cross instituion borrowed while performing collection update.
     *
     * @param collectionForm the collection form
     * @param result         the result
     * @param model          the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/checkCrossInstitutionBorrowed")
    public CollectionForm checkCrossInstitutionBorrowed(@RequestBody CollectionForm collectionForm) {
        String itemBarcode = collectionForm.getBarcode();
        String warningMessage = null;
        List<CustomerCodeEntity> deliveryLocations = marcRecordViewUtil.getDeliveryLocationsList(collectionForm.getCustomerCode());
        collectionForm.setDeliveryLocations(deliveryLocations);
        RequestItemEntity activeRetrievalRequest = getRequestItemDetailsRepository().findByItemBarcodeAndRequestStaCode(itemBarcode, RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
        RequestItemEntity activeRecallRequest = getRequestItemDetailsRepository().findByItemBarcodeAndRequestStaCode(itemBarcode, RecapCommonConstants.REQUEST_STATUS_RECALLED);
        if (null != activeRetrievalRequest && null != activeRecallRequest) {
            warningMessage = RecapConstants.WARNING_MESSAGE_REQUEST_BORROWED_ITEM;
        } else if (null != activeRetrievalRequest && null == activeRecallRequest) {
            String itemOwningInstitution = activeRetrievalRequest.getItemEntity().getInstitutionEntity().getInstitutionCode();
            String retrievalRequestingInstitution = activeRetrievalRequest.getInstitutionEntity().getInstitutionCode();
            if (!itemOwningInstitution.equalsIgnoreCase(retrievalRequestingInstitution)) {
                warningMessage = RecapConstants.WARNING_MESSAGE_RETRIEVAL_CROSS_BORROWED_ITEM;
            } else {
                warningMessage = RecapConstants.WARNING_MESSAGE_RETRIEVAL_BORROWED_ITEM;
            }
        } else if (null == activeRetrievalRequest && null != activeRecallRequest) {
            String itemOwningInstitution = activeRecallRequest.getItemEntity().getInstitutionEntity().getInstitutionCode();
            String recallRequestingInstitution = activeRecallRequest.getInstitutionEntity().getInstitutionCode();
            if (!itemOwningInstitution.equalsIgnoreCase(recallRequestingInstitution)) {
                warningMessage = RecapConstants.WARNING_MESSAGE_RECALL_CROSS_BORROWED_ITEM;
            } else {
                warningMessage = RecapConstants.WARNING_MESSAGE_RECALL_BORROWED_ITEM;
            }
        }

        if (StringUtils.isNotBlank(warningMessage)) {
            if (RecapCommonConstants.UPDATE_CGD.equalsIgnoreCase(collectionForm.getCollectionAction())) {
                collectionForm.setWarningMessage(warningMessage);
            } else if (RecapCommonConstants.DEACCESSION.equalsIgnoreCase(collectionForm.getCollectionAction())) {
                collectionForm.setWarningMessage(warningMessage + " " + RecapConstants.WARNING_MESSAGE_DEACCESSION_REQUEST_BORROWED_ITEM);
            }
        }
        collectionForm.setWarningMessage(RecapConstants.WARNING_MESSAGE_RECALL_BORROWED_ITEM);
        return collectionForm;
        //return new ModelAndView("collection :: #itemDetailsSection", RecapConstants.COLLECTION_FORM, collectionForm);
    }

    private CollectionForm searchAndSetResults(CollectionForm collectionForm) throws Exception {
        return buildMissingBarcodes(buildResultRows(trimBarcodes(collectionForm)));
    }

    private String limitedBarcodes(CollectionForm collectionForm) {
        String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
        if (barcodeArray.length > RecapCommonConstants.BARCODE_LIMIT) {
            String[] limitBarcodeArray = Arrays.copyOfRange(barcodeArray, 0, RecapCommonConstants.BARCODE_LIMIT);
            collectionForm.setIgnoredBarcodesErrorMessage(RecapConstants.BARCODE_LIMIT_ERROR + " - " + StringUtils.join(Arrays.copyOfRange(barcodeArray, RecapCommonConstants.BARCODE_LIMIT, barcodeArray.length), ","));
            return StringUtils.join(limitBarcodeArray, ",");
        }
        return StringUtils.join(barcodeArray, ",");
    }

    private CollectionForm trimBarcodes(CollectionForm collectionForm) {
        List<String> barcodeList = new ArrayList<>();
        String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
        for (String barcode : barcodeArray) {
            if (StringUtils.isNotBlank(barcode)) {
                String itemBarcode = barcode.trim();
                barcodeList.add(itemBarcode);
            }
        }
        collectionForm.setItemBarcodes(StringUtils.join(barcodeList, ","));
        return collectionForm;
    }

    private CollectionForm buildResultRows(CollectionForm collectionForm) {
        if (StringUtils.isNotBlank(collectionForm.getItemBarcodes())) {
            SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
            searchRecordsRequest.setFieldName(RecapCommonConstants.BARCODE);
            searchRecordsRequest.setFieldValue(limitedBarcodes(collectionForm));

            SearchRecordsResponse searchRecordsResponse = searchUtil.requestSearchResults(searchRecordsRequest);
            List<SearchResultRow> searchResultRows = searchRecordsResponse.getSearchResultRows();
            collectionForm.setSearchResultRows(Collections.emptyList());
            if (CollectionUtils.isNotEmpty(searchResultRows)) {
                collectionForm.setSearchResultRows(searchResultRows);
                collectionForm.setSelectAll(false);
            }
        } else {
            collectionForm.setErrorMessage(RecapCommonConstants.NO_RESULTS_FOUND);
        }
        collectionForm.setShowResults(true);
        return collectionForm;
    }

    private CollectionForm buildMissingBarcodes(CollectionForm collectionForm) {
        Set<String> missingBarcodes = getMissingBarcodes(collectionForm);
        if (CollectionUtils.isNotEmpty(missingBarcodes)) {
            collectionForm.setBarcodesNotFoundErrorMessage(RecapCommonConstants.BARCODES_NOT_FOUND + " - " + StringUtils.join(missingBarcodes, ","));
        }
        return collectionForm;
    }

    private Set<String> getMissingBarcodes(CollectionForm collectionForm) {
        if (StringUtils.isNotBlank(collectionForm.getItemBarcodes())) {
            String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
            if (barcodeArray.length > RecapCommonConstants.BARCODE_LIMIT) {
                barcodeArray = Arrays.copyOfRange(barcodeArray, 0, RecapCommonConstants.BARCODE_LIMIT);
            }
            Set<String> missingBarcodes = new HashSet<>(Arrays.asList(barcodeArray));
            for (SearchResultRow searchResultRow : collectionForm.getSearchResultRows()) {
                String barcode = searchResultRow.getBarcode();
                if (StringUtils.isBlank(barcode) && CollectionUtils.isNotEmpty(searchResultRow.getSearchItemResultRows())) {
                    SearchItemResultRow searchItemResultRow = searchResultRow.getSearchItemResultRows().get(0);
                    barcode = searchItemResultRow.getBarcode();
                    searchResultRow.setBarcode(barcode);
                    searchResultRow.setItemId(searchItemResultRow.getItemId());
                    searchResultRow.setCollectionGroupDesignation(searchItemResultRow.getCollectionGroupDesignation());
                }
                missingBarcodes.remove(barcode);
            }
            return missingBarcodes;
        }
        return Collections.emptySet();
    }

    private CollectionForm populateCollectionForm(CollectionForm collectionForm, BibliographicMarcForm bibliographicMarcForm) {
        collectionForm.setTitle(bibliographicMarcForm.getTitle());
        collectionForm.setAuthor(bibliographicMarcForm.getAuthor());
        collectionForm.setPublisher(bibliographicMarcForm.getPublisher());
        collectionForm.setPublishedDate(bibliographicMarcForm.getPublishedDate());
        collectionForm.setOwningInstitution(bibliographicMarcForm.getOwningInstitution());
        collectionForm.setCallNumber(bibliographicMarcForm.getCallNumber());
        collectionForm.setMonographCollectionGroupDesignation(bibliographicMarcForm.getMonographCollectionGroupDesignation());
        collectionForm.setTag000(bibliographicMarcForm.getTag000());
        collectionForm.setControlNumber001(bibliographicMarcForm.getControlNumber001());
        collectionForm.setControlNumber005(bibliographicMarcForm.getControlNumber005());
        collectionForm.setControlNumber008(bibliographicMarcForm.getControlNumber008());
        collectionForm.setBibDataFields(bibliographicMarcForm.getBibDataFields());
        collectionForm.setAvailability(bibliographicMarcForm.getAvailability());
        collectionForm.setBarcode(bibliographicMarcForm.getBarcode());
        collectionForm.setLocationCode(bibliographicMarcForm.getLocationCode());
        collectionForm.setUseRestriction(bibliographicMarcForm.getUseRestriction());
        collectionForm.setCollectionGroupDesignation(bibliographicMarcForm.getCollectionGroupDesignation());
        collectionForm.setNewCollectionGroupDesignation(bibliographicMarcForm.getNewCollectionGroupDesignation());
        collectionForm.setCgdChangeNotes(bibliographicMarcForm.getCgdChangeNotes());
        collectionForm.setCustomerCode(bibliographicMarcForm.getCustomerCode());
        collectionForm.setDeaccessionType(bibliographicMarcForm.getDeaccessionType());
        collectionForm.setDeaccessionNotes(bibliographicMarcForm.getDeaccessionNotes());
        collectionForm.setDeliveryLocations(bibliographicMarcForm.getDeliveryLocations());
        collectionForm.setDeliveryLocation(bibliographicMarcForm.getDeliveryLocation());
        collectionForm.setShared(bibliographicMarcForm.isShared());
        collectionForm.setSubmitted(bibliographicMarcForm.isSubmitted());
        collectionForm.setMessage(bibliographicMarcForm.getMessage());
        collectionForm.setErrorMessage(bibliographicMarcForm.getErrorMessage());
        collectionForm.setWarningMessage(bibliographicMarcForm.getWarningMessage());
        collectionForm.setCollectionAction(bibliographicMarcForm.getCollectionAction());
        collectionForm.setShowModal(true);
        collectionForm.setShowResults(true);
        collectionForm.setAllowEdit(bibliographicMarcForm.isAllowEdit());
        return collectionForm;
    }

}

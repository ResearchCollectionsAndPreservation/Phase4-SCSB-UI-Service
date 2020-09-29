package org.recap.controller;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.CancelRequestResponse;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.request.ItemResponseInformation;
import org.recap.model.request.PopulateItem;
import org.recap.model.request.ReplaceRequest;
import org.recap.model.search.RequestForm;
import org.recap.model.search.SearchResultRow;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.service.RequestService;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by rajeshbabuk on 13/10/16.
 */

@RestController
@Getter
@Setter
@CrossOrigin
@RequestMapping("/request")
public class RequestController extends RecapController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);


    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private SecurityUtil securityUtil;

    public RequestService getRequestService() {
        return requestService;
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    /**
     * Render the request UI page for the scsb application.
     *
     * @param model   the model
     * @param request the request
     * @return the string
     * @throws JSONException the json exception
     */
    @GetMapping("/request")
    public String request(Model model, HttpServletRequest request) throws JSONException {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_REQUEST_URL);
        if (authenticated) {
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.REQUEST_PRIVILEGE);
            RequestForm requestForm = getRequestService().setFormDetailsForRequest(model, request, userDetailsForm);
            model.addAttribute(RecapConstants.REQUEST_FORM, requestForm);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
            return RecapConstants.VIEW_SEARCH_RECORDS;
        } else {
            return UserManagementService.unAuthorizedUser(session, "Request", logger);
        }
    }

    /**
     * Get results from scsb database and display them as row based on the search conditions provided in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/searchRequests")
    public RequestForm searchRequests(@RequestBody RequestForm requestForm) {
        try {
            requestForm = setSearch(requestForm);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            logger.debug(exception.getMessage());
        }
        return requestForm;
        //return new ModelAndView(RecapConstants.VIEW_SEARCH_REQUESTS_SECTION, RecapConstants.REQUEST_FORM, requestForm);
    }

    /**
     * To know the request information of an item once the request is placed through the create request UI page.
     *
     * @param requestForm            the request form
     * @param patronBarcodeInRequest the patron barcode in request
     * @param result                 the result
     * @param model                  the model
     * @return the model and view
     */
    @GetMapping("/goToSearchRequest")
    public RequestForm goToSearchRequest(RequestForm requestForm, String patronBarcodeInRequest,HttpServletRequest request) {
        try {
            UserDetailsForm userDetails = getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.REQUEST_PRIVILEGE);
            requestForm.resetPageNumber();
            requestForm.setPatronBarcode(patronBarcodeInRequest);
            setFormValues(requestForm, userDetails);
            requestForm.setStatus("");
            requestForm = searchAndSetResults(requestForm);
            //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            logger.debug(exception.getMessage());
        }
        return requestForm;
        //return new ModelAndView("request :: #requestContentId", RecapConstants.REQUEST_FORM, requestForm);
    }

    /**
     * Get first page results from scsb database and display them as row in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/first")
    public RequestForm searchFirst(@RequestBody RequestForm requestForm) {
        requestForm.setPageNumber(0);
        return setSearch(requestForm);
        //return new ModelAndView(RecapConstants.VIEW_SEARCH_REQUESTS_SECTION, RecapConstants.REQUEST_FORM, requestForm);
    }

    /**
     * Get last page results from scsb database and display them as row in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/last")
    public RequestForm searchLast(@RequestBody RequestForm requestForm) {
        //disableRequestSearchInstitutionDropDown(requestForm);
        requestForm.setPageNumber(requestForm.getTotalPageCount() - 1);
        return searchAndSetResults(requestForm);
       /* model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        return new ModelAndView(RecapConstants.VIEW_SEARCH_REQUESTS_SECTION, RecapConstants.REQUEST_FORM, requestForm);
   */
    }

    /**
     * Get previous page results from scsb database and display them as rows in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/previous")
    public RequestForm searchPrevious(@RequestBody RequestForm requestForm) {
        requestForm.setPageNumber(requestForm.getPageNumber()-1);
        return search(requestForm);
    }

    /**
     * Get next page results from scsb database and display them as rows in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/next")
    public RequestForm searchNext(@RequestBody RequestForm requestForm) {
        requestForm.setPageNumber(requestForm.getPageNumber()+1);
        return search(requestForm);
    }

    /**
     * Based on the selected page size value that many request search results will be displayed in the search request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the model and view
     */
    @PostMapping("/requestPageSizeChange")
    public RequestForm onRequestPageSizeChange(@RequestBody RequestForm requestForm) {
        //disableRequestSearchInstitutionDropDown(requestForm);
        requestForm.setPageNumber(getPageNumberOnPageSizeChange(requestForm));
        return searchAndSetResults(requestForm);
        /*model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        return new ModelAndView(RecapConstants.VIEW_SEARCH_REQUESTS_SECTION, RecapConstants.REQUEST_FORM, requestForm);
    */
    }

    /**
     * Populate default values to the request type and requesting institution drop downs in the create request UI page.
     *
     * @param model   the model
     * @param request the request
     * @return the model and view
     */
    @PostMapping("/loadCreateRequest")
    public RequestForm loadCreateRequest() {
        UserDetailsForm userDetailsForm = new UserDetailsForm(1,true,true,true); //getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.REQUEST_PRIVILEGE);
        RequestForm requestForm = getRequestService().setDefaultsToCreateRequest(userDetailsForm);
        return setRequestAttribute(requestForm);
    }

    /**
     * Retains patron's barcode , patron email address and requesting institution values in the create request UI page when request is going to be placed for the same patron.
     *
     * @param model   the model
     * @param request the request
     * @return the model and view
     */
    @PostMapping("/loadCreateRequestForSamePatron")
    public RequestForm loadCreateRequestForSamePatron() {
        UserDetailsForm userDetailsForm = new UserDetailsForm(4,false,false,true);//getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.REQUEST_PRIVILEGE);
        RequestForm requestForm = getRequestService().setDefaultsToCreateRequest(userDetailsForm);
        requestForm.setOnChange("true");
        return setRequestAttribute(requestForm);
    }

    /**
     * Populate default values to the status and institution drop downs in search request UI page.
     *
     * @param model   the model
     * @param request the request
     * @return the model and view
     */
    @PostMapping("/loadSearchRequest")
    public RequestForm loadSearchRequest() {
        UserDetailsForm userDetails = new UserDetailsForm(4,false,false,true);//getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.REQUEST_PRIVILEGE);
        RequestForm requestForm = new RequestForm();
        setFormValues(requestForm, userDetails);
        return setRequestAttribute(requestForm);
    }


    /**
     * Based on the given barcode, this method gets the item information from scsb database to display it in the create request UI page.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @param request     the request
     * @return the string
     * @throws JSONException the json exception
     */
    
    @PostMapping("/populateItem")
    public String populateItem(@RequestBody RequestForm requestForm) throws JSONException {
        return requestService.populateItemForRequest(requestForm);
    }
    /**
     * This method passes information about the requesting item to the scsb-circ micro service to place a request in scsb.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @param request     the request
     * @return the model and view
     * @throws JSONException the json exception
     */
    @PostMapping("/createRequest")
    public RequestForm createRequest(@RequestBody RequestForm requestForm) throws JSONException {

        try {
            ///HttpSession session = request.getSession(false);
            String username = "dinakartest";//(String) session.getAttribute(RecapConstants.USER_NAME);
            String stringJson = populateItem(requestForm);
            if (stringJson != null) {
                JSONObject responseJsonObject = new JSONObject(stringJson);
                Object errorMessage = responseJsonObject.has(RecapConstants.ERROR_MESSAGE) ? responseJsonObject.get(RecapConstants.ERROR_MESSAGE) : null;
                Object noPermissionErrorMessage = responseJsonObject.has(RecapConstants.NO_PERMISSION_ERROR_MESSAGE) ? responseJsonObject.get(RecapConstants.NO_PERMISSION_ERROR_MESSAGE) : null;
                Object itemTitle = responseJsonObject.has(RecapConstants.REQUESTED_ITEM_TITLE) ? responseJsonObject.get(RecapConstants.REQUESTED_ITEM_TITLE) : null;
                Object itemOwningInstitution = responseJsonObject.has(RecapConstants.REQUESTED_ITEM_OWNING_INSTITUTION) ? responseJsonObject.get(RecapConstants.REQUESTED_ITEM_OWNING_INSTITUTION) : null;
                Object deliveryLocations = responseJsonObject.has(RecapConstants.DELIVERY_LOCATION) ? responseJsonObject.get(RecapConstants.DELIVERY_LOCATION) : null;
                Object requestTypes = responseJsonObject.has(RecapConstants.REQUEST_TYPES) ? responseJsonObject.get(RecapConstants.REQUEST_TYPES) : null;
                List<CustomerCodeEntity> customerCodeEntities = new ArrayList<>();
                List<String> requestTypeList = new ArrayList<>();
                if (itemTitle != null && itemOwningInstitution != null && deliveryLocations != null) {
                    requestForm.setItemTitle((String) itemTitle);
                    requestForm.setItemOwningInstitution((String) itemOwningInstitution);
                    JSONObject deliveryLocationsJson = (JSONObject) deliveryLocations;
                    Iterator iterator = deliveryLocationsJson.keys();
                    while (iterator.hasNext()) {
                        String customerCode = (String) iterator.next();
                        String description = (String) deliveryLocationsJson.get(customerCode);
                        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
                        customerCodeEntity.setCustomerCode(customerCode);
                        customerCodeEntity.setDescription(description);
                        customerCodeEntities.add(customerCodeEntity);
                    }
                    requestForm.setDeliveryLocations(customerCodeEntities);
                }
                if (!(RecapCommonConstants.RECALL.equals(requestForm.getRequestType())) && requestTypes != null) {
                    JSONArray requestTypeArray = (JSONArray) requestTypes;
                    for (int i = 0; i < requestTypeArray.length(); i++) {
                        requestTypeList.add(requestTypeArray.getString(i));
                    }
                    requestForm.setRequestTypes(requestTypeList);
                }
                if (noPermissionErrorMessage != null) {
                    requestForm.setErrorMessage((String) noPermissionErrorMessage);
                    requestForm.setShowRequestErrorMsg(true);
                    return requestForm;
                    //return new ModelAndView(RecapConstants.CREATE_REQUEST_SECTION, RecapConstants.REQUEST_FORM, requestForm);
                } else if (errorMessage != null) {
                    requestForm.setErrorMessage((String) errorMessage);
                    requestForm.setShowRequestErrorMsg(true);
                    return requestForm;
                   // return new ModelAndView(RecapConstants.CREATE_REQUEST_SECTION, RecapConstants.REQUEST_FORM, requestForm);
                }
            }

            String requestItemUrl = getScsbUrl() + RecapConstants.REQUEST_ITEM_URL;

            ItemRequestInformation itemRequestInformation = getItemRequestInformation();
            itemRequestInformation.setUsername(username);
            itemRequestInformation.setItemBarcodes(Arrays.asList(requestForm.getItemBarcodeInRequest().split(",")));
            itemRequestInformation.setPatronBarcode(requestForm.getPatronBarcodeInRequest());
            itemRequestInformation.setRequestingInstitution(requestForm.getRequestingInstitution());
            itemRequestInformation.setEmailAddress(requestForm.getPatronEmailAddress());
            itemRequestInformation.setTitle(requestForm.getItemTitle());
            itemRequestInformation.setTitleIdentifier(requestForm.getItemTitle());
            itemRequestInformation.setItemOwningInstitution(requestForm.getItemOwningInstitution());
            itemRequestInformation.setRequestType(requestForm.getRequestType());
            itemRequestInformation.setRequestNotes(requestForm.getRequestNotes());
            itemRequestInformation.setStartPage(requestForm.getStartPage());
            itemRequestInformation.setEndPage(requestForm.getEndPage());
            itemRequestInformation.setAuthor(requestForm.getArticleAuthor());
            itemRequestInformation.setChapterTitle(requestForm.getArticleTitle());
            itemRequestInformation.setIssue(requestForm.getIssue());
            if (requestForm.getVolumeNumber() != null) {
                itemRequestInformation.setVolume(requestForm.getVolumeNumber());
            } else {
                itemRequestInformation.setVolume("");
            }

            if (StringUtils.isNotBlank(requestForm.getDeliveryLocationInRequest())) {
                CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(requestForm.getDeliveryLocationInRequest());
                if (null != customerCodeEntity) {
                    itemRequestInformation.setDeliveryLocation(customerCodeEntity.getCustomerCode());
                }
            }

            HttpEntity<ItemRequestInformation> requestEntity = new HttpEntity<>(itemRequestInformation, getRestHeaderService().getHttpHeaders());
            ResponseEntity<ItemResponseInformation> itemResponseEntity = getRestTemplate().exchange(requestItemUrl, HttpMethod.POST, requestEntity, ItemResponseInformation.class);
            ItemResponseInformation itemResponseInformation = itemResponseEntity.getBody();
            if (null != itemResponseInformation && !itemResponseInformation.isSuccess()) {
                requestForm.setErrorMessage(itemResponseInformation.getScreenMessage());
                requestForm.setDisableRequestingInstitution(false);
                requestForm.setShowRequestErrorMsg(true);
            }
        } catch (HttpClientErrorException httpException) {
            logger.error(RecapCommonConstants.LOG_ERROR, httpException);
            String responseBodyAsString = httpException.getResponseBodyAsString();
            requestForm.setErrorMessage(responseBodyAsString);
            requestForm.setShowRequestErrorMsg(true);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            requestForm.setErrorMessage(exception.getMessage());
            requestForm.setShowRequestErrorMsg(true);
        }

        requestForm.setRequestingInstitutions(requestForm.getInstitutionList());
        if (requestForm.getInstitutionList().size() == 1) {
            requestForm.setDisableRequestingInstitution(true);
        }
        if (requestForm.getErrorMessage() == null) {
            requestForm.setSubmitted(true);
            requestForm.setDisableRequestingInstitution(true);
        }
        return requestForm;
        //return new ModelAndView(RecapConstants.CREATE_REQUEST_SECTION, RecapConstants.REQUEST_FORM, requestForm);
    }

    /**
     * Cancel the request which is placed in scsb.
     *
     * @param requestForm the request form
     * @param result      the result
     * @param model       the model
     * @return the string
     */
    @PostMapping("/cancelRequest")
    public String cancelRequest(@RequestBody RequestForm requestForm) {
        JSONObject jsonObject = new JSONObject();
        String requestStatus = null;
        String requestNotes = null;
        try {
            HttpEntity requestEntity = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbUrl() + RecapConstants.URL_REQUEST_CANCEL).queryParam(RecapCommonConstants.REQUEST_ID, requestForm.getRequestId());
            HttpEntity<CancelRequestResponse> responseEntity = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.POST, requestEntity, CancelRequestResponse.class);
            CancelRequestResponse cancelRequestResponse = responseEntity.getBody();
            jsonObject.put(RecapCommonConstants.MESSAGE, cancelRequestResponse.getScreenMessage());
            jsonObject.put(RecapCommonConstants.STATUS, cancelRequestResponse.isSuccess());
            Optional<RequestItemEntity> requestItemEntity = requestItemDetailsRepository.findById(requestForm.getRequestId());
            if (requestItemEntity.isPresent()) {
                requestStatus = requestItemEntity.get().getRequestStatusEntity().getRequestStatusDescription();
                requestNotes = requestItemEntity.get().getNotes();
            }
            jsonObject.put(RecapCommonConstants.REQUEST_STATUS, requestStatus);
            jsonObject.put(RecapConstants.REQUEST_NOTES, requestNotes);
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            logger.debug(exception.getMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Resubmit the exception request. Creates a new request with the same data.
     */
    @PostMapping("/resubmitRequest")
    public String resubmitRequest(@RequestBody RequestForm requestForm) {
        JSONObject jsonObject = new JSONObject();
        try {
            ReplaceRequest replaceRequest = new ReplaceRequest();
            replaceRequest.setReplaceRequestByType(RecapCommonConstants.REQUEST_IDS);
            replaceRequest.setRequestStatus(RecapConstants.EXCEPTION);
            String requestId = String.valueOf(requestForm.getRequestId());
            replaceRequest.setRequestIds(requestId);
            HttpEntity request = new HttpEntity<>(replaceRequest, getRestHeaderService().getHttpHeaders());
            Map resultMap = getRestTemplate().postForObject(getScsbUrl() + RecapConstants.URL_REQUEST_RESUBMIT, request, Map.class);
            jsonObject.put(RecapCommonConstants.BARCODE, requestForm.getItemBarcodeHidden());
            if (resultMap.containsKey(requestId)) {
                String message = (String) resultMap.get(requestId);
                jsonObject.put(RecapCommonConstants.MESSAGE, resultMap.get(requestId));
                jsonObject.put(RecapCommonConstants.STATUS, StringUtils.isNotBlank(message) && message.contains(RecapCommonConstants.SUCCESS));
            } else if (resultMap.containsKey(RecapCommonConstants.INVALID_REQUEST)) {
                jsonObject.put(RecapCommonConstants.MESSAGE, resultMap.get(RecapCommonConstants.INVALID_REQUEST));
                jsonObject.put(RecapCommonConstants.STATUS, false);
            } else if (resultMap.containsKey(RecapCommonConstants.FAILURE)) {
                jsonObject.put(RecapCommonConstants.MESSAGE, resultMap.get(RecapCommonConstants.FAILURE));
                jsonObject.put(RecapCommonConstants.STATUS, false);
            }
        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            logger.debug(exception.getMessage());
        }
        logger.info(jsonObject.toString());
        return jsonObject.toString();
    }

    private RequestForm searchAndSetResults(RequestForm requestForm) {
        Page<RequestItemEntity> requestItemEntities = getRequestServiceUtil().searchRequests(requestForm);
        List<SearchResultRow> searchResultRows = buildSearchResultRows(requestItemEntities.getContent(), requestForm);
        if (CollectionUtils.isNotEmpty(searchResultRows)) {
            requestForm.setSearchResultRows(searchResultRows);
            requestForm.setTotalRecordsCount(NumberFormat.getNumberInstance().format(requestItemEntities.getTotalElements()));
            requestForm.setTotalPageCount(requestItemEntities.getTotalPages());
        } else {
            requestForm.setSearchResultRows(Collections.emptyList());
            requestForm.setMessage(RecapCommonConstants.SEARCH_RESULT_ERROR_NO_RECORDS_FOUND);
        }
        requestForm.setShowResults(true);
        return requestForm;
    }

    private List<SearchResultRow> buildSearchResultRows(List<RequestItemEntity> requestItemEntities, RequestForm requestForm) {
        if (CollectionUtils.isNotEmpty(requestItemEntities)) {
            List<SearchResultRow> searchResultRows = new ArrayList<>();
            for (RequestItemEntity requestItemEntity : requestItemEntities) {
                ItemEntity itemEntity = requestItemEntity.getItemEntity();
                try {
                    if (requestForm.getInstitutionList().size() == 1 && (itemEntity.getInstitutionEntity().getInstitutionCode().equalsIgnoreCase(requestForm.getInstitution()) && !itemEntity.getOwningInstitutionId().equals(requestItemEntity.getRequestingInstitutionId()))) {
                        populateRequestResultsForRecall(searchResultRows, requestItemEntity);
                    } else {
                        populateRequestResults(searchResultRows, requestItemEntity);
                    }
                } catch (Exception e) {
                    logger.error(RecapCommonConstants.LOG_ERROR, e);
                }
            }
            return searchResultRows;
        }
        return Collections.emptyList();
    }

    private void populateRequestResultsForRecall(List<SearchResultRow> searchResultRows, RequestItemEntity requestItemEntity) {
        SearchResultRow searchResultRow = setSearchResultRow(requestItemEntity);
        searchResultRow.setShowItems(false);
        setBibData(requestItemEntity, searchResultRow, searchResultRows);
    }

    private void populateRequestResults(List<SearchResultRow> searchResultRows, RequestItemEntity requestItemEntity) {
        SearchResultRow searchResultRow = setSearchResultRow(requestItemEntity);
        searchResultRow.setShowItems(true);
        searchResultRow.setRequestCreatedBy(requestItemEntity.getCreatedBy());
        if (StringUtils.isNotBlank(requestItemEntity.getEmailId())) {
            searchResultRow.setPatronEmailId(securityUtil.getDecryptedValue(requestItemEntity.getEmailId()));
        } else {
            searchResultRow.setPatronEmailId(requestItemEntity.getEmailId());
        }
        searchResultRow.setPatronBarcode(requestItemEntity.getPatronId());
        searchResultRow.setDeliveryLocation(requestItemEntity.getStopCode());
        setBibData(requestItemEntity, searchResultRow, searchResultRows);
    }

    private Integer getPageNumberOnPageSizeChange(RequestForm requestForm) {
        int totalRecordsCount;
        Integer pageNumber = requestForm.getPageNumber();
        try {
            totalRecordsCount = NumberFormat.getNumberInstance().parse(requestForm.getTotalRecordsCount()).intValue();
            int totalPagesCount = (int) Math.ceil((double) totalRecordsCount / (double) requestForm.getPageSize());
            if (totalPagesCount > 0 && pageNumber >= totalPagesCount) {
                pageNumber = totalPagesCount - 1;
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
        return pageNumber;
    }

    /**
     * Gets item request information.
     *
     * @return the item request information
     */
    public ItemRequestInformation getItemRequestInformation() {
        return new ItemRequestInformation();
    }

    /**
     * To change the status information of requested item asynchronously in the search request UI page.
     *
     * @param request the request
     * @return the string
     */
    @ResponseBody
    @GetMapping(value = "/request/refreshStatus")
    public String refreshStatus(HttpServletRequest request) {
        return requestService.getRefreshedStatus(request);
    }

    private void setFormValuesToDisableSearchInstitution(@Valid @ModelAttribute("requestForm") RequestForm requestForm, UserDetailsForm userDetails, List<String> institutionList) {
        Optional<InstitutionEntity> institutionEntity = getInstitutionDetailsRepository().findById(userDetails.getLoginInstitutionId());
        if (userDetails.isSuperAdmin() || userDetails.isRecapUser() || ((institutionEntity.isPresent()) && (institutionEntity.get().getInstitutionCode().equalsIgnoreCase("HTC")))) {
            getRequestService().getInstitutionForSuperAdmin(institutionList);
            requestForm.setInstitutionList(institutionList);
        } else {
            requestForm.setDisableSearchInstitution(true);
            if (institutionEntity.isPresent()) {
                requestForm.setInstitutionList(Arrays.asList(institutionEntity.get().getInstitutionCode()));
                requestForm.setInstitution(institutionEntity.get().getInstitutionCode());
                requestForm.setSearchInstitutionHdn(institutionEntity.get().getInstitutionCode());
            }
        }
    }

    private RequestForm search(RequestForm requestForm) {
        return searchAndSetResults(disableRequestSearchInstitutionDropDown(requestForm));
       /* model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        return new ModelAndView(RecapConstants.VIEW_SEARCH_REQUESTS_SECTION, RecapConstants.REQUEST_FORM, requestForm);
    */
    }

    private void setBibData(RequestItemEntity requestItemEntity, SearchResultRow searchResultRow, List<SearchResultRow> searchResultRows) {
        ItemEntity itemEntity = requestItemEntity.getItemEntity();
        if (null != itemEntity && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
            searchResultRow.setBibId(itemEntity.getBibliographicEntities().get(0).getBibliographicId());
        }
        searchResultRows.add(searchResultRow);
    }

    private RequestForm setRequestAttribute(RequestForm requestForm) {
       /* model.addAttribute(RecapConstants.REQUEST_FORM, requestForm);
        model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        return new ModelAndView(RecapCommonConstants.REQUEST, RecapConstants.REQUEST_FORM, requestForm);
    */
        return requestForm;
    }

    private void setFormValues(RequestForm requestForm, UserDetailsForm userDetails) {
        List<String> requestStatuses = new ArrayList<>();
        List<String> institutionList = new ArrayList<>();
        getRequestService().findAllRequestStatusExceptProcessing(requestStatuses);
        requestForm.setRequestStatuses(requestStatuses);
        setFormValuesToDisableSearchInstitution(requestForm, userDetails, institutionList);

    }

    private RequestForm setSearch(RequestForm requestForm) {
        //requestForm = disableRequestSearchInstitutionDropDown(requestForm);
        requestForm.setPageNumber(0);
        return searchAndSetResults(requestForm);
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
    }
}



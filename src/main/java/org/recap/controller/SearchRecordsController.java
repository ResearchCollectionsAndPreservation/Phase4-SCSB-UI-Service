package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchRecordsResponse;
import org.recap.model.search.SearchResultRow;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.CsvUtil;
import org.recap.util.HelperUtil;
import org.recap.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rajeshbabuk on 6/7/16.
 */

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SearchRecordsController extends RecapController {

    private static final Logger logger = LoggerFactory.getLogger(SearchRecordsController.class);
    @Autowired
    private SearchUtil searchUtil;
    @Autowired
    private CsvUtil csvUtil;
    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * Gets search util.
     *
     * @return the search util
     */
    public SearchUtil getSearchUtil() {
        return searchUtil;
    }

    /**
     * Gets institution details repository.
     *
     * @return the institution details repository
     */
    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    /**
     * Render the search UI page for the scsb application.
     *
     * @param model   the model
     * @param request the request
     * @return the string
     */
    @GetMapping("/search")
    public String searchRecords(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL);
        if (authenticated) {
            SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
            model.addAttribute(RecapConstants.VIEW_SEARCH_RECORDS_REQUEST, searchRecordsRequest);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.SEARCH);
            return RecapConstants.VIEW_SEARCH_RECORDS;
        } else {
            return UserManagementService.unAuthorizedUser(session, "Search", logger);
        }

    }

    /**
     * Performs search on solr and returns the results as rows to get displayed in the search UI page.
     *
     * @return the model and view
     */
    @PostMapping("/search")
    public SearchRecordsResponse search(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        logger.info("search records calling with payload:",searchRecordsRequest);
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);
        return searchRecordsResponse;
    }

    /**
     * Performs search on solr and returns the previous page results as rows to get displayed in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @return the model and view
     */
    @PostMapping("/previous")
    public SearchRecordsResponse searchPrevious(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.setPageNumber(setPageNumber(searchRecordsRequest));
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);
        searchRecordsResponse.setPageNumber(searchRecordsRequest.getPageNumber());
        return searchRecordsResponse;
    }

    private Integer setPageNumber(SearchRecordsRequest searchRecordsRequest) {
        if (searchRecordsRequest.getPageNumber() > 0)
            return searchRecordsRequest.getPageNumber() - 1;
        else
            return 0;
    }

    /**
     * Performs search on solr and returns the next page results as rows to get displayed in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @param result               the result
     * @param model                the model
     * @return the model and view
     */
    @PostMapping("/next")
    public SearchRecordsResponse searchNext(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.setPageNumber(searchRecordsRequest.getPageNumber()+1);
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);
        searchRecordsResponse.setPageNumber(searchRecordsRequest.getPageNumber());
        return searchRecordsResponse;
    }

    /**
     * Performs search on solr and returns the first page results as rows to get displayed in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @param result               the result
     * @param model                the model
     * @return the model and view
     */
    @PostMapping("/first")
    public SearchRecordsResponse searchFirst(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.setPageNumber(0);
        return searchUtil.searchRecord(searchRecordsRequest);
    }

    /**
     * Performs search on solr and returns the last page results as rows to get displayed in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @param result               the result
     * @param model                the model
     * @return the model and view
     */
    @PostMapping("/last")
    public SearchRecordsResponse searchLast(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.setPageNumber(searchRecordsRequest.getTotalPageCount()-1);
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);
        searchRecordsResponse.setPageNumber(searchRecordsRequest.getPageNumber());
        return searchRecordsResponse;
    }

    @PostMapping("/clear")
    public SearchRecordsRequest clear(SearchRecordsRequest searchRecordsRequest) {

        searchRecordsRequest.setFieldValue("");
        searchRecordsRequest.setOwningInstitutions(new ArrayList<>());
        searchRecordsRequest.setCollectionGroupDesignations(new ArrayList<>());
        searchRecordsRequest.setAvailability(new ArrayList<>());
        searchRecordsRequest.setMaterialTypes(new ArrayList<>());
        searchRecordsRequest.setUseRestrictions(new ArrayList<>());
        searchRecordsRequest.setShowResults(false);
        return searchRecordsRequest;
    }

    /**
     * Clear all the input fields and the search result rows in the search UI page.
     *
     * @return the model and view
     */
    @PostMapping("/newSearch")
    public SearchRecordsRequest newSearch() {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        return searchRecordsRequest;
    }

    /**
     * This method redirects to request UI page with the selected items information in the search results.
     *
     * @param searchRecordsRequest the search records request
     * @param result               the result
     * @param model                the model
     * @param request              the request
     * @param redirectAttributes   the redirect attributes
     * @return the model and view
     */
    @PostMapping("/request")
    public ModelAndView requestRecords(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                       BindingResult result,
                                       Model model,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails( RecapConstants.REQUEST_PRIVILEGE);
        processRequest(searchRecordsRequest, userDetailsForm, redirectAttributes);
        if (StringUtils.isNotBlank(searchRecordsRequest.getErrorMessage())) {
            searchRecordsRequest.setShowResults(true);
            model.addAttribute("searchRecordsRequest", searchRecordsRequest);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.SEARCH);
            return new ModelAndView("searchRecords");
        }
        model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REQUEST);
        return new ModelAndView(new RedirectView("/request", true));
    }

    /**
     * Selected items in the search UI page will be exported to a csv file.
     *
     * @param searchRecordsRequest the search records request
     * @return the byte [ ]
     * @throws Exception the exception
     */
    @PostMapping("/export")
    public byte[] exportRecords(@RequestBody SearchRecordsRequest searchRecordsRequest) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileNameWithExtension = "ExportRecords_" + dateFormat.format(new Date()) + ".csv";
        File csvFile = csvUtil.writeSearchResultsToCsv(searchRecordsRequest.getSearchResultRows(), fileNameWithExtension);
        return HelperUtil.getFileContent(csvFile, fileNameWithExtension, RecapCommonConstants.SEARCH);
    }

    /**
     * Performs search on solr on changing the page size. The number of results returned will be equal to the selected page size.
     *
     * @param searchRecordsRequest the search records request
     */

    @PostMapping("/pageChanges")
    public SearchRecordsResponse onPageSizeChange(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        logger.info("showEntries size changed calling with size:",searchRecordsRequest.getPageSize());
        Integer pageNumber = searchRecordsRequest.getPageNumber();
        searchRecordsRequest.setPageNumber(0);
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);
        if (searchRecordsResponse.getTotalPageCount() > 0 && pageNumber >= searchRecordsResponse.getTotalPageCount()) {
            pageNumber = searchRecordsResponse.getTotalPageCount() - 1;
        }
        searchRecordsRequest.setPageNumber(pageNumber);
        SearchRecordsResponse searchRecordsResponseNew = searchRecordsPage(searchRecordsRequest);
        searchRecordsResponseNew.setPageNumber(pageNumber);
        return searchRecordsResponseNew;
    }

    /**
     * To get the page number based on the total number of records in result set and the selected page size.
     *
     * @param searchRecordsRequest the search records request
     * @return the integer
     */
    public Integer getPageNumberOnPageSizeChange(SearchRecordsRequest searchRecordsRequest, SearchRecordsResponse searchRecordsResponse) {
        int totalRecordsCount;
        Integer pageNumber = searchRecordsRequest.getPageNumber();
        try {
           /* if (isEmptyField(searchRecordsRequest)) {
                totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalRecordsCount()).intValue();
            } else if (isItemField(searchRecordsRequest)) {
                totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalItemRecordsCount()).intValue();
            } else {*/
            totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsResponse.getTotalRecordsCount()).intValue();
            // }
            int totalPagesCount = (int) Math.ceil((double) totalRecordsCount / (double) searchRecordsRequest.getPageSize());
            if (totalPagesCount > 0 && pageNumber >= totalPagesCount) {
                pageNumber = totalPagesCount - 1;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return pageNumber;
    }

    private boolean isEmptyField(SearchRecordsRequest searchRecordsRequest) {
        return StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue());
    }

    private boolean isItemField(SearchRecordsRequest searchRecordsRequest) {
        return (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && (searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapCommonConstants.BARCODE) ||
                searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapCommonConstants.CALL_NUMBER)));
    }

    private void processRequest(SearchRecordsRequest searchRecordsRequest, UserDetailsForm userDetailsForm, RedirectAttributes redirectAttributes) {
        String userInstitution = null;
        Optional<InstitutionEntity> institutionEntity = getInstitutionDetailsRepository().findById(userDetailsForm.getLoginInstitutionId());


        if (institutionEntity.isPresent()) {
            userInstitution = institutionEntity.get().getInstitutionCode();
        }
        List<SearchResultRow> searchResultRows = searchRecordsRequest.getSearchResultRows();
        Set<String> barcodes = new HashSet<>();
        Set<String> itemTitles = new HashSet<>();
        Set<String> itemOwningInstitutions = new HashSet<>();
        Set<String> itemAvailability = new HashSet<>();
        for (SearchResultRow searchResultRow : searchResultRows) {
            if (searchResultRow.isSelected()) {
                if (RecapCommonConstants.PRIVATE.equals(searchResultRow.getCollectionGroupDesignation()) && !userDetailsForm.isSuperAdmin() && !userDetailsForm.isRecapUser() && StringUtils.isNotBlank(userInstitution) && !userInstitution.equals(searchResultRow.getOwningInstitution())) {
                    searchRecordsRequest.setErrorMessage(RecapConstants.REQUEST_PRIVATE_ERROR_USER_NOT_PERMITTED);
                } else if (!userDetailsForm.isRecapPermissionAllowed()) {
                    searchRecordsRequest.setErrorMessage(RecapConstants.REQUEST_ERROR_USER_NOT_PERMITTED);
                } else {
                    processBarcodesForSearchResultRow(barcodes, itemTitles, itemOwningInstitutions, searchResultRow, itemAvailability);
                }
            } else if (!CollectionUtils.isEmpty(searchResultRow.getSearchItemResultRows())) {
                for (SearchItemResultRow searchItemResultRow : searchResultRow.getSearchItemResultRows()) {
                    if (searchItemResultRow.isSelectedItem()) {
                        if (RecapCommonConstants.PRIVATE.equals(searchItemResultRow.getCollectionGroupDesignation()) && !userDetailsForm.isSuperAdmin() && !userDetailsForm.isRecapUser() && StringUtils.isNotBlank(userInstitution) && !userInstitution.equals(searchResultRow.getOwningInstitution())) {
                            searchRecordsRequest.setErrorMessage(RecapConstants.REQUEST_PRIVATE_ERROR_USER_NOT_PERMITTED);
                        } else if (!userDetailsForm.isRecapPermissionAllowed()) {
                            searchRecordsRequest.setErrorMessage(RecapConstants.REQUEST_ERROR_USER_NOT_PERMITTED);
                        } else {
                            processBarcodeForSearchItemResultRow(barcodes, itemTitles, itemOwningInstitutions, searchItemResultRow, searchResultRow, itemAvailability);
                        }
                    }
                }
            }
        }
        redirectAttributes.addFlashAttribute(RecapConstants.REQUESTED_BARCODE, StringUtils.join(barcodes, ","));
        redirectAttributes.addFlashAttribute(RecapConstants.REQUESTED_ITEM_TITLE, StringUtils.join(itemTitles, " || "));
        redirectAttributes.addFlashAttribute(RecapConstants.REQUESTED_ITEM_OWNING_INSTITUTION, StringUtils.join(itemOwningInstitutions, ","));
        redirectAttributes.addFlashAttribute(RecapConstants.REQUESTED_ITEM_AVAILABILITY, itemAvailability);
    }

    private void processBarcodeForSearchItemResultRow(Set<String> barcodes, Set<String> titles, Set<String> itemInstitutions, SearchItemResultRow searchItemResultRow, SearchResultRow searchResultRow, Set<String> itemAvailabilty) {
        String barcode = searchItemResultRow.getBarcode();
        processTitleAndItemInstitution(barcodes, titles, itemInstitutions, searchResultRow, barcode, itemAvailabilty);
    }

    private void processBarcodesForSearchResultRow(Set<String> barcodes, Set<String> titles, Set<String> itemInstitutions, SearchResultRow searchResultRow, Set<String> itemAvailabilty) {
        String barcode = searchResultRow.getBarcode();
        processTitleAndItemInstitution(barcodes, titles, itemInstitutions, searchResultRow, barcode, itemAvailabilty);
    }

    private void processTitleAndItemInstitution(Set<String> barcodes, Set<String> titles, Set<String> itemInstitutions, SearchResultRow searchResultRow, String barcode, Set<String> itemAvailabilty) {
        String title = searchResultRow.getTitle();
        String owningInstitution = searchResultRow.getOwningInstitution();
        itemAvailabilty.add(searchResultRow.getAvailability());
        if (StringUtils.isNotBlank(barcode)) {
            barcodes.add(barcode);
        }
        if (StringUtils.isNotBlank(title)) {
            titles.add(title);
        }
        if (StringUtils.isNotBlank(owningInstitution)) {
            itemInstitutions.add(owningInstitution);
        }
    }

    /**
     * Add up for srring thymeleaf and spring binding.
     *
     * @param binder the binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(1048576);
    }

    private SearchRecordsResponse searchRecordsPage(SearchRecordsRequest searchRecordsRequest) {
        return searchUtil.searchAndSetResults(searchRecordsRequest);
        /*model.addAttribute( RecapCommonConstants.TEMPLATE,  RecapCommonConstants.SEARCH);
        return new ModelAndView(RecapConstants.VIEW_SEARCH_RECORDS, RecapConstants.VIEW_SEARCH_RECORDS_REQUEST, searchRecordsRequest);*/
    }
}

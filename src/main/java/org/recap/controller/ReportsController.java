package org.recap.controller;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.request.DownloadReports;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.HelperUtil;
import org.recap.util.ReportsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/10/16.
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin
public class ReportsController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    private ReportsUtil reportsUtil;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * Gets reports util.
     *
     * @return the reports util
     */
    public ReportsUtil getReportsUtil() {
        return reportsUtil;
    }

    /**
     * Render the reports UI page for the scsb application.
     *
     * @param model   the model
     * @param request the request
     * @return the string
     */
    @GetMapping("/reports")
    public String reports(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_REPORT_URL);
        if (authenticated) {
            ReportsForm reportsForm = new ReportsForm();
            model.addAttribute(RecapConstants.REPORTS_FORM, reportsForm);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REPORTS);
            return RecapConstants.VIEW_SEARCH_RECORDS;
        } else {
            return UserManagementService.unAuthorizedUser(session, "Reports", logger);
        }

    }

    /**
     * Get the item count for requested, accessioned and deaccessioned report.
     *
     * @param reportsForm the reports form
     *                    //* @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/submit")
    public ReportsForm reportCounts(@RequestBody ReportsForm reportsForm) throws Exception {
        if (reportsForm.getRequestType().equalsIgnoreCase(RecapCommonConstants.REPORTS_REQUEST)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapCommonConstants.SIMPLE_DATE_FORMAT_REPORTS);
            Date requestFromDate = simpleDateFormat.parse(reportsForm.getRequestFromDate());
            Date requestToDate = simpleDateFormat.parse(reportsForm.getRequestToDate());
            Date fromDate = getFromDate(requestFromDate);
            Date toDate = getToDate(requestToDate);
            if (reportsForm.getShowBy().equalsIgnoreCase(RecapCommonConstants.REPORTS_PARTNERS)) {
                reportsForm = reportsUtil.populatePartnersCountForRequest(reportsForm, fromDate, toDate);
            } else if (reportsForm.getShowBy().equalsIgnoreCase(RecapCommonConstants.REPORTS_REQUEST_TYPE)) {
                reportsForm = reportsUtil.populateRequestTypeInformation(reportsForm, fromDate, toDate);
            }
        } else if (reportsForm.getRequestType().equalsIgnoreCase(RecapCommonConstants.REPORTS_ACCESSION_DEACCESSION)) {
            reportsForm = reportsUtil.populateAccessionDeaccessionItemCounts(reportsForm);

        } else if ("CollectionGroupDesignation".equalsIgnoreCase(reportsForm.getRequestType())) {
            reportsForm = reportsUtil.populateCGDItemCounts(reportsForm);
        }
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REPORTS);
        //return new ModelAndView("reports", "reportsForm", reportsForm);
        return reportsForm;
    }

    /**
     * Get the item count for collection group designation report.
     *
     * @return the model and view
     * @throws Exception the exception
     */
    @GetMapping("/collectionGroupDesignation")
    public ReportsForm cgdCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        return reportsUtil.populateCGDItemCounts(reportsForm);
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REPORTS);
        //return new ModelAndView(RecapConstants.REPORTS_VIEW_CGD_TABLE, RecapConstants.REPORTS_FORM, reportsForm);

    }

    /**
     * Get deaccessioned item results from scsb solr and display them as rows in the deaccession report UI page.
     *
     * @param reportsForm the reports form
     *                    //* @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/deaccessionInformation")
    public ReportsForm deaccessionInformation(@RequestBody ReportsForm reportsForm) throws Exception {
        return daccessionItemResults(reportsForm);
    }


    /**
     * Get first page deaccessioned or incomplete item results from scsb solr and display them as rows in the deaccession report or incomplete report.
     *
     * @param reportsForm the reports form
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/first")
    public ReportsForm searchFirst(@RequestBody ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            reportsForm.setIncompletePageNumber(0);
            return getIncompleteRecords(reportsForm);
        } else {
            reportsForm.setPageNumber(0);
            return setReportData(reportsForm);
        }
    }

    /**
     * Get previous page deaccessioned or incomplete item results from scsb solr and display them as rows in the deaccession report or incomplete report.
     *
     * @param reportsForm the reports form
     *                    //* @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/previous")
    public ReportsForm searchPrevious(@RequestBody ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            reportsForm.setIncompletePageNumber(reportsForm.getIncompletePageNumber() - 1);
            return getIncompleteRecords(reportsForm);
        } else {
            reportsForm.setPageNumber(reportsForm.getPageNumber() - 1);
            return setReportData(reportsForm);
        }
    }


    /**
     * Get next page deaccessioned or incomplete item results from scsb solr and display them as rows in the deaccession report or incomplete report.
     *
     * @param reportsForm the reports form
     *                    //* @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/next")
    public ReportsForm searchNext(@RequestBody ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            reportsForm.setIncompletePageNumber(reportsForm.getIncompletePageNumber() + 1);
            return getIncompleteRecords(reportsForm);
        } else {
            reportsForm.setPageNumber(reportsForm.getPageNumber() + 1);
            return setReportData(reportsForm);
        }
    }


    /**
     * Get last page deaccessioned or incomplete item results from scsb solr and display them as rows in the deaccession report or incomplete report.
     *
     * @param reportsForm the reports form
     *                    // * @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/last")
    public ReportsForm searchLast(@RequestBody ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            reportsForm.setIncompletePageNumber(reportsForm.getIncompleteTotalPageCount() - 1);
            return getIncompleteRecords(reportsForm);
        } else {
            reportsForm.setPageNumber(reportsForm.getTotalPageCount() - 1);
            return setReportData(reportsForm);
        }
    }

    /**
     * Get incomplete item results from scsb solr and display them as rows in the incomplete report UI page.
     *
     * @param reportsForm the reports form
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/incompleteRecords")
    public ReportsForm incompleteRecordsReport(@RequestBody ReportsForm reportsForm) throws Exception {
        reportsForm.setIncompletePageNumber(0);
        return getIncompleteRecords(reportsForm);

    }

    /**
     * To generate institution drop down values in the incomplete report UI page.
     *
     * @return the institution for incomplete report
     */
    @GetMapping("/getInstitutions")
    public ReportsForm getInstitutionForIncompleteReport() {
        ReportsForm reportsForm = new ReportsForm();
        List<String> instList = new ArrayList<>();
        List<InstitutionEntity> institutionCodeForSuperAdmin = institutionDetailsRepository.getInstitutionCodeForSuperAdmin();
        for (InstitutionEntity institutionEntity : institutionCodeForSuperAdmin) {
            instList.add(institutionEntity.getInstitutionCode());
        }
        reportsForm.setIncompleteShowByInst(instList);
        return reportsForm;
        //return new ModelAndView(RecapConstants.REPORTS_INCOMPLETE_SHOW_BY_VIEW, RecapConstants.REPORTS_FORM, reportsForm);
    }


    /**
     * To export the incomplete report results to a csv file.
     *
     * @param reportsForm the reports form
     * @return the byte [ ]
     * @throws Exception the exception
     */
    @PostMapping("/export")
    public DownloadReports exportIncompleteRecords(@RequestBody ReportsForm reportsForm) throws Exception {
        DownloadReports downloadReports = new DownloadReports();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileNameWithExtension = RecapConstants.REPORTS_INCOMPLETE_EXPORT_FILE_NAME + reportsForm.getIncompleteRequestingInstitution() + "_" + dateFormat.format(new Date()) + ".csv";
        reportsForm.setExport(true);
        List<IncompleteReportResultsRow> incompleteReportResultsRows = reportsUtil.incompleteRecordsReportFieldsInformation(reportsForm);
        IncompleteReportResultsRow incompleteReportResultsRow = new IncompleteReportResultsRow();
        incompleteReportResultsRows.add(incompleteReportResultsRow);
        File csvFile = reportsUtil.exportIncompleteRecords(incompleteReportResultsRows, fileNameWithExtension);
        byte[] fileContent = HelperUtil.getFileContent(csvFile, fileNameWithExtension, RecapCommonConstants.REPORTS);
        //byte[] fileContent = IOUtils.toByteArray(new FileInputStream(csvFile));
        downloadReports.setContent(fileContent);
        downloadReports.setFileName(fileNameWithExtension);
        return downloadReports;
    }

    /**
     * Based on the selected page size search results will display the results in the incomplete report UI page.
     *
     * @param reportsForm the reports form
     *                    //* @param model       the model
     * @return the model and view
     * @throws Exception the exception
     */
    @PostMapping("/incompleteReportPageSizeChange")
    public ReportsForm incompleteReportPageSizeChange(@RequestBody ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            reportsForm.setIncompletePageNumber(0);
            return getIncompleteRecords(reportsForm);
        } else {
            reportsForm.setPageNumber(0);
            return setReportData(reportsForm);
        }
    }

    private ReportsForm getIncompleteRecords(ReportsForm reportsForm) throws Exception {
        List<IncompleteReportResultsRow> incompleteReportResultsRows = getReportsUtil().incompleteRecordsReportFieldsInformation(reportsForm);
        if (incompleteReportResultsRows.isEmpty()) {
            reportsForm.setShowIncompleteResults(false);
            reportsForm.setErrorMessage(RecapConstants.REPORTS_INCOMPLETE_RECORDS_NOT_FOUND);
        } else {
            reportsForm.setShowIncompleteResults(true);
            reportsForm.setShowIncompletePagination(true);
            reportsForm.setIncompleteReportResultsRows(incompleteReportResultsRows);
        }
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapCommonConstants.REPORTS);
        //return new ModelAndView(RecapConstants.REPORTS_INCOMPLETE_RECORDS_VIEW, RecapConstants.REPORTS_FORM, reportsForm);
        return reportsForm;
    }

    /**
     * For the given date this method will add the start time of the day.
     *
     * @param createdDate the created date
     * @return the from date
     */
    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * For the given date this method will add the end time of the day.
     *
     * @param createdDate the created date
     * @return the to date
     */
    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private ReportsForm search(ReportsForm reportsForm) throws Exception {
        if ((RecapConstants.REPORTS_INCOMPLETE_RECORDS).equals(reportsForm.getRequestType())) {
            return getIncompleteRecords(reportsForm);
        } else {
            return setReportData(reportsForm);
        }
    }

    private ReportsForm setReportData(ReportsForm reportsForm) throws Exception {
        return daccessionItemResults(reportsForm);
    }

    private ReportsForm daccessionItemResults(ReportsForm reportsForm) throws Exception {
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = getReportsUtil().deaccessionReportFieldsInformation(reportsForm);
        reportsForm.setDeaccessionItemResultsRows(deaccessionItemResultsRowList);
        return reportsForm;
    }
}

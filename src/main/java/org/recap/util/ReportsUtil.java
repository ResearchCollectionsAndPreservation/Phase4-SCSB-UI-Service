package org.recap.util;

import com.csvreader.CsvWriter;
import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.model.reports.ReportsInstitutionForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by akulak on 21/12/16.
 */
@Component
public class ReportsUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReportsUtil.class);

    @Autowired
    private ReportsServiceUtil reportsServiceUtil;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * To get the item count for the physical and edd request report from the scsb database and
     * set those values to the reports form to get displayed in the request reports UI page.
     *
     * @param reportsForm     the reports form
     * @param requestFromDate the request from date
     * @param requestToDate   the request to date
     */
    public ReportsForm populatePartnersCountForRequest(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setReportsInstitutionFormList(new ArrayList<>());
        List<InstitutionEntity> institutionEntities = getInstitutionEntities();
        if (!institutionEntities.isEmpty()) {
            for (InstitutionEntity institutionEntity : institutionEntities) {
                ReportsInstitutionForm reportsInstitutionForm = new ReportsInstitutionForm();
                reportsInstitutionForm.setInstitution(institutionEntity.getInstitutionCode());
                reportsInstitutionForm.setPhysicalPrivateCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_PRIVATE), getOtherInstitutionIdsForPartner(institutionEntity.getId()), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED,RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)));
                reportsInstitutionForm.setPhysicalSharedCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), getOtherInstitutionIdsForPartner(institutionEntity.getId()), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED,RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)));
                reportsInstitutionForm.setPhysicalPartnerSharedCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(institutionEntity.getId()) ,Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED,RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)));
                reportsInstitutionForm.setEddPrivateCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_PRIVATE), getOtherInstitutionIdsForPartner(institutionEntity.getId()), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_EDD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.EDD)));
                reportsInstitutionForm.setEddSharedOpenCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), getOtherInstitutionIdsForPartner(institutionEntity.getId()), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_EDD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.EDD)));
                reportsInstitutionForm.setEddPartnerSharedOpenCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, Arrays.asList(institutionEntity.getId()), Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(institutionEntity.getId()) ,Arrays.asList(RecapCommonConstants.REQUEST_STATUS_EDD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.EDD)));
                reportsForm.getReportsInstitutionFormList().add(reportsInstitutionForm);
            }
            reportsForm.setShowPartners(true);
            reportsForm.setShowReportResultsText(true);
            reportsForm.setShowNotePartners(true);
        }
        return reportsForm;
    }


    /**
     *To get the item count for the retrieval, recall and edd request report from the scsb database and
     * set those values to the reports form to get displayed in the request reports UI page.
     * @param reportsForm     the reports form
     * @param requestFromDate the request from date
     * @param requestToDate   the request to date
     */
    public ReportsForm populateRequestTypeInformation(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setReportsInstitutionFormList(new ArrayList<>());
        List<InstitutionEntity> institutionEntities = getInstitutionEntities();
        if (!institutionEntities.isEmpty()) {
            for (InstitutionEntity institutionEntity : institutionEntities) {
                ReportsInstitutionForm reportsInstitutionForm = new ReportsInstitutionForm();
                reportsInstitutionForm.setInstitution(institutionEntity.getInstitutionCode());
                reportsInstitutionForm.setRetrievalRequestCount(requestItemDetailsRepository.getEDDRecallRetrievalRequestCounts(requestFromDate, requestToDate, institutionEntity.getId(), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED,RecapCommonConstants.REQUEST_STATUS_INITIAL_LOAD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)));
                reportsInstitutionForm.setRecallRequestCount(requestItemDetailsRepository.getEDDRecallRetrievalRequestCounts(requestFromDate, requestToDate,institutionEntity.getId(), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_RECALLED,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED,RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED),Arrays.asList(RecapCommonConstants.REQUEST_TYPE_RECALL)));
                reportsInstitutionForm.setEddRequestCount(requestItemDetailsRepository.getEDDRecallRetrievalRequestCounts(requestFromDate, requestToDate, institutionEntity.getId(), Arrays.asList(RecapCommonConstants.REQUEST_STATUS_EDD,RecapCommonConstants.REQUEST_STATUS_REFILED,RecapCommonConstants.REQUEST_STATUS_CANCELED),Arrays.asList(RecapCommonConstants.EDD)));
                reportsForm.getReportsInstitutionFormList().add(reportsInstitutionForm);
            }
            reportsForm.setShowRecallTable(true);
            reportsForm.setShowRetrievalTable(true);
            reportsForm.setShowReportResultsText(true);
            reportsForm.setShowRequestTypeTable(true);
            reportsForm.setShowNoteRequestType(true);
        }
        return reportsForm;
    }

    /**
     * Gets the response from the requestAccessionDeaccessionCounts method under ReportsServiceUtil class and
     * sets that response to the reports form to get displayed in the accession/deaccession reports UI page.
     *
     * @param reportsForm the reports form
     * @throws Exception the exception
     */
    public ReportsForm populateAccessionDeaccessionItemCounts(ReportsForm reportsForm) throws Exception {
        ReportsResponse reportsResponse = reportsServiceUtil.requestAccessionDeaccessionCounts(reportsForm);
        reportsForm.setReportsInstitutionFormList(reportsResponse.getReportsInstitutionFormList());
        reportsForm.setShowAccessionDeaccessionTable(true);
        reportsForm.setErrorMessage(reportsResponse.getMessage());
        return reportsForm;
    }


    /**
     * Gets the response from the requestCgdItemCounts method under ReportsServiceUtil class and
     * sets that response to the reports form to get displayed in the collection group designation reports UI page.
     *
     * @param reportsForm the reports form
     * @throws Exception the exception
     */
    public ReportsForm populateCGDItemCounts(ReportsForm reportsForm) throws Exception {
        ReportsResponse reportsResponse = reportsServiceUtil.requestCgdItemCounts(reportsForm);
        reportsForm.setReportsInstitutionFormList(reportsResponse.getReportsInstitutionFormList());
        reportsForm.setErrorMessage(reportsResponse.getMessage());
        return reportsForm;
    }

    /**
     * Passes the reports form to reports service util class and supports pagination for the deaccession reports.
     *
     * @param reportsForm the reports form
     * @return the list
     * @throws Exception the exception
     */
    public List<DeaccessionItemResultsRow> deaccessionReportFieldsInformation(ReportsForm reportsForm) throws Exception {
        ReportsResponse reportsResponse = reportsServiceUtil.requestDeaccessionResults(reportsForm);
        reportsForm.setTotalPageCount(reportsResponse.getTotalPageCount());
        reportsForm.setTotalRecordsCount(reportsResponse.getTotalRecordsCount());
        return reportsResponse.getDeaccessionItemResultsRows();
    }

    /**
     * Passes the reports form to reports service util class and supports pagination for the incomplete reports.
     *
     * @param reportsForm the reports form
     * @return the list
     * @throws Exception the exception
     */
    public List<IncompleteReportResultsRow> incompleteRecordsReportFieldsInformation(ReportsForm reportsForm) throws Exception {
        ReportsResponse reportsResponse = reportsServiceUtil.requestIncompleteRecords(reportsForm);
        if(!reportsForm.isExport()){
            reportsForm.setIncompleteTotalPageCount(reportsResponse.getIncompleteTotalPageCount());
            reportsForm.setIncompleteTotalRecordsCount(reportsResponse.getIncompleteTotalRecordsCount());
            reportsForm.setIncompletePageNumber(reportsForm.getIncompletePageNumber());
            reportsForm.setIncompletePageSize(reportsForm.getIncompletePageSize());
        }
        return reportsResponse.getIncompleteReportResultsRows();
    }

    /**
     * Export incomplete report search results to a csv file.
     *
     * @param incompleteReportResultsRows the incomplete report results rows
     * @param fileNameWithExtension       the file name with extension
     * @return the file
     */
    public File exportIncompleteRecords(List<IncompleteReportResultsRow> incompleteReportResultsRows, String fileNameWithExtension) {
        File file = new File(fileNameWithExtension);
        CsvWriter csvOutput = null;

        if (CollectionUtils.isNotEmpty(incompleteReportResultsRows)){
            try (FileWriter fileWriter = new FileWriter(file)){
                csvOutput = new CsvWriter(fileWriter, ',');
                writeHeader(csvOutput);
                for (IncompleteReportResultsRow incompleteReportResultsRow : incompleteReportResultsRows) {
                    if(CollectionUtils.isNotEmpty(incompleteReportResultsRows)){
                        writeRow(incompleteReportResultsRow,csvOutput);
                    }
                }
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR,e);
            }
            finally {
                if(csvOutput!=null) {
                    csvOutput.flush();
                    csvOutput.close();
                }
            }
        }
        return file;
    }

    private void writeRow(IncompleteReportResultsRow incompleteReportResultsRow, CsvWriter csvOutput) throws IOException {
        csvOutput.write(incompleteReportResultsRow.getTitle());
        csvOutput.write(incompleteReportResultsRow.getAuthor());
        csvOutput.write(incompleteReportResultsRow.getCustomerCode());
        csvOutput.write(incompleteReportResultsRow.getBarcode());
        csvOutput.write(incompleteReportResultsRow.getCreatedDate());
        csvOutput.endRecord();
    }

    private void writeHeader(CsvWriter csvOutput) throws Exception{
        csvOutput.write("Title");
        csvOutput.write("Author");
        csvOutput.write("Customer code");
        csvOutput.write("Barcode");
        csvOutput.write("Accession Date");
        csvOutput.endRecord();

    }

    /**
     * Get All institutions other than HTC
     * @return
     */
    public List<String> getInstitutions() {
        return institutionDetailsRepository.getInstitutionCodeForSuperAdmin().stream().map(InstitutionEntity::getInstitutionCode).collect(Collectors.toList());
    }

    /**
     * Get All institutions Entities other than HTC
     * @return
     */
    public List<InstitutionEntity> getInstitutionEntities() {
        return institutionDetailsRepository.getInstitutionCodeForSuperAdmin();
    }

    /**
     * Returns Institution Ids
     * @return
     */
    private List<Integer> getInstitutionIds() {
        return institutionDetailsRepository.getInstitutionCodeForSuperAdmin().stream().map(InstitutionEntity::getId).collect(Collectors.toList());
    }

    /**
     * Returns other partner institution Ids for an institution
     * @param partnerInstitutionId
     * @return
     */
    private List<Integer> getOtherInstitutionIdsForPartner(Integer partnerInstitutionId) {
        List<Integer> institutionsIds = new ArrayList<>(getInstitutionIds());
        institutionsIds.remove(partnerInstitutionId);
        return institutionsIds;
    }
}

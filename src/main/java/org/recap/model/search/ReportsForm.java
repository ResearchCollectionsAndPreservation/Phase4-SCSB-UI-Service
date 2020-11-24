package org.recap.model.search;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.reports.ReportsInstitutionForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/10/16.
 */
@Setter
@Getter
public class ReportsForm {

    private String showBy;
    private String requestType;
    private String requestFromDate;
    private String requestToDate;
    private String accessionDeaccessionFromDate;
    private String accessionDeaccessionToDate;
    private boolean showILBDResults=false;
    private boolean showPartners=false;
    private boolean showRequestTypeTable=false;
    private boolean showAccessionDeaccessionTable=false;
    private boolean showReportResultsText=false;
    private boolean showNoteILBD=false;
    private boolean showNotePartners=false;
    private boolean showNoteRequestType=false;
    private boolean showRetrievalTable=false;
    private boolean showRecallTable=false;
    private boolean showRequestTypeShow=false;

    private List<String> reportRequestType=new ArrayList<>();
    private List<String> owningInstitutions = new ArrayList<>();
    private List<String> collectionGroupDesignations = new ArrayList<>();
    private List<DeaccessionItemResultsRow> deaccessionItemResultsRows = new ArrayList<>();

    private boolean showDeaccessionInformationTable = false;
    private String totalRecordsCount = "0";
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private String deaccessionOwnInst;

    //IncompleteRecordsReport
    private String incompleteRequestingInstitution;
    private Integer incompletePageNumber = 0;
    private Integer incompletePageSize = 10;
    private String incompleteTotalRecordsCount = "0";
    private Integer incompleteTotalPageCount = 0;
    private List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
    private List<String> incompleteShowByInst =  new ArrayList<>();
    private boolean showIncompleteResults = false;
    private String errorMessage;
    private boolean showIncompletePagination = false;
    private boolean export = false;

    private List<ReportsInstitutionForm> reportsInstitutionFormList = new ArrayList<>();

    /**
     * Instantiates a new Reports form.
     */
    public ReportsForm() {
        this.getCollectionGroupDesignations().add("Shared");
        this.getCollectionGroupDesignations().add("Private");
        this.getCollectionGroupDesignations().add("Open");
    }


}

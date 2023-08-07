package org.recap;

/**
 * Created by SheikS on 6/20/2016.
 */
public class ScsbConstants {

    //Report Types
    public static final String MATCHING_TYPE = "Matching";
    public static final String MATCHING_COUNT_URI = "reportsService/titleMatchCount";
    public static final String MATCHING_REPORTS_URI = "reportsService/titleMatchReport";
    public static final String MATCHING_REPORTS_URI_EXPORT = "reportsService/titleMatchReportExport";
    public static final String MATCHING_REPORTS_URI_EXPORT_S3 = "reportsService/title-match-report-export-s3";
    //SEARCH
    public static final String SEARCH = "Search";
    public static final String REDIRECT_SEARCH = "redirect:search";
    public static final String REDIRECT_HOME = "redirect:home";
    public static final String REDIRECT_USER = "redirect:userLogin";
    public static final String FORWARD_INDEX = "forward:/index.html";
    public static final String FACETS_UN = "UN";
    //Error Message
    public static final String RECORD_NOT_AVAILABLE = "Database may be empty or Bib table does not contains this record";
    //Collection
    public static final String COLLECTION = "Collection";
    public static final String COLLECTION_UPDATE_CALLED = "Collection Update Called";
    public static final String COLLECTION_CCIB_CALLED = "Check Cross Institution Borrowed Called";
    public static final String COLLECTION_OMV_CALLED = "Open MarcView Called";
    public static final String BARCODES_NOT_AVAILABLE = "Barcode(s) not available";
    public static final String BARCODE_LIMIT_ERROR = "Only ten items can be processed. Items not processed:";
    public static final String OWNING_INST_CIRCULATION_FREEZE_ERROR = "The Owning Institution is on circulation freeze.";
    public static final String DEACCESSION_ERROR_REQUEST_CANCEL = "The active request associated with the item has been cancelled.";

    //Request
    public static final String REQUEST_PRIVATE_ERROR_USER_NOT_PERMITTED = "User is not permitted to request private item(s) from other partners";
    public static final String REQUEST_ERROR_USER_NOT_PERMITTED = "User is not permitted to request item(s)";
    public static final String REQUEST = "Request";
    public static final String BULK_REQUEST_CHECK = "BulkRequest";
    public static final String VALIDATE_REQUEST_ITEM_URL = "requestItem/validateItemRequestInformations";
    public static final String REQUEST_ITEM_URL = "requestItem/requestItem";

    public static final String REQUEST_ITEM_LOG_URL = "request-log/reports";

    public static final String REQUEST_ITEM_LOG_SUBMIT_URL = "request-log/submit";
    public static final String BULK_REQUEST_ITEM_URL = "requestItem/bulkRequest";
    public static final String URL_REQUEST_CANCEL = "requestItem/cancelRequest";
    public static final String URL_REQUEST_RESUBMIT = "requestItem/replaceRequest";
    public static final String DELIVERY_LOCATION = "deliveryLocation";
    public static final String REQUEST_NOTES = "requestNotes";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String NOT_AVAILABLE_ERROR_MESSAGE = "notAvailableErrorMessage";
    public static final String NO_PERMISSION_ERROR_MESSAGE = "noPermissionErrorMessage";
    public static final String NOT_AVAILABLE_FROZEN_ITEMS_ERROR_MESSAGE = "notAvailableFrozenItemsErrorMessage";
    public static final Integer CGD_PRIVATE = 3;

    public static final String REQUEST_TYPE = "requestType";
    public static final String REQUEST_TYPES = "requestTypes";
    public static final String SHOW_EDD = "showEDD";
    public static final String MULTIPLE_BARCODES = "multipleBarcodes";
    public static final String CREATE_BULKREQUEST_CALLED = "Create BulkRequest Called";
    //Reports
    public static final Integer CGD_SHARED = 1;
    public static final Integer CGD_OPEN = 2;
    public static final Boolean IS_EXPORT_FALSE = false;
    public static final Boolean IS_EXPORT_TRUE = true;
    public static final String REPORTS = "REPORTS";
    public static final String EXPORT = "EXPORT";
    public static final String COUNT = "COUNT";
    public static final String SCSB_DATA_DUMP_URL = "dataDump/exportDataDump";
    public static final String SCSB_SEARCH_SERVICE_URL = "searchService/search";
    public static final String SCSB_UPDATE_CGD_URL = "updateCgdService/updateCgd";
    public static final String SCSB_DEACCESSION_URL = "deAccessionService/deaccession";
    public static final String SCSB_DATA_EXPORT_RECENT_INFO_URL = "getRecentDataExportsInfo";

    public static final String SCSB_REPORTS_ACCESSION_DEACCESSION_COUNTS_URL = "reportsService/accessionDeaccessionCounts";
    public static final String SCSB_REPORTS_CGD_ITEM_COUNTS_URL = "reportsService/cgdItemCounts";
    public static final String SCSB_REPORTS_DEACCESSION_RESULTS_URL = "reportsService/deaccessionResults";
    public static final String SCSB_REPORTS_SUBMIT_RESULTS_URL = "reportsService/submitCollection";
    public static final String SCSB_REPORTS_ACCESSION_RESULTS_URL = "reportsService/accessionReports";
    public static final String SCSB_SHIRO_AUTHENTICATE_URL = "userAuth/authService";
    public static final String SCSB_SHIRO_SEARCH_URL = "auth/search";
    public static final String SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL = "auth/touchExistingSession";
    public static final String SCSB_SHIRO_REQUEST_URL = "auth/request";
    public static final String SCSB_SHIRO_COLLECTION_URL = "auth/collection";
    public static final String AUTH_PATH = "auth/";
    //  REPORT LOGS
    public static final String EXCEPTION_LOGS_TRANSACTION = "Exception Occured in while pulling Transaction Reports :: {}";
    public static final String EXCEPTION_LOGS_REQUEST_EXCEPTIONS = "Exception Occured while Exporting Request Exception Reports :: {}";
    //Monitoring and Logging
    public static final String SCSB_SHIRO_MONITORING_URL = "auth/monitoring";
    public static final String SCSB_SHIRO_LOGGING_URL = "auth/logging";
    public static final String SCSB_SHIRO_REPORT_URL = "auth/reports";
    public static final String SCSB_SHIRO_USER_ROLE_URL = "auth/userRoles";
    public static final String SCSB_SHIRO_ROLE_URL = "auth/roles";
    public static final String SCSB_SHIRO_DATAEXPORT_URL = "auth/dataExport";
    public static final String SCSB_SHIRO_LOGOUT_URL = "userAuth/logout";
    public static final String LOG_USER_LOGOUT_SUCCESS = "User logged out successfully";
    public static final String LOGOUT = "logout";
    public static final String IS_USER_AUTHENTICATED = "isAuthenticated";
    public static final String AUTH = "auth";
    public static final String LOG_USER_LOGOUT_URL = "/logout";
    public static final String USER_ID = "id";
    public static final String USER_INSTITUTION = "userInstitution";
    public static final String REQUEST_PRIVILEGE = "isRequestAllowed";
    public static final String COLLECTION_PRIVILEGE = "isCollectionAllowed";
    public static final String REPORTS_PRIVILEGE = "isReportAllowed";
    public static final String SEARCH_PRIVILEGE = "isSearchAllowed";
    public static final String USER_ROLE_PRIVILEGE = "isUserRoleAllowed";

    //ROLES
    public static final String INVALID_ROLE_NAME = "Please give one valid role name";
    public static final String WRONG_PERMISSION = "This permission is not given to this role";
    public static final String INVALID_PERMISSION = "Please give one valid permission name";
    public static final String SPECIAL_CHARACTERS_NOT_ALLOWED = "Special characters and spaces are not allowed";
    public static final String SPECIAL_CHARACTERS_NOT_ALLOWED_CREATE = "Special characters and spaces are not allowed in Role Name";
    public static final String ROLES_SUPER_ADMIN = "Super Admin";
    public static final String ROLES = "Roles";
    public static final String EDITED_AND_SAVED = " edited and saved successfully";
    public static final String DELETED_SUCCESSFULLY = " deleted successfully";
    public static final String ALREADY_EXISTS = " already exists";
    public static final String ADDED_SUCCESSFULLY = " added Successfully";
    public static final String EDITED_SUCCESSFULLY = " edited Successfully";
    //USERS
    public static final String LOGIN_USER = "Login User";
    public static final String NETWORK_LOGIN_ID_DOES_NOT_EXIST = "Network Login Id does not exist";
    public static final String EMAILID_ID_DOES_NOT_EXIST = "User email ID does not exist";
    public static final String NETWORK_LOGIN_ID_AND_EMAILID_ID_DOES_NOT_EXIST = "Network ID and email ID do not exist";

    //populating Item Barcode, Title and Institution for Request
    public static final String REQUESTED_BARCODE = "requestedBarcode";
    public static final String REQUESTED_ITEM_TITLE = "itemTitle";
    public static final String REQUESTED_ITEM_OWNING_INSTITUTION = "itemOwningInstitution";
    public static final String REQUESTED_ITEM_STORAGE_LOCATION = "storageLocation";
    public static final String OWNING_INSTITUTION = "owningInstitution";

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String SCSB_INSTITUTION_CODE = "RECAP_INSTITUTION_CODE";
    public static final String STATUS = "status";
    public static final String CREATED = "CREATED";
    public static final String SEARCH_REQUEST_ACTIVE = "active";
    public static final String SEARCH_REQUEST_REFILED = "REFILED";
    public static final String WARNING_MESSAGE_RETRIEVAL_CROSS_BORROWED_ITEM = "Warning : This item has an active retrieval request made by other institution.";
    public static final String WARNING_MESSAGE_RECALL_CROSS_BORROWED_ITEM = "Warning : This item has an active recall request made by other institution.";
    public static final String WARNING_MESSAGE_RETRIEVAL_BORROWED_ITEM = "Warning : This item has an active retrieval request.";
    public static final String WARNING_MESSAGE_RECALL_BORROWED_ITEM = "Warning : This item has an active recall request.";
    public static final String WARNING_MESSAGE_REQUEST_BORROWED_ITEM = "Warning : This item has an active retrieval request and recall request.";
    public static final String WARNING_MESSAGE_DEACCESSION_REQUEST_BORROWED_ITEM = "Deaccessioning the item will cancel the request.";

    public static final String USERS_TAB_CLICKED = "Users Tab Clicked";
    public static final String ROLES_TAB_CLICKED = "Roles Tab Clicked";
    public static final String REPORTS_TAB_CLICKED = "Reports Tab Clicked";
    public static final String REQUEST_TAB_CLICKED = "Request Tab Clicked";
    public static final String BULKREQUEST_TAB_CLICKED = "BulkRequest Tab Clicked";
    public static final String SEARCH_TAB_CLICKED = "Search Tab Clicked";
    public static final String COLLECTION_TAB_CLICKED = "Collection Tab Clicked";


    //Incomplete Record Reports
    public static final String SCSB_REPORTS_INCOMPLETE_RESULTS_URL = "reportsService/incompleteRecords";
    public static final String REPORTS_INCOMPLETE_RECORDS = "IncompleteRecordsReport";
    public static final String REPORTS_INCOMPLETE_RECORDS_NOT_FOUND = "No Records Found";
    public static final String REPORTS_INCOMPLETE_EXPORT_FILE_NAME = "ExportIncompleteRecords_";

    //UserManagementService
    public static final String USER_AUTH = "user_auth";
    public static final String ROLE_ID = "roleId";
    public static final String USER_TOKEN = "token";

    public static final String RESUBMIT_REQUEST_PRIVILEGE = "isReSubmitRequestAllowed";
    public static final String REQUEST_ALL_PRIVILEGE = "isRequestAllAllowed";
    public static final String REQUEST_ITEM_PRIVILEGE = "isRequestItemAllowed";
    public static final String BARCODE_RESTRICTED_PRIVILEGE = "isBarcodeRestricted";
    public static final String DEACCESSION_PRIVILEGE = "isDeaccessionAllowed";
    public static final String SUPER_ADMIN_USER = "isSuperAdmin";
    public static final String USER_ADMINISTRATOR = "isUserAdministrator";
    public static final String REPOSITORY = "isRepository";
    public static final String USER_AUTH_ERRORMSG = "authErrorMsg";
    public static final String USER_NAME = "userName";
    public static final String USER_DESC = "userDesc";
    public static final String ROLE_FOR_SUPER_ADMIN = "isRoleAllowed";
    public static final String TOKEN_SPLITER = ":";
    public static final String OCLC_NUMBER_PATTERN = "[^0-9]";
    public static final String LOGGED_IN_INSTITUTION = "loggedInInstitution";

    //EXCEPTION REPORTS
    public static final String REPORTS_EXCEPTION = "EXCEPTION";
    public static final String ID = "id";
    public static final String NA = "NA";

    public static final String UNSCHEDULE = "Unschedule";
    public static final String UNSCHEDULED = "Unscheduled";
    public static final String SCHEDULED = "Scheduled";
    public static final String REQUESTED_ITEM_AVAILABILITY = "requestedItemAvailabilty";
    public static final String BULK_REQUEST_FORM = "bulkRequestForm";
    public static final String BULK_REQUEST = "bulkRequest";
    public static final String IN_PROCESS = "IN PROCESS";
    public static final String SCSB_SHIRO_BULK_REQUEST_URL = "auth/bulkRequest";
    public static final String EXCEPTION = "EXCEPTION";

    public static final String MONITORING = "isMonitoring";
    public static final String LOGGING = "isLogging";

    public static final String DATA_EXPORT = "isDataExport";

    public static final String REQUESTLOG = "isRequestLog";
    public static final String SCSB_UI = "scsb_ui";
    public static final String SCSB_AUTH = "scsb_auth";
    public static final String SCSB_GATEWAY = "scsb_gateway";
    public static final String SCSB_DOC = "scsb_doc";
    public static final String SCSB_CIRC = "scsb_circ";
    public static final String SCSB_BATCH = "scsb_batch_scheduler";
    public static final String AUTH_TYPE_OAUTH = "OAuth2";
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static final String IS_AUTHENTICATED = "isAuthenticated";
    public static final String ON_BOARD_INSTITUTION_STATUS = "On-Board Institution Status";
    public static final String ADMIN_TEXT = "Admin";
    public static final String DESCRIPTION = "Description";
    public static final String SUCCESSED = "Successed";
    public static final String FAILED = "Failed";
    public static final String PWD_DELIVERY_RESTRICT_TYPE = "PWD";
    public static final String FILE_NAMES = "fileNames";
    //AUTHENTICATN

    public static final String FALSE_STRING = "FALSE";
    public static final String TRUE_STRING = "TRUE";
    public static final String USER_AUTHENTICATED = "user_authenticated";
    public static final String API_PATH = "api_path";
    public static final String MVM = "MVM";
    public static final String CSP_VALUE = "csp.value";
    public static final String CSP_ENABLE = "csp.enable";
    public static final String CSP = "Content-Security-Policy";

    private ScsbConstants() {
        //Do nothing
    }
}

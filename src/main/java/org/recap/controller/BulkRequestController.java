package org.recap.controller;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.request.DownloadReports;
import org.recap.model.search.BulkRequestForm;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.service.BulkRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by akulak on 19/9/17.
 */
@RestController
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RequestMapping("/bulkRequest")
public class BulkRequestController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(BulkRequestController.class);
    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    private BulkRequestService bulkRequestService;
    @Autowired
    private BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @GetMapping("/checkPermission")
    public boolean bulkRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_BULK_REQUEST_URL);
        if (authenticated) {
            logger.info(RecapConstants.BULKREQUEST_TAB_CLICKED);
            return RecapConstants.TRUE;
        } else {
            return UserManagementService.unAuthorizedUser(session, RecapConstants.BULK_REQUEST_CHECK, logger);
        }
    }

    @PostMapping("/loadCreateRequest")
    public BulkRequestForm loadCreateRequest(@RequestBody BulkRequestForm bulkRequestForm) {
        return loadCreateRequestPage(bulkRequestForm);
    }

    @PostMapping("/createBulkRequest")
    public JSONObject createRequest(@RequestParam("file") MultipartFile file, @RequestParam("deliveryLocation") String deliveryLocation
            , @RequestParam("requestingInstitutionId") String requestingInstitutionId
            , @RequestParam("patronBarcodeId") String patronBarcodeId
            , @RequestParam("BulkRequestName") String BulkRequestName
            , @RequestParam("choosenFile") String choosenFile
            , @RequestParam("patronEmailId") String patronEmailId, HttpServletRequest request, HttpServletResponse response) {
        BulkRequestForm bulkRequestForm = new BulkRequestForm();
        bulkRequestForm.setFile(file);
        bulkRequestForm.setPatronEmailAddress(patronEmailId);
        bulkRequestForm.setFileName(choosenFile);
        bulkRequestForm.setDeliveryLocationInRequest(deliveryLocation);
        bulkRequestForm.setBulkRequestName(BulkRequestName);
        bulkRequestForm.setPatronBarcodeInRequest(patronBarcodeId);
        bulkRequestForm.setRequestingInstitution(requestingInstitutionId);
        logger.info(RecapConstants.CREATE_BULKREQUEST_CALLED);
        HashMap<String, String> resStatus = new HashMap<>();
        try {
            BulkRequestForm res = bulkRequestService.processCreateBulkRequest(bulkRequestForm, request);
            if (res.getErrorMessage() == null)
                resStatus.put(RecapConstants.STATUS, RecapConstants.CREATED);
            else
                resStatus.put(RecapConstants.STATUS, res.getErrorMessage());
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
            resStatus.put(RecapConstants.STATUS, e.getMessage());
        }
        JSONObject recvObj = new JSONObject(resStatus);
        return recvObj;
    }

    @PostMapping("/searchRequest")
    public BulkRequestForm searchRequest(@RequestBody BulkRequestForm bulkRequestForm) {
        logger.info(RecapConstants.CREATE_SR_BULKREQUEST_CALLED);
        return bulkRequestService.processSearchRequest(bulkRequestForm);
    }


    @PostMapping("/requestPageSizeChange")
    public BulkRequestForm onPageSizeChange(@RequestBody BulkRequestForm bulkRequestForm) {
        return bulkRequestService.processOnPageSizeChange(bulkRequestForm);
    }

    @PostMapping("/first")
    public BulkRequestForm searchFirst(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(0);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
    }

    @PostMapping("/previous")
    public BulkRequestForm searchPrevious(@RequestBody BulkRequestForm bulkRequestForm) {

        bulkRequestForm.setPageNumber(bulkRequestForm.getPageNumber() - 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
    }

    @PostMapping("/next")
    public BulkRequestForm searchNext(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(bulkRequestForm.getPageNumber() + 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
    }

    @PostMapping("/last")
    public BulkRequestForm searchLast(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(bulkRequestForm.getTotalPageCount() - 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
    }

    @GetMapping("/bulkRequest/goToSearchRequest")
    public ModelAndView searchRequestByPatronBarcode(String patronBarcodeInRequest, Model model) {
        BulkRequestForm bulkRequestForm = new BulkRequestForm();
        bulkRequestForm.setPatronBarcodeSearch(patronBarcodeInRequest);
        bulkRequestService.processSearchRequest(bulkRequestForm);
        loadSearchRequestPage(bulkRequestForm);
        model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        return new ModelAndView(RecapConstants.BULK_REQUEST, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
    }

    @PostMapping("/bulkRequest/loadCreateRequestForSamePatron")
    public ModelAndView loadCreateRequestForSamePatron(@Valid @ModelAttribute("bulkRequestForm") BulkRequestForm bulkRequestForm, Model model) {
        loadCreateRequestPage(bulkRequestForm);
        bulkRequestForm.setSubmitted(false);
        bulkRequestForm.setRequestingInstitution(bulkRequestForm.getRequestingInstituionHidden());
        return processBulkRequest(bulkRequestForm, model);
    }

    @PostMapping("/populateDeliveryLocations")
    public BulkRequestForm populateDeliveryLocations(@RequestBody BulkRequestForm bulkRequestForm) {
        return bulkRequestService.processDeliveryLocations(bulkRequestForm);
    }

    @GetMapping("/{bulkRequestId}")
    public DownloadReports downloadReports(@PathVariable String bulkRequestId) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        BulkRequestItemEntity bulkRequestItemEntity = bulkRequestService.saveUpadatedRequestStatus(Integer.valueOf(bulkRequestId));
        String fileNameWithExtension = "Results_" + StringUtils.substringBefore(bulkRequestItemEntity.getBulkRequestFileName(), ".csv") + dateFormat.format(new Date()) + ".csv";
        DownloadReports downloadReports = new DownloadReports();
        downloadReports.setContent(bulkRequestItemEntity.getBulkRequestFileData());
        downloadReports.setFileName(fileNameWithExtension);
        return downloadReports;
    }

    private BulkRequestForm loadCreateRequestPage(BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setRequestingInstitutions(getInstitutions());
        return bulkRequestForm;
    }

    private void loadSearchRequestPage(BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setInstitutionList(getInstitutions());
    }

    private List<String> getInstitutions() {
        return institutionDetailsRepository.getInstitutionCodeForSuperAdmin().stream().map(InstitutionEntity::getInstitutionCode).collect(Collectors.toList());
    }

    private ModelAndView processBulkRequest(BulkRequestForm bulkRequestForm, Model model) {
        bulkRequestService.processDeliveryLocations(bulkRequestForm);
        model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        return new ModelAndView(RecapConstants.BULK_REQUEST, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
    }


}

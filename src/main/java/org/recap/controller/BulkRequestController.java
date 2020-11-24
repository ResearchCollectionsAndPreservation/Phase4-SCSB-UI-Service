package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.request.DownloadReports;
import org.recap.model.search.BulkRequestForm;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.BulkRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by akulak on 19/9/17.
 */
@RestController
@CrossOrigin
@RequestMapping("/bulkRequest")
public class BulkRequestController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(BulkRequestController.class);
    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    private BulkRequestService bulkRequestService;
    @Autowired
    private BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @PostMapping("/loadCreateRequest")
    public BulkRequestForm loadCreateRequest(@RequestBody BulkRequestForm bulkRequestForm) {
        return loadCreateRequestPage(bulkRequestForm);
    }

    @PostMapping("/createBulkRequest")
    //@RequestMapping (value=("/createBulkRequest"),consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method=RequestMethod.POST)
    public BulkRequestForm createRequest(@RequestParam("file") MultipartFile file, @RequestParam("deliveryLocation") String deliveryLocation
            , @RequestParam("requestingInstitutionId") String requestingInstitutionId
            , @RequestParam("patronBarcodeId") String patronBarcodeId
            , @RequestParam("BulkRequestName") String BulkRequestName
            , @RequestParam("choosenFile") String choosenFile
            , @RequestParam("patronEmailId") String patronEmailId) {
        //loadCreateRequestPage(bulkRequestForm);\
        BulkRequestForm bulkRequestForm = new BulkRequestForm();
        bulkRequestForm.setPatronEmailAddress(patronEmailId);
        bulkRequestForm.setFileName(choosenFile);
        bulkRequestForm.setDeliveryLocationInRequest(deliveryLocation);
        bulkRequestForm.setBulkRequestName(BulkRequestName);
        bulkRequestForm.setPatronBarcodeInRequest(patronBarcodeId);
        bulkRequestForm.setRequestingInstitution(requestingInstitutionId);
        logger.info("createBulkRequest function --> Called");
        return bulkRequestService.processCreateBulkRequest(bulkRequestForm);
        //return bulkRequestForm;
    }

    @PostMapping("/searchRequest")
    public BulkRequestForm searchRequest(@RequestBody BulkRequestForm bulkRequestForm) {
        logger.info("SearchRequest --> called");
        return bulkRequestService.processSearchRequest(bulkRequestForm);
        //return bulkRequestForm;
    }


    @PostMapping("/requestPageSizeChange")
    public BulkRequestForm onPageSizeChange(@RequestBody BulkRequestForm bulkRequestForm) {
        return bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        //return bulkRequestForm;
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return new ModelAndView(RecapConstants.BULK_SEARCH_REQUEST_SECTION, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
    }

    @PostMapping("/first")
    public BulkRequestForm searchFirst(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(0);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
        //return bulkRequestForm;
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return new ModelAndView(RecapConstants.BULK_SEARCH_REQUEST_SECTION, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
    }

    @PostMapping("/previous")
    public BulkRequestForm searchPrevious(@RequestBody BulkRequestForm bulkRequestForm) {

        bulkRequestForm.setPageNumber(bulkRequestForm.getPageNumber() - 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
        //return bulkRequestForm;
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return new ModelAndView(RecapConstants.BULK_SEARCH_REQUEST_SECTION, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
    }

    @PostMapping("/next")
    public BulkRequestForm searchNext(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(bulkRequestForm.getPageNumber() + 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return new ModelAndView(RecapConstants.BULK_SEARCH_REQUEST_SECTION, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
        //return bulkRequestForm;
    }

    @PostMapping("/last")
    public BulkRequestForm searchLast(@RequestBody BulkRequestForm bulkRequestForm) {
        bulkRequestForm.setPageNumber(bulkRequestForm.getTotalPageCount() - 1);
        return bulkRequestService.getPaginatedSearchResults(bulkRequestForm);

        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return new ModelAndView(RecapConstants.BULK_SEARCH_REQUEST_SECTION, RecapConstants.BULK_REQUEST_FORM, bulkRequestForm);
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
        //response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameWithExtension + "\"");
        // response.setContentLength(bulkRequestItemEntity.getBulkRequestFileData().length);
        //FileCopyUtils.copy(bulkRequestItemEntity.getBulkRequestFileData(), response.getOutputStream());
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.BULK_REQUEST);
        //return bulkRequestItemEntity.getBulkRequestFileData();
        //return b.toString();
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

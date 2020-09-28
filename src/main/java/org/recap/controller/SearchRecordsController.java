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
    String json = "{\n" +
            "\t\"totalPageCount\": 915,\n" +
            "\t\"totalBibRecordsCount\": \"45,726\",\n" +
            "\t\"totalItemRecordsCount\": \"396,872\",\n" +
            "\t\"totalRecordsCount\": \"45,726\",\n" +
            "\t\"showTotalCount\": false,\n" +
            "\t\"errorMessage\": null,\n" +
            "\t\"searchResultRows\": [{\n" +
            "\t\t\t\"bibId\": 1993640,\n" +
            "\t\t\t\"title\": \"The ... Agricultural survey.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"The Dept. of Agriculture,\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1967/68-1972/73; 1977\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"837187\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 8891626,\n" +
            "\t\t\t\"title\": \"... ajandası / Kadın Eserleri Kütüphanesi ve Bilgi Merkezi Vakfı.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"Kadın Eserleri Kütüphanesi ve Bilgi Merkezi Vakfı,\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2016-2017\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"10503953\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1579711,\n" +
            "\t\t\t\"title\": \"[ Analyses of Hour and Wage Provisions of Codes Approved].\",\n" +
            "\t\t\t\"author\": \" United States. Women's Bureau.  \",\n" +
            "\t\t\t\"publisher\": \"Women's Bureau,\",\n" +
            "\t\t\t\"publisherDate\": \"1933].\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"No. 1159 (Aug., 1933) - no. 1356 (June, 1934)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"IR-12207\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"no.1159-1340 (Aug., 1933 - April, 1934)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077419008\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2224561,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5938134\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6374676\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"IR-12207\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"no.1179-1254 (Aug., 1933 - Dec., 1933)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077419016\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2224562,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5938165\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6374676\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"IR-12207\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"no.1257-1356 (Dec., 1933 - June, 1934)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077419024\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2224563,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5938197\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6374676\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"6386321\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1934216,\n" +
            "\t\t\t\"title\": \".. Anglo-American Aeronautical Conference.\",\n" +
            "\t\t\t\"author\": \"  Anglo-American Aeronautical Conference. \",\n" +
            "\t\t\t\"publisher\": \"The Royal Aeronautical Society,\",\n" +
            "\t\t\t\"publisherDate\": \"1952.\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"3rd (4th-7th Sept. 1951)-4th (15th-17th Sept. 1953)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1951\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244348\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2930065,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925537\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782633\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1953\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244330\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2930066,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925540\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782633\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"701147\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1935273,\n" +
            "\t\t\t\"title\": \"... Anglo-American Aeronautical Conference.\",\n" +
            "\t\t\t\"author\": \"  Anglo-American Aeronautical Conference. \",\n" +
            "\t\t\t\"publisher\": \"Royal Aeronautical Society,\",\n" +
            "\t\t\t\"publisherDate\": \"1959-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"6th (9th-12th Sept. 1957)-9th (Oct. 16-24, 1963); 15th (31 May-2 June 1977)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"15th (1977)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244512\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2944252,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925578\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782672\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"6th , (1957)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244454\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2944248,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925561\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782672\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"7th, (1959)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244447\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2944249,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925568\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782672\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"8th (1961)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244439\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2944250,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925574\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782672\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"9th (1963)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101045244421\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2944251,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3925576\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"782672\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"701181\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1877691,\n" +
            "\t\t\t\"title\": \"... Annual Conference and General Meeting.\",\n" +
            "\t\t\t\"author\": \" East African Law Society. Annual Conference and General Meeting, author.  \",\n" +
            "\t\t\t\"publisher\": \"East African Law Society\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"20th (2015)-21st (2016)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"KQC74.A353 .E197\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"20th (2015)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101098726506\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2792084,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"7583399\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"9919989\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"KQC74.A353 .E197\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"21st (2016)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101097391245\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 14585586,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"7703963\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"9919989\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"10128754\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 530453,\n" +
            "\t\t\t\"title\": \"... Annual conference of state and local boards of health.\",\n" +
            "\t\t\t\"author\": \" New Jersey. Board of Health.  \",\n" +
            "\t\t\t\"publisher\": \"Board of Health of the State of New Jersey,\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1907\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"8941.674.2\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1907\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101058089093\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 644680,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"4214289\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"2461067\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2174942\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 9228836,\n" +
            "\t\t\t\"title\": \".. annual report.\",\n" +
            "\t\t\t\"author\": \" Southern Sudan Human Rights Commission, author,  \",\n" +
            "\t\t\t\"publisher\": \"Southern Sudan Human Rights Commission,\",\n" +
            "\t\t\t\"publisherDate\": \"[2008]-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1st (July 2006/December 2007)-2nd (2008)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"JC599.S643 S68\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1st (July/Dec. 2007)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101103736482\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 15241815,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"8009717\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"10845745\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"JC599.S643 S68\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2nd (2008)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101103736490\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 15241874,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"8009719\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"10845745\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"11131831\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1823309,\n" +
            "\t\t\t\"title\": \"... annual report.\",\n" +
            "\t\t\t\"author\": \" Chamber of Mines (Zimbabwe), author,  \",\n" +
            "\t\t\t\"publisher\": \"The Chamber of Mines of Zimbabwe\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2013-2014\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"TN119.R45 C46aq\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2013\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101092836939\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2725149,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"7192752\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"9027500\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"TN119.R45 C46aq\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2014\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101099156919\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2725150,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"7463298\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"9027500\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"9143559\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 562146,\n" +
            "\t\t\t\"title\": \"... Annual report / Division of the New Jersey Real Estate Commission in the Department of Banking and Insurance.\",\n" +
            "\t\t\t\"author\": \" New Jersey. Division of the New Jersey Real Estate Commission.  \",\n" +
            "\t\t\t\"publisher\": \"The Commission,\",\n" +
            "\t\t\t\"publisherDate\": \"1954-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1953/54-  1957/58 -1959/60\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2228806\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 9419892,\n" +
            "\t\t\t\"title\": \"... annual report / NBFIRA, Non-Bank Financial Institutions Regulatory Authority.\",\n" +
            "\t\t\t\"author\": \" Non-Bank Financial Institutions Regulatory Authority (Botswana), author,  \",\n" +
            "\t\t\t\"publisher\": \"Non-Bank Financial Institutions Regulatory Authority\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2014\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HG187.5.B55 N66aq\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"2014\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101107802785\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 15645570,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"8146249\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"11160294\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"11471029\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1934061,\n" +
            "\t\t\t\"title\": \"... Annual report of Board of Directors and balance sheet, December 31 ...\",\n" +
            "\t\t\t\"author\": \" Amerada Corporation.  \",\n" +
            "\t\t\t\"publisher\": \"The Corporation,\",\n" +
            "\t\t\t\"publisherDate\": \"1926-[1941]\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"5th (1925)-[20th] (1940)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HD9569.A5 A3\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1925/1945\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101089725491\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 2928425,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"6939747\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"779054\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"697999\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 760631,\n" +
            "\t\t\t\"title\": \"... Annual report of the directors of the Northern Railroad to the stockholders.\",\n" +
            "\t\t\t\"author\": \" Northern Railroad Company (N.H.)  \",\n" +
            "\t\t\t\"publisher\": \"s.n.,\",\n" +
            "\t\t\t\"publisherDate\": \"1846-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"27th-47th\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HE2791.N83A1\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"27th-47th (1871/72-1891/92)\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101066786888\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 990515,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"4944825\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"2784446\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2483208\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 677547,\n" +
            "\t\t\t\"title\": \"... Annual report of the managers and officers of the New Jersey State Hospital at Greystone Park : for the year ending ...\",\n" +
            "\t\t\t\"author\": \" New Jersey State Hospital at Greystone Park.  \",\n" +
            "\t\t\t\"publisher\": \"The Hospital\",\n" +
            "\t\t\t\"publisherDate\": \"1925-1959\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1943/44-1948/49, 1951/52-1959\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"HV3006.N3G86\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1943/44-1948/49 (inc.)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101057576876\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 851126,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"4260193\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2640265\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HV3006.N3G86\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1951/52-1960/61\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101057576884\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 851125,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"4260192\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2640265\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2347124\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1468728,\n" +
            "\t\t\t\"title\": \"... Annual report of the president, treasurer and attorney ... for the year ...\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": null,\n" +
            "\t\t\t\"publisherDate\": \"[19--]\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"47th annual, 1922\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"IR-7973\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"47th annual (1922)\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101065068437\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 2083940,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"4862287\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"5587786\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"5436211\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 562145,\n" +
            "\t\t\t\"title\": \"... Annual report to the ... Governor of the state of New Jersey / by the New Jersey Real Estate Commission.\",\n" +
            "\t\t\t\"author\": \" New Jersey Real Estate Commission.  \",\n" +
            "\t\t\t\"publisher\": \"The Commission,\",\n" +
            "\t\t\t\"publisherDate\": \"1922-1923.\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1921/22-1922/23 -1938/39\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HD266.N4A5\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1921-/22-1938/39\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101047854086\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 682550,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"2892898\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"2515913\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2228799\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1567417,\n" +
            "\t\t\t\"title\": \"The ... biennial convention proceedings, District No. 22, United Mine Workers of America ...\",\n" +
            "\t\t\t\"author\": \" United Mine Workers of America. District 22.  \",\n" +
            "\t\t\t\"publisher\": \"The District\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1st -3rd, (1914-1918);  Special, (1917);  5th-10th, (1922-1934);  12th-13th, (1939-1941).\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"10th (1934)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411781\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209015,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859675\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"12th (1939)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411799\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209016,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859681\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"13th (1941)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411807\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209017,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859684\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1st (1914)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411690\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209006,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859638\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2nd (1916)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411708\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209007,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859639\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"3rd (1918)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411716\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209008,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859640\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"5th (1922)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411732\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209010,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859653\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"6th (1924)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411740\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209011,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859657\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"7th (1927)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411757\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209012,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859659\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"8th (1930)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411765\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209013,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859662\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"9th (1932)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411773\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209014,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859668\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HD6515.M615 U52\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"Special (1917)\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101077411724\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2209009,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"5859645\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"6253051\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"6243253\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1776166,\n" +
            "\t\t\t\"title\": \"... budget performance quarterly report / Malawi Government.\",\n" +
            "\t\t\t\"author\": \" Malawi, author.  \",\n" +
            "\t\t\t\"publisher\": \"Ministry of Finance,\",\n" +
            "\t\t\t\"publisherDate\": \"[2010]-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2010/11 Q1-2010/11 Q2 ; 2011/12, Q1 (Oct. 2011)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ83.2 .A23aq\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2010/11, Q1\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101092252780\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2668075,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"6986645\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"8035071\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ83.2 .A23aq\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2010/11, Q2\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101092252798\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2668076,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"6986647\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"8035071\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ83.2 .A23aq\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"2011/12, Q1\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101091634319\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 2668074,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"6865119\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"8035071\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"8282314\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 815991,\n" +
            "\t\t\t\"title\": \"The ... Bulletin of the Central Intercollegiate Athletic Association ... with proceedings of the ... annual meeting.\",\n" +
            "\t\t\t\"author\": \" Central Intercollegiate Athletic Association.  \",\n" +
            "\t\t\t\"publisher\": \"s.n.]\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1951-1953\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"4200.247\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1951-53\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101087116495\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 1071773,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"6574332\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"2878817\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2573981\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 598546,\n" +
            "\t\t\t\"title\": \"... Compte géneral de l'administration des finances rendu ... ... Algemeene rekening van het bestuur van financien afgelegd ... door den Minister van financien ... par le Ministre des finances\",\n" +
            "\t\t\t\"author\": \" Belgium. Ministère des finances.  \",\n" +
            "\t\t\t\"publisher\": null,\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1913-1914, 1917-1918\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ53.A3q\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1913\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101078729942\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 728761,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"2634382\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2588153\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ53.A3q\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1914\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101078729959\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 728760,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"2634379\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2588153\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ53.A3q\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1917\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101078729603\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 728759,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"2634377\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2588153\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"HJ53.A3q\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1918\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101078729611\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 728758,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"2634374\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"2588153\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2297446\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 8986342,\n" +
            "\t\t\t\"title\": \"... energy, air and climate change.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"Bureau of Statistics\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2014\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HD1698.L5 E647q\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"2014\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101106513706\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 14821518,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"7826769\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"10516729\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"10789983\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 688269,\n" +
            "\t\t\t\"title\": \"... Financial abstract.\",\n" +
            "\t\t\t\"author\": \" London County Council.  \",\n" +
            "\t\t\t\"publisher\": null,\n" +
            "\t\t\t\"publisherDate\": \"1897-19--\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1889/94, 1896/1906-1898/1908, 1910/11-1919/20\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HJ9041.L75\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1889/1904, 1898/1908\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101050298130\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 865028,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"2892416\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"2659315\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"2365456\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1287140,\n" +
            "\t\t\t\"title\": \"... Grand Annual Entertainment & Dance of the Enosis Lefkariton of Cyprus in America, Inc. = Henōsis Leukaritōn en Amerikē : ... Megalē Etēsia Choroesperis.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"Enosis Lefkariton of Cyprus in America,\",\n" +
            "\t\t\t\"publisherDate\": \"1931-\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"1968\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"E184.G7 G66\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1968\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101046403984\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 1841119,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"3421922\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"4192330\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"3893122\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1829000,\n" +
            "\t\t\t\"title\": \"... ICT statistics report.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"Statistics Botswana\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2011\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"HN806.Z9 I565q\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"2011\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101098720459\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 2731872,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"7377194\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"9075149\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"9199190\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1935257,\n" +
            "\t\t\t\"title\": \"... International Aeronautical Conference.\",\n" +
            "\t\t\t\"author\": \"  International Aeronautical Conference. \",\n" +
            "\t\t\t\"publisher\": \"The Institute of Aeronautical Sciences, Inc.,\",\n" +
            "\t\t\t\"publisherDate\": \"[1949].\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2nd (24th-26th May 1949)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1949\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101045244355\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 2944121,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"3925527\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"782624\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"701138\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1935259,\n" +
            "\t\t\t\"title\": \"... International Aeronautical Conference.\",\n" +
            "\t\t\t\"author\": \"  International Aeronautical Conference. \",\n" +
            "\t\t\t\"publisher\": \"Institute of the Aeronautical Sciences,\",\n" +
            "\t\t\t\"publisherDate\": \"[1955].\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"5th (1955)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"9250.114\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"1955\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101045244462\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 2944126,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"3925544\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"782644\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"701158\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 9486403,\n" +
            "\t\t\t\"title\": \"... İstanbul Film Festivali.\",\n" +
            "\t\t\t\"author\": \"  Uluslararası İstanbul Film Festivali. \",\n" +
            "\t\t\t\"publisher\": \"[İstanbul Kültür Sanat Vakfı]\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"No. 31 (2012)\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"PN1993.4 .U48\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"no.31 ((2012))\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101102550272\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 15752990,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"7849891\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"10544814\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"10824787\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 1154321,\n" +
            "\t\t\t\"title\": \"The ... Lawyer statistical report.\",\n" +
            "\t\t\t\"author\": \"   Lawyer statistical report (Chicago : 1967)\",\n" +
            "\t\t\t\"publisher\": \"American Bar Foundation,\",\n" +
            "\t\t\t\"publisherDate\": \"1968-1972.\",\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\t\"callNumber\": \"7680.1275.2\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1967\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101043028644\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 1604673,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3228902\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"392589\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"callNumber\": \"7680.1275.2\",\n" +
            "\t\t\t\t\t\"chronologyAndEnum\": \"1971\",\n" +
            "\t\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\t\"barcode\": \"32101043028636\",\n" +
            "\t\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\t\"itemId\": 1604674,\n" +
            "\t\t\t\t\t\"owningInstitutionItemId\": \"3228903\",\n" +
            "\t\t\t\t\t\"owningInstitutionHoldingsId\": \"392589\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"359288\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"bibId\": 9431218,\n" +
            "\t\t\t\"title\": \"... Mazu wen hua nian jian.\",\n" +
            "\t\t\t\"author\": \"   \",\n" +
            "\t\t\t\"publisher\": \"Fujian Sheng Lianjiang Xian zheng fu\",\n" +
            "\t\t\t\"publisherDate\": null,\n" +
            "\t\t\t\"owningInstitution\": \"PUL\",\n" +
            "\t\t\t\"customerCode\": null,\n" +
            "\t\t\t\"collectionGroupDesignation\": null,\n" +
            "\t\t\t\"useRestriction\": null,\n" +
            "\t\t\t\"barcode\": null,\n" +
            "\t\t\t\"summaryHoldings\": \"2015\",\n" +
            "\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\"leaderMaterialType\": \"Serial\",\n" +
            "\t\t\t\"selected\": false,\n" +
            "\t\t\t\"showItems\": true,\n" +
            "\t\t\t\"selectAllItems\": false,\n" +
            "\t\t\t\"itemId\": null,\n" +
            "\t\t\t\"searchItemResultRows\": [{\n" +
            "\t\t\t\t\"callNumber\": \"DS797.26.L537 M38q\",\n" +
            "\t\t\t\t\"chronologyAndEnum\": \"2015\",\n" +
            "\t\t\t\t\"customerCode\": \"PA\",\n" +
            "\t\t\t\t\"barcode\": \"32101098265760\",\n" +
            "\t\t\t\t\"useRestriction\": \"No Restrictions\",\n" +
            "\t\t\t\t\"collectionGroupDesignation\": \"Shared\",\n" +
            "\t\t\t\t\"availability\": \"Available\",\n" +
            "\t\t\t\t\"selectedItem\": false,\n" +
            "\t\t\t\t\"itemId\": 15665497,\n" +
            "\t\t\t\t\"owningInstitutionItemId\": \"8023910\",\n" +
            "\t\t\t\t\"owningInstitutionHoldingsId\": \"10867639\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"owningInstitutionBibId\": \"11157665\",\n" +
            "\t\t\t\"owningInstitutionHoldingsId\": null,\n" +
            "\t\t\t\"owningInstitutionItemId\": null,\n" +
            "\t\t\t\"requestPosition\": null,\n" +
            "\t\t\t\"patronBarcode\": null,\n" +
            "\t\t\t\"requestingInstitution\": null,\n" +
            "\t\t\t\"deliveryLocation\": null,\n" +
            "\t\t\t\"requestType\": null,\n" +
            "\t\t\t\"requestNotes\": null\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";
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
        SearchRecordsResponse searchRecordsResponse = searchUtil.requestSearchResults(searchRecordsRequest);
        return searchRecordsResponse;
        //SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        //Gson gon = new Gson();
        //SearchRecordsResponse sr = gon.fromJson(json, SearchRecordsResponse.class);
        //return sr;
    }

    /**
     * Performs search on solr and returns the previous page results as rows to get displayed in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @return the model and view
     */
    @PostMapping("/previous")
    public SearchRecordsResponse searchPrevious(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        //searchRecordsRequest.setPageNumber(setPageNumber(searchRecordsRequest));
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
        //searchRecordsRequest.setPageNumber(searchRecordsRequest.getPageNumber()+1);
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
        //searchRecordsRequest.setPageNumber(searchRecordsRequest.getTotalPageCount()-1);
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
     * @param model the model
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
        UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.REQUEST_PRIVILEGE);
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
     * @param response             the response
     * @param result               the result
     * @param model                the model
     * @return the byte [ ]
     * @throws Exception the exception
     */
    @PostMapping("/export")
    public byte[] exportRecords(@RequestBody SearchRecordsRequest searchRecordsRequest, HttpServletResponse response,
                                BindingResult result,
                                Model model) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileNameWithExtension = "ExportRecords_" + dateFormat.format(new Date()) + ".csv";
        File csvFile = csvUtil.writeSearchResultsToCsv(searchRecordsRequest.getSearchResultRows(), fileNameWithExtension);
        return HelperUtil.getFileContent(csvFile, model, response, fileNameWithExtension, RecapCommonConstants.SEARCH);
    }

    /**
     * Performs search on solr on changing the page size. The number of results returned will be equal to the selected page size.
     *
     * @param searchRecordsRequest the search records request
     */

    @PostMapping("/pageChanges")
    public SearchRecordsResponse onPageSizeChange(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        //searchRecordsRequest.setPageNumber(getPageNumberOnPageSizeChange(searchRecordsRequest));
        //int totalRecordsCount;
        Integer pageNumber = searchRecordsRequest.getPageNumber();
        searchRecordsRequest.setPageNumber(0);
        SearchRecordsResponse searchRecordsResponse = searchRecordsPage(searchRecordsRequest);

        //searchRecordsResponse.setPageNumber(getPageNumberOnPageSizeChange(searchRecordsRequest,searchRecordsResponse));
        if (searchRecordsResponse.getTotalPageCount() > 0 && pageNumber >= searchRecordsResponse.getTotalPageCount()) {
            pageNumber = searchRecordsResponse.getTotalPageCount() - 1;
        }
        //searchRecordsResponse.setPageNumber(pageNumber);
        //searchRecordsRequest.setPageNumber(getPageNumberOnPageSizeChange(searchRecordsResponse,searchRecordsRequest.getPageSize(),pageNumber));
        //SearchRecordsResponse searchRecordsResponseN = searchRecordsPage(searchRecordsRequest);
        //searchRecordsResponseN.setPageNumber(searchRecordsRequest.getPageNumber());
        //return searchRecordsResponseN;
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

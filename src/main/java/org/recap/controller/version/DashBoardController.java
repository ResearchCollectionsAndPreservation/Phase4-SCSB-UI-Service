package org.recap.controller.version;

import org.json.JSONObject;
import org.recap.PropertyKeyConstants;
import org.recap.controller.ScsbController;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dinakarn on 27/01/21.
 */
@RestController
@RequestMapping("/validation")
public class DashBoardController extends ScsbController {

    private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);

    @Value("${" + PropertyKeyConstants.VERSION_NUMBER + "}")
    private String versionNumberService;

    @Value("${" + PropertyKeyConstants.SCSB_EMAIL_ASSIST_TO + "}")
    private String recapAssistanceEmailTo;

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * Gets the version number for scsb application which is configured in external application properties file.
     *
     * @return the version number service
     */
    @GetMapping("/getVersionNumberService")
    public String getVersionNumberService() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("versionNumber", versionNumberService);
        return jsonObject.toString();
    }

    /**
     * Sets version number service.
     *
     * @param versionNumberService the version number service
     */
    public void setVersionNumberService(String versionNumberService) {
        this.versionNumberService = versionNumberService;
    }

    @GetMapping("/getEmail")
    public String getEmail() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", recapAssistanceEmailTo);
        return jsonObject.toString();
    }

    /**
     * To get circulation freeze institutions with banner message
     *
     * @return list of banner messages
     */
    @GetMapping("/getFrozenInstitutions")
    public List<String> getFrozenInstitutions() {
        List<String> institutionWithBannerMessages = new ArrayList<>();
        Map<String, String> propertyMap = propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE);
        for (Map.Entry<String, String> mapEntry : propertyMap.entrySet()) {
            String institutionCode = mapEntry.getKey();
            String circulationFreezeValue = mapEntry.getValue();
            boolean isCirculationFreezeEnabled = Boolean.parseBoolean(circulationFreezeValue);
            if (isCirculationFreezeEnabled) {
                String institutionBannerMessage = propertyUtil.getPropertyByInstitutionAndKey(institutionCode, PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE);
                institutionWithBannerMessages.add(institutionBannerMessage);
            }
        }
        return institutionWithBannerMessages;
    }
}

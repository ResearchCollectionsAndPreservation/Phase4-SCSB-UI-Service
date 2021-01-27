package org.recap.controller.version;

import org.recap.RecapConstants;
import org.recap.controller.RecapController;
import org.recap.controller.SearchRecordsController;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by dinakarn on 27/01/21.
 */
@RestController
@RequestMapping("/validation")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class DashBoardController extends RecapController {

    private static final Logger logger = LoggerFactory.getLogger(SearchRecordsController.class);

    @Value("${version.number}")
    private String versionNumberService;
    /**
     * Sets version number service.
     *
     * @param versionNumberService the version number service
     */
    public void setVersionNumberService(String versionNumberService) {
        this.versionNumberService = versionNumberService;
    }

    /**
     * Gets the version number for scsb application which is configured in external application properties file.
     *
     * @return the version number service
     */
    @GetMapping("/getVersionNumberService")
    public String getVersionNumberService() {
        return versionNumberService;
    }

    @GetMapping("/checkPermission")
    public boolean searchRecords(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL);
        if (authenticated) {
            logger.info(RecapConstants.SEARCH_TAB_CLICKED);
            return RecapConstants.TRUE;
        } else {
            return UserManagementService.unAuthorizedUser(session, RecapConstants.SEARCH, logger);
        }
    }
}

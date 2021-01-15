package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.security.UserManagementService;
import org.recap.util.MonitoringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MonitoringController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);

    @Autowired
    private MonitoringUtil monitoringUtil;

    /**
     * Display All Monitoring url's
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/monitoring")
    public boolean monitoring(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_MONITORING_URL);
        if (authenticated) {
            return true;
        }
        return UserManagementService.unAuthorizedUser(session, "Monitoring", logger);
    }

    @GetMapping("/logging")
    public boolean logging(HttpServletRequest request) {
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_LOGGING_URL);
        HttpSession session = request.getSession(false);
        if (authenticated) {
            return true;
        }
        return UserManagementService.unAuthorizedUser(session, "Logging", logger);
    }
}
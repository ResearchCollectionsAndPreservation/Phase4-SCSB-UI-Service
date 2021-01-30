package org.recap.controller;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.filter.CsrfCookieGeneratorFilter;
import org.recap.filter.ReCAPInstitutionFilter;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.security.UserManagementService;
import org.recap.util.HelperUtil;
import org.recap.util.PropertyUtil;
import org.recap.util.ReportsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class HomeController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    private ReportsUtil reportsUtil;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserInstitutionCache userInstitutionCache;

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * @return InstitutionsList
     */
    @GetMapping("/institutions")
    public Map<String, String> loadInstitutions() {
        Map<String, String> instList = new LinkedHashMap<>();
        List<InstitutionEntity> InstitutionCodes = null;
        try {
            InstitutionCodes = institutionDetailsRepository.getInstitutionCodes();
        } catch (Exception e) {
            logger.info("Exception occured while pulling institutions from DB :: {}", e.getMessage());
        }
        for (InstitutionEntity institutionEntity : InstitutionCodes) {
            instList.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionName());
        }
        instList.put(RecapConstants.HTC, RecapConstants.HTC);
        logger.info("Institutions fetched from DB successfully :: {}", instList);
        return instList;
    }

    /**
     *
     */
    @GetMapping(value = "/loginCheck")
    public Map<String, Object> login(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        logger.info("session info :: {}", session.getId());
        Map<String, Object> resultMap = new HashMap<>();
        boolean isAuthenticated = false;
        try {
            isAuthenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL);
            resultMap.put(RecapConstants.REQUEST_PRIVILEGE, request.getSession().getAttribute(RecapConstants.REQUEST_PRIVILEGE));
            resultMap.put(RecapConstants.SEARCH_PRIVILEGE, request.getSession().getAttribute(RecapConstants.SEARCH_PRIVILEGE));
            resultMap.put(RecapConstants.COLLECTION_PRIVILEGE, request.getSession().getAttribute(RecapConstants.COLLECTION_PRIVILEGE));
            resultMap.put(RecapConstants.DEACCESSION_PRIVILEGE, request.getSession().getAttribute(RecapConstants.DEACCESSION_PRIVILEGE));
            resultMap.put(RecapConstants.REQUEST_ALL_PRIVILEGE, request.getSession().getAttribute(RecapConstants.REQUEST_ALL_PRIVILEGE));
            resultMap.put(RecapCommonConstants.BULK_REQUEST_PRIVILEGE, request.getSession().getAttribute(RecapCommonConstants.BULK_REQUEST_PRIVILEGE));
            resultMap.put(RecapConstants.REPORTS_PRIVILEGE, request.getSession().getAttribute(RecapConstants.REPORTS_PRIVILEGE));
            resultMap.put(RecapConstants.ROLE_FOR_SUPER_ADMIN, request.getSession().getAttribute(RecapConstants.ROLE_FOR_SUPER_ADMIN));
            resultMap.put(RecapConstants.USER_ROLE_PRIVILEGE, request.getSession().getAttribute(RecapConstants.USER_ROLE_PRIVILEGE));
            resultMap.put(RecapConstants.SUPER_ADMIN_USER, request.getSession().getAttribute(RecapConstants.SUPER_ADMIN_USER));
            resultMap.put(RecapConstants.MONITORING, request.getSession().getAttribute(RecapConstants.MONITORING));
            resultMap.put(RecapConstants.LOGGING, request.getSession().getAttribute(RecapConstants.LOGGING));
            resultMap.put(RecapConstants.DATA_EXPORT, request.getSession().getAttribute(RecapConstants.DATA_EXPORT));
            resultMap.put(RecapConstants.IS_AUTHENTICATED, isAuthenticated);
        } catch (Exception e) {
            logger.info("Exception Occured while User Validation :: {}", e.getMessage());
            isAuthenticated = UserManagementService.unAuthorizedUser(session, RecapConstants.LOGIN_USER, logger);
            resultMap.put(RecapConstants.IS_AUTHENTICATED, isAuthenticated);
        }
        return resultMap;
    }
}
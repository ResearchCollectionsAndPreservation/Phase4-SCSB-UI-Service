package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;
import org.recap.util.PropertyUtil;
import org.recap.util.ReportsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(allowCredentials="true",allowedHeaders = "*")
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
     *
     */
    @GetMapping("/authenticate")
    public void authenticate(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("authenticated", "true");
    }

    /**
     * @return InstitutionsList
     */
    @GetMapping("/institutions")
    public Map<String,String> loadInstitutions() {

        Map<String,String> instList = new HashMap<>();
        List<InstitutionEntity> InstitutionCodes = institutionDetailsRepository.getInstitutionCodes();
        for (InstitutionEntity institutionEntity : InstitutionCodes) {
            instList.put(institutionEntity.getInstitutionCode(),institutionEntity.getInstitutionName());
        }
        return instList;
    }

    /**
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/loginCheck")
    public boolean login(HttpServletRequest request) {
        boolean authenticated = false;
        try {
            authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL);
        } catch (Exception e) {
        }
        return authenticated;
    }

    /**
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public boolean logoutUser(HttpServletRequest request,HttpServletResponse response){
        logger.info("Subject Logged out");
        String institutionCode = HelperUtil.getInstitutionFromRequest(request);
        String requestedSessionId = request.getSession().getId();
        HttpSession session = null;
        try {
            session = request.getSession(false);
            getUserAuthUtil().authorizedUser(RecapConstants.SCSB_SHIRO_LOGOUT_URL, (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN));
        } finally {
            if (session != null) {
                Cookie[] cookies = request.getCookies();
                cookiesOuter:
                for (Cookie cookie : cookies) {
                    if (StringUtils.equals(cookie.getName(), RecapConstants.IS_USER_AUTHENTICATED) && StringUtils.equals(cookie.getValue(), "Y")) {
                        for (Cookie innerCookies : cookies) {
                            if (StringUtils.equals(innerCookies.getName(), RecapConstants.LOGGED_IN_INSTITUTION)) {
                                institutionCode = innerCookies.getValue();
                                cookie.setValue(null);
                                cookie.setMaxAge(0);
                                response.addCookie(cookie);

                                innerCookies.setValue(null);
                                innerCookies.setMaxAge(0);
                                response.addCookie(innerCookies);

                                break cookiesOuter;
                            }
                        }
                    }
                }
                userInstitutionCache.removeSessionId(requestedSessionId);
                session.invalidate();
            }
            return true;
        }
    }

    private HttpSession processSessionFixation(HttpServletRequest request) {

        String requestedSessionId = request.getSession().getId();

        String institutionCode = userInstitutionCache.getInstitutionForRequestSessionId(requestedSessionId);

        userInstitutionCache.removeSessionId(requestedSessionId);

        request.getSession(false).invalidate();
        HttpSession session = request.getSession(true);

        userInstitutionCache.addRequestSessionId(session.getId(), institutionCode);

        return session;
    }

    private void setSessionValues(HttpSession session, Map<String, Object> resultMap, UsernamePasswordToken token) {
        session.setAttribute(RecapConstants.USER_TOKEN, token);
        session.setAttribute(RecapConstants.USER_AUTH, resultMap);
        setValuesInSession(session, resultMap);
    }

    private void setValuesInSession(HttpSession session, Map<String, Object> authMap) {
        session.setAttribute("userName", authMap.get("userName"));
        session.setAttribute(RecapConstants.USER_ID, authMap.get(RecapConstants.USER_ID));
        session.setAttribute(RecapConstants.USER_INSTITUTION, authMap.get(RecapConstants.USER_INSTITUTION));
        session.setAttribute(RecapConstants.SUPER_ADMIN_USER, authMap.get(RecapConstants.SUPER_ADMIN_USER));
        session.setAttribute(RecapConstants.RECAP_USER, authMap.get(RecapConstants.RECAP_USER));
        session.setAttribute(RecapConstants.REQUEST_PRIVILEGE, authMap.get(RecapConstants.REQUEST_PRIVILEGE));
        session.setAttribute(RecapConstants.COLLECTION_PRIVILEGE, authMap.get(RecapConstants.COLLECTION_PRIVILEGE));
        session.setAttribute(RecapConstants.REPORTS_PRIVILEGE, authMap.get(RecapConstants.REPORTS_PRIVILEGE));
        session.setAttribute(RecapConstants.SEARCH_PRIVILEGE, authMap.get(RecapConstants.SEARCH_PRIVILEGE));
        session.setAttribute(RecapConstants.USER_ROLE_PRIVILEGE, authMap.get(RecapConstants.USER_ROLE_PRIVILEGE));
        session.setAttribute(RecapConstants.REQUEST_ALL_PRIVILEGE, authMap.get(RecapConstants.REQUEST_ALL_PRIVILEGE));
        session.setAttribute(RecapConstants.REQUEST_ITEM_PRIVILEGE, authMap.get(RecapConstants.REQUEST_ITEM_PRIVILEGE));
        session.setAttribute(RecapConstants.BARCODE_RESTRICTED_PRIVILEGE, authMap.get(RecapConstants.BARCODE_RESTRICTED_PRIVILEGE));
        session.setAttribute(RecapConstants.DEACCESSION_PRIVILEGE, authMap.get(RecapConstants.DEACCESSION_PRIVILEGE));
        session.setAttribute(RecapCommonConstants.BULK_REQUEST_PRIVILEGE, authMap.get(RecapCommonConstants.BULK_REQUEST_PRIVILEGE));
        session.setAttribute(RecapCommonConstants.RESUBMIT_REQUEST_PRIVILEGE, authMap.get(RecapCommonConstants.RESUBMIT_REQUEST_PRIVILEGE));
        session.setAttribute(RecapConstants.MONITORING, authMap.get(RecapConstants.MONITORING));
        session.setAttribute(RecapConstants.LOGGING, authMap.get(RecapConstants.LOGGING));
        Object isSuperAdmin = session.getAttribute(RecapConstants.SUPER_ADMIN_USER);
        if ((boolean) isSuperAdmin) {
            session.setAttribute(RecapConstants.ROLE_FOR_SUPER_ADMIN, true);
        } else {
            session.setAttribute(RecapConstants.ROLE_FOR_SUPER_ADMIN, false);
        }
    }
}
package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.usermanagement.LoginValidator;
import org.recap.model.usermanagement.UserForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * Created by dinakar on 23/12/20.
 */
@Controller
public class LoginController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String redirectSearch = "redirect:/search";
    private final LoginValidator loginValidator = new LoginValidator();

    @Value("${scsb.ui.url}")
    private String uiUrl;

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
     * @return
     */
    @GetMapping("/home")
    public String home(HttpServletRequest request){
        return "redirect:" + uiUrl + "home";
    }

    /**
     * Perform the SCSB authentication and authorization after user authenticated from partners IMS
     *
     * @param request the request
     * @return the view name
     */
    @GetMapping(value = "/login-scsb")
    public String login(HttpServletRequest request) {
        HttpSession session = processSessionFixation(request);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            String institutionFromRequest = request.getParameter("institution");
            String authType = propertyUtil.getPropertyByInstitutionAndKey(institutionFromRequest, "auth.type");
            if (StringUtils.equals(authType, RecapConstants.AUTH_TYPE_OAUTH)) {
                OAuth2Authentication oauth = (OAuth2Authentication) auth;
                String tokenString = ((OAuth2AuthenticationDetails) oauth.getDetails()).getTokenValue();
                OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenString);

                Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
                if (null != additionalInformation) {
                    username = (String) additionalInformation.get("sub");
                }
            }
            logger.info("passing in /login");
            UsernamePasswordToken token = new UsernamePasswordToken(username + RecapConstants.TOKEN_SPLITER + institutionFromRequest, "", true);
            Map<String, Object> resultMap = getUserAuthUtil().doAuthentication(token);
            if (!(Boolean) resultMap.get(RecapConstants.IS_USER_AUTHENTICATED)) {
                String errorMessage = (String) resultMap.get(RecapConstants.USER_AUTH_ERRORMSG);
                logger.error(RecapCommonConstants.LOG_ERROR + errorMessage);
                return "redirect:" + uiUrl + "home";
            }
            setSessionValues(session, resultMap, token);

        } catch (Exception exception) {
            logger.error(RecapCommonConstants.LOG_ERROR, exception);
            logger.error("Exception occurred in authentication : " + exception.getLocalizedMessage());
            return "redirect:" + uiUrl + "home";
        }
        return "redirect:" + uiUrl + "search";
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

    private void setSessionValues(HttpSession session, Map<String, Object> resultMap, UsernamePasswordToken token) {
        session.setAttribute(RecapConstants.USER_TOKEN, token);
        session.setAttribute(RecapConstants.USER_AUTH, resultMap);
        setValuesInSession(session, resultMap);
    }
}

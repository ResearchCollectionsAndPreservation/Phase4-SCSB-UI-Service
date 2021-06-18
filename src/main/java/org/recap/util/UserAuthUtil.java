package org.recap.util;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.service.RestHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmendrag on 4/1/17.
 */
@Service
public class UserAuthUtil {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthUtil.class);
    @Autowired
    RestHeaderService restHeaderService;
    @Value("${" + PropertyKeyConstants.SCSB_AUTH_URL + "}")
    private String scsbShiro;

    public RestHeaderService getRestHeaderService() {
        return restHeaderService;
    }

    /**
     * Authenticate the used based on the UsernamePasswordToken.
     *
     * @param token the token
     * @return the map of the authorization values
     * @throws Exception the exception
     */
    public Map<String, Object> doAuthentication(UsernamePasswordToken token) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UsernamePasswordToken> requestEntity = new HttpEntity<>(token, getRestHeaderService().getHttpHeaders());
        return restTemplate.postForObject(scsbShiro + ScsbConstants.SCSB_SHIRO_AUTHENTICATE_URL, requestEntity, HashMap.class);
    }

    /**
     * Authorize the login user.
     *
     * @param serviceURL the service url
     * @param token      the token
     * @return the boolean
     */
    public boolean authorizedUser(String serviceURL, UsernamePasswordToken token) {

        boolean statusResponse = false;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<UsernamePasswordToken> requestEntity = new HttpEntity<>(token, getRestHeaderService().getHttpHeaders());
            statusResponse = restTemplate.postForObject(scsbShiro + serviceURL, requestEntity, Boolean.class);
        } catch (Exception e) {
            if (serviceURL.contains(ScsbConstants.LOGOUT))
                logger.info(ScsbConstants.LOG_USER_LOGOUT_SUCCESS + " :: {}", token != null ? token.getUsername() : null);
        }
        return statusResponse;
    }

    /**
     * Gets logged-in user details.
     *
     * @param recapPermission the recap permission
     * @return the user details
     */
    public UserDetailsForm getUserDetails(HttpSession session, String recapPermission) {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin((Boolean) session.getAttribute(ScsbConstants.SUPER_ADMIN_USER));
        userDetailsForm.setRepositoryUser((Boolean) session.getAttribute(ScsbConstants.REPOSITORY));
        userDetailsForm.setLoginInstitutionId((Integer) session.getAttribute(ScsbConstants.USER_INSTITUTION));
        userDetailsForm.setRecapPermissionAllowed((Boolean) session.getAttribute(recapPermission));
        userDetailsForm.setUserAdministrator((Boolean) session.getAttribute(ScsbConstants.USER_ADMINISTRATOR));
        return userDetailsForm;
    }

    /**
     * Checks whether the user session is authenticated to support the request.
     *
     * @param httpSession The current session.
     * @param roleUrl     The role url.
     * @return <code>true</code> if the user is successfully authenticated and
     * <code>false</code> if not
     */
    public boolean isAuthenticated(HttpSession httpSession, String roleUrl) {
        return this.authorizedUser(roleUrl, (UsernamePasswordToken) httpSession.getAttribute(ScsbConstants.USER_TOKEN));
    }

    /**
     * Checks whether the request has a valid session to serve it..
     *
     * @param httpServletRequest The current request.
     * @param roleUrl            The role url.
     * @return <code>true</code> if the user is successfully authenticated and
     * <code>false</code> if not
     */
    public boolean isAuthenticated(HttpServletRequest httpServletRequest, String roleUrl) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        return this.isAuthenticated(httpSession, roleUrl);
    }


}

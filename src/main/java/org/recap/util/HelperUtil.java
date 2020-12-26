package org.recap.util;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.spring.PropertyValueProvider;
import org.recap.spring.SwaggerAPIProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by sheiks on 20/01/17.
 */
public class HelperUtil {

    private static final Logger logger = LoggerFactory.getLogger(HelperUtil.class);


    /**
     * Gets attribute value from request.
     *
     * @param request the request
     * @param key     the key
     * @return the attribute value of the given key
     */
    
    public static String getAttributeValueFromRequest(HttpServletRequest request, String key) {
        return (String) request.getAttribute(key);
    }

    /**
     * Gets bean for the given type.
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return the bean
     */
    public static <T> T getBean(Class<T> requiredType) {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    /**
     * Gets institution from request.
     *
     * @param request the request
     * @return the institution
     */
    public static String getInstitutionFromRequest(HttpServletRequest request) {
        String institution = request.getParameter("institution");
        if (StringUtils.isBlank(institution)) {
            institution = (String) request.getAttribute(RecapConstants.RECAP_INSTITUTION_CODE);
        }
        return institution;
    }

    /**
     * Gets logout url based on the loggined in institution.
     *
     * @param institutionCode the institution code
     * @return the logout url
     */
    public static String getLogoutUrl(String institutionCode) {
        String casLogoutUrl;
        String authType = HelperUtil.getBean(PropertyUtil.class).getPropertyByInstitutionAndKey(institutionCode, "auth.type");
        if (StringUtils.equals(authType, RecapConstants.AUTH_TYPE_OAUTH)) {
            casLogoutUrl = "http://localhost:9088/home"; // Todo : Need to get the corresponding logout url from NYPL
        } else {
            String urlProperty = "cas" + RecapConstants.SERVICE_LOGOUT;
            PropertyValueProvider propertyValueProvider = HelperUtil.getBean(PropertyValueProvider.class);
            String url = HelperUtil.getBean(PropertyUtil.class).getPropertyByInstitutionAndKey(institutionCode,urlProperty);
            String redirectUri = propertyValueProvider.getProperty("scsb.app"+RecapConstants.REDIRECT_URI);
            casLogoutUrl = "http://localhost:9088/home";
        }
        return casLogoutUrl;
    }

    /**
     * Logout from shiro.
     *
     * @param attribute the attribute
     */
    public static void logoutFromShiro(Object attribute) {
        if (null != attribute) {
            try {
                UserAuthUtil userAuthUtil = HelperUtil.getBean(UserAuthUtil.class);
                userAuthUtil.authorizedUser(RecapConstants.SCSB_SHIRO_LOGOUT_URL, (UsernamePasswordToken) attribute);
            } catch (Exception e) {
                logger.error("Logout", e);
            }
        }
    }

    /**
     * find ss anonymous user logged in.
     *
     * @param auth the auth
     * @return the boolean
     */
    public static boolean isAnonymousUser(Authentication auth) {
        if (StringUtils.equals(auth.getName(), RecapConstants.ANONYMOUS_USER)) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (Iterator<? extends GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext(); ) {
                GrantedAuthority grantedAuthority = iterator.next();
                if (StringUtils.equals(grantedAuthority.getAuthority(), RecapConstants.ROLE_ANONYMOUS)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static HttpHeaders getSwaggerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(RecapCommonConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }

    public static byte[] getFileContent(File csvFile, String fileNameWithExtension, String templateName) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(csvFile));
    }

    public static void setCookieProperties(Cookie cookie) {
        cookie.setMaxAge(-1);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
    }
}


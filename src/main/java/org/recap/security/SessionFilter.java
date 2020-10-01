package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.util.HelperUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by dharmendrag on 11/1/17.
 */
@ComponentScan
public class SessionFilter implements Filter{


    UserAuthUtil userAuthUtil;

    //private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Do nothing
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        SecurityContext context = SecurityContextHolder.getContext();
        if(null != context) {
            Authentication authentication = context.getAuthentication();
            if(null != authentication && !HelperUtil.isAnonymousUser(authentication)) {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                Cookie cookie = new Cookie(RecapConstants.IS_USER_AUTHENTICATED, "Y");
                HelperUtil.setCookieProperties(cookie);
                response.addCookie(cookie);

                UserInstitutionCache userInstitutionCache = HelperUtil.getBean(UserInstitutionCache.class);

                String requestedSessionId = request.getSession().getId();

                String institutionCode = userInstitutionCache.getInstitutionForRequestSessionId(requestedSessionId);

                Cookie institutionCodeCookies = new Cookie(RecapConstants.LOGGED_IN_INSTITUTION, institutionCode);
                HelperUtil.setCookieProperties(institutionCodeCookies);
                response.addCookie(institutionCodeCookies);

                HttpSession session=request.getSession(false);
                UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN);
                if(usernamePasswordToken != null) {
                    getUserAuthUtil().authorizedUser(RecapConstants.SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL, usernamePasswordToken);
                }

            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        //Do nothing
    }

    public UserAuthUtil getUserAuthUtil() {
        if(userAuthUtil == null) {
            userAuthUtil = ApplicationContextProvider.getInstance().getApplicationContext().getBean(UserAuthUtil.class);
        }
        return userAuthUtil;
    }
}

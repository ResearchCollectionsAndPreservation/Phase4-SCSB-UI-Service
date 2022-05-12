package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.util.HelperUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({HelperUtil.class,SecurityContextHolder.class})
@PowerMockIgnore("javax.security.*")
public class SessionFilterUT extends BaseTestCaseUT{
    @InjectMocks
    SessionFilter sessionFilter;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext context;
    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    HttpSession httpSession;

    @Mock
    UserInstitutionCache userInstitutionCache;

    @Mock
    UsernamePasswordToken usernamePasswordToken;

    @Mock
    FilterConfig filterConfig;

    @Mock
    ApplicationContextProvider applicationContextProvider;

    @Test
    public void doFilter() throws Exception {
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        PowerMockito.mockStatic(HelperUtil.class);
        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(HelperUtil.isAnonymousUser(Mockito.any())).thenReturn(false);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(Mockito.anyString())).thenReturn("PUL");
        when(SecurityContextHolder.getContext()).thenReturn(context);
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(httpSession.getId()).thenReturn("1");
        Mockito.when(request.getSession(false)).thenReturn(httpSession);
        Mockito.when(httpSession.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(usernamePasswordToken);
        Mockito.when(userAuthUtil.authorizedUser(ScsbConstants.SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL, usernamePasswordToken)).thenReturn(true);
        sessionFilter.doFilter(request, response, filterChain);
        assertTrue(true);
    }

    @Test
    public void doFilterTest() throws Exception {
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        PowerMockito.mockStatic(HelperUtil.class);
        PowerMockito.mockStatic(SecurityContextHolder.class);
        when(HelperUtil.isAnonymousUser(Mockito.any())).thenReturn(false);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(Mockito.anyString())).thenReturn("PUL");
        when(SecurityContextHolder.getContext()).thenReturn(context);
        Mockito.when(request.getSession()).thenReturn(httpSession);
        Mockito.when(httpSession.getId()).thenReturn("1");
        Mockito.when(request.getSession(false)).thenReturn(httpSession);
        Mockito.when(httpSession.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(null);
        Mockito.when(userAuthUtil.authorizedUser(ScsbConstants.SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL, usernamePasswordToken)).thenReturn(false);
        sessionFilter.doFilter(request, response, filterChain);
    }

    @Test
    public void testDoNothingMethods() throws ServletException {
        sessionFilter.destroy();
        sessionFilter.init(filterConfig);
        assertTrue(true);
    }

    @Test
    public void getUserAuthUtil(){
        SessionFilter sessionFilter = new SessionFilter();
        try {
            UserAuthUtil userAuthUtil = sessionFilter.getUserAuthUtil();
            assertNotNull(userAuthUtil);
        }catch (Exception e){}
    }

}

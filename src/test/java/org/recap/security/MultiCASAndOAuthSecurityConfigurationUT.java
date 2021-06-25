package org.recap.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.filter.SCSBLogoutFilter;
import org.recap.filter.SCSBValidationFilter;
import org.recap.util.UserAuthUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class MultiCASAndOAuthSecurityConfigurationUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    MultiCASAndOAuthSecurityConfiguration multiCASAndOAuthSecurityConfiguration;

    @Mock
    CASPropertyProvider casPropertyProvider;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    ObjectPostProcessor<Object> objectPostProcessor;

    @Mock
    Map<Class<?>, Object> sharedObjects;

    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    AuthenticationConfiguration authenticationConfiguration;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"casServiceLogout","https://test");
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"casUrlPrefix","test");
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"appServiceLogout","test");
    }

    @Test
    public void requestCasGlobalLogoutFilter(){
        LogoutFilter logoutFilter = multiCASAndOAuthSecurityConfiguration.requestCasGlobalLogoutFilter();
        assertNotNull(logoutFilter);
    }

    @Test
    public void casAuthenticationFilter() throws Exception {
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"localConfigureAuthenticationBldr",authenticationManagerBuilder);
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"disableLocalConfigureAuthenticationBldr",Boolean.TRUE);
        ReflectionTestUtils.setField(multiCASAndOAuthSecurityConfiguration,"authenticationConfiguration",authenticationConfiguration);
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        CasAuthenticationFilter casAuthenticationFilter = multiCASAndOAuthSecurityConfiguration.casAuthenticationFilter();
        assertNotNull(casAuthenticationFilter);
    }

    @Test
    public void filterRegistrationBean(){
        FilterRegistrationBean<SCSBValidationFilter> filterRegistrationBean = multiCASAndOAuthSecurityConfiguration.filterRegistrationBean();
        assertNotNull(filterRegistrationBean);
    }

    @Test
    public void reCAPLogoutFilter(){
        SCSBLogoutFilter scsbLogoutFilter =  multiCASAndOAuthSecurityConfiguration.reCAPLogoutFilter();
        assertNotNull(scsbLogoutFilter);
    }

    @Test
    public void configure() throws Exception {
        WebSecurity webSecurity = new WebSecurity(objectPostProcessor);
        try {
            multiCASAndOAuthSecurityConfiguration.configure(webSecurity);
        }catch (Exception e){}
    }

    @Test
    public void httpSessionEventPublisher(){
        SCSBHttpSessionEventPublisher scsbHttpSessionEventPublisher = multiCASAndOAuthSecurityConfiguration.httpSessionEventPublisher();
        assertNotNull(scsbHttpSessionEventPublisher);
    }

    @Test
    public void corsConfigurer(){
        WebMvcConfigurer webMvcConfigurer = multiCASAndOAuthSecurityConfiguration.corsConfigurer();
        assertNotNull(webMvcConfigurer);
    }
}

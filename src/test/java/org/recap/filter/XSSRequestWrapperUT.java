package org.recap.filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

public class XSSRequestWrapperUT extends BaseTestCaseUT {

    @InjectMocks
    XSSRequestWrapper xssRequestWrapper;

    @Mock
    ServletRequest request;

    @Mock
    HttpSession httpSession;

    @Mock
    HttpServletRequest servletRequest;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(xssRequestWrapper,"request",request);
    }

    @Test
    public void testGetParameterValues() throws Exception{
        String[] values;
        String parameter = "+-0123456789#";
        values = xssRequestWrapper.getParameterValues(parameter);
        assertNull(values);
    }

    @Test
    public  void getParameterValues(){
        String[] Strvalues;
        String[] values = {"PD","PG"};
        String parameter = "+-0123456789#";
        Mockito.when(request.getParameterValues(any())).thenReturn(values);
        Strvalues = xssRequestWrapper.getParameterValues(parameter);
        assertNotNull(Strvalues);
    }
    @Test
    public void testGetHeaders() throws Exception{
        String name = "test";
        String value = xssRequestWrapper.getHeader(name);
        assertNull(value);
    }

    @Test
    public void testGetParameter() throws Exception{
        String parameter = "test";
        String value = xssRequestWrapper.getParameter(parameter);
        assertNull(value);
    }

}

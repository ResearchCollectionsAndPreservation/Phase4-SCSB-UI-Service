package org.recap.security;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.ScsbConstants;
import org.recap.util.HelperUtil;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class SCSBHttpSessionEventPublisherUT extends BaseTestCase {

    @Mock
    HttpSessionEvent httpSessionEvent;
    @Mock
    HttpSession session;

    @Mock
    HelperUtil helperUtil;

    Map<String, Object> attributes = new HashMap<>();

    String attribute = ScsbConstants.USER_TOKEN;
    @Test
    public void testSessionDestroyed() {
        SCSBHttpSessionEventPublisher publisher = new SCSBHttpSessionEventPublisher();
        try {
            attributes.put("1", "token");
            when(session.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(attributes);
            when(httpSessionEvent.getSession()).thenReturn(session);
            helperUtil.logoutFromShiro(attribute);
            publisher.sessionDestroyed(httpSessionEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

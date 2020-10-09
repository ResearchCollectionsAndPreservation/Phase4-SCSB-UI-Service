package org.recap.security;

import org.recap.RecapConstants;
import org.recap.util.HelperUtil;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;

/**
 * Created by sheiks on 27/03/17.
 */
public class ReCAPHttpSessionEventPublisher extends HttpSessionEventPublisher {
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        Object attribute = event.getSession().getAttribute(RecapConstants.USER_TOKEN);
        HelperUtil.logoutFromShiro(attribute);
        super.sessionDestroyed(event);
    }
}

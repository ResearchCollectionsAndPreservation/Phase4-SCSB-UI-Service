package org.recap.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.stereotype.Component;

/**
 * Created by sheiks on 30/01/17.
 */
@Component
public class CASPropertyProvider {

    @Value("${app.service.security}")
    private String security;

    @Value("${app.service.home}")
    private String home;

    /**
     * Gets security.
     *
     * @return the security
     */
    public String getSecurity() {
        return security;
    }

    /**
     * Sets security.
     *
     * @param security the security
     */
    public void setSecurity(String security) {
        this.security = security;
    }

    /**
     * Gets home.
     *
     * @return the home
     */
    public String getHome() {
        return home;
    }

    /**
     * Sets home.
     *
     * @param home the home
     */
    public void setHome(String home) {
        this.home = home;
    }

    /**
     * Gets service properties.
     *
     * @return the service properties
     */
    public ServiceProperties getServiceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(home + "/" + security);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }
}

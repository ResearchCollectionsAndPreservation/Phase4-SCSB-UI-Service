package org.recap.config;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import java.util.HashMap;

public class MonitoringConfigurationUT extends BaseTestCaseUT {

    @InjectMocks
    MonitoringConfiguration monitoringConfiguration;

    @Test
    public void logging(){
        monitoringConfiguration.setUrl(new HashMap<>());
        monitoringConfiguration.getUrl();
        MonitoringConfiguration.Logging logging = new MonitoringConfiguration.Logging();
    }
}

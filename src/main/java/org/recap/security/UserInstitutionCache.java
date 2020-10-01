package org.recap.security;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sheiks on 24/03/17.
 */
@Component
public class UserInstitutionCache {

    private Map<String, String> sessionIdAndInstitutionType = new HashMap<>();

    public void addRequestSessionId(String key, String value) {
        sessionIdAndInstitutionType.put(key, value);
    }

    public String getInstitutionForRequestSessionId(String key) {
        return sessionIdAndInstitutionType.get(key);
    }

    public void removeSessionId(String key) {
        sessionIdAndInstitutionType.remove(key);
    }

}

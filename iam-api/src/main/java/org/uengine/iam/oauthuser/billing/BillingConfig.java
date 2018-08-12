package org.uengine.iam.oauthuser.billing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConfigurationProperties(prefix = "iam.billing")
public class BillingConfig {
    private String url;
    private String authentication;
    private String organization;
    private boolean accountSync;
    private Map<String, String> accountSyncBody;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isAccountSync() {
        return accountSync;
    }

    public void setAccountSync(boolean accountSync) {
        this.accountSync = accountSync;
    }

    public Map<String, String> getAccountSyncBody() {
        return accountSyncBody;
    }

    public void setAccountSyncBody(Map<String, String> accountSyncBody) {
        this.accountSyncBody = accountSyncBody;
    }
}

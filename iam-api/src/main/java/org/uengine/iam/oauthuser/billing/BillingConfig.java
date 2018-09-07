package org.uengine.iam.oauthuser.billing;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Data
@Service
@ConfigurationProperties(prefix = "iam.billing")
public class BillingConfig {
    private String url;
    private String authentication;
    private String organization;
    private boolean accountSync;
    private Map<String, String> accountSyncBody;
}

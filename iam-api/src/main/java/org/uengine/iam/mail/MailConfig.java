package org.uengine.iam.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by uengine on 2017. 12. 30..
 */
@Data
@Component
@ConfigurationProperties(prefix = "iam.mail")
public class MailConfig {
    private String host;
    private String username;
    private String password;
    private int port;
    private boolean smtpAuth;
    private boolean smtpStarttlsEnable;
    private String fromAddress;
    private String fromName;
}

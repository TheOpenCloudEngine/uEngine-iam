package org.uengine.iam.notification;

import lombok.Data;

/**
 * Created by uengine on 2017. 1. 25..
 */
@Data
public class Template {

    private String clientKey;
    private NotificationType notification_type;
    private String locale;
    private String subject;
    private String body;
}

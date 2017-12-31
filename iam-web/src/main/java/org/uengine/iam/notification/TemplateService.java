package org.uengine.iam.notification;

import java.util.Map;

public interface TemplateService {

    Map<String, Template> selectByClientKey(String clientKey) throws Exception;

    Template selectByClientKeyAndType(String clientKey, NotificationType notification_type) throws Exception;
}

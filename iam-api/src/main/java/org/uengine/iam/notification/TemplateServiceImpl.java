package org.uengine.iam.notification;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.uengine.iam.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Service
public class TemplateServiceImpl implements TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateService.class);

    /**
     * 클라이언트의 이메일 템플릿들을 가져온다.
     *
     * @param clientKey
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Template> selectByClientKey(String clientKey) throws Exception {
        Map<String, Template> map = new HashMap();
        NotificationType[] values = NotificationType.values();
        for (NotificationType value : values) {
            map.put(value.toString(), this.getTemplateFromResource(clientKey, value));
        }
        return map;
    }

    /**
     * 클라이언트의 주어진 노티 이벤트에 해당하는 이메일 템플릿을 가져온다.
     *
     * @param clientKey
     * @param notification_type
     * @return
     * @throws Exception
     */
    @Override
    public Template selectByClientKeyAndType(String clientKey, NotificationType notification_type) throws Exception {
        return this.getTemplateFromResource(clientKey, notification_type);
    }

    private String getContentFromResource(String resourcePath) throws IOException {
        InputStream in = getClass().getResourceAsStream(resourcePath);
        StringWriter writer = new StringWriter();
        String encoding = StandardCharsets.UTF_8.name();
        IOUtils.copy(in, writer, encoding);
        return writer.toString();
    }

    /**
     * 리소스폴더로부터 이메일 템플릿을 가져온다.
     *
     * @param clientKey
     * @param notificationType
     * @return
     * @throws Exception
     */
    private Template getTemplateFromResource(String clientKey, NotificationType notificationType) throws Exception {
        String templateDir = "/notification/" + clientKey + "/";
        String defaultDir = "/notification/default/";

        //예외의 경우 default 폴더의 템플릿을 가져오도록 한다.
        Template template = new Template();
        template.setClientKey(clientKey);
        template.setNotification_type(notificationType);

        try {
            String subjectUrl = templateDir + notificationType.toString() + "-subject.txt";
            String subject = getContentFromResource(subjectUrl);
            template.setSubject(subject);
        } catch (Exception ex) {
            String subjectUrl = defaultDir + notificationType.toString() + "-subject.txt";
            String subject = getContentFromResource(subjectUrl);
            template.setSubject(subject);
        }

        try {
            String bodyUrl = templateDir + notificationType.toString() + "-body.html";
            String body = getContentFromResource(bodyUrl);
            template.setBody(body);
        } catch (Exception ex) {
            String bodyUrl = defaultDir + notificationType.toString() + "-body.html";
            String body = getContentFromResource(bodyUrl);
            template.setBody(body);
        }

        return template;
    }
}

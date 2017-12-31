package org.uengine.iam.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.uengine.iam.util.ResourceUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

@Service
public class TemplateServiceImpl implements TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateService.class);

    /**
     * 클라이언트의 이메일 템플릿들을 가져온다.
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
     * @param clientKey
     * @param notification_type
     * @return
     * @throws Exception
     */
    @Override
    public Template selectByClientKeyAndType(String clientKey, NotificationType notification_type) throws Exception {
        return this.getTemplateFromResource(clientKey, notification_type);
    }

    /**
     * 리소스폴더로부터 이메일 템플릿을 가져온다.
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
            URL subjectUrl = getClass().getResource(templateDir + notificationType.toString() + "-subject.txt");
            File subjectFile = ResourceUtils.getFile(subjectUrl);
            String subject = new String(Files.readAllBytes(subjectFile.toPath()));
            template.setSubject(subject);
        } catch (Exception ex) {
            URL subjectUrl = getClass().getResource(defaultDir + notificationType.toString() + "-subject.txt");
            File subjectFile = ResourceUtils.getFile(subjectUrl);
            String subject = new String(Files.readAllBytes(subjectFile.toPath()));
            template.setSubject(subject);
        }

        try {
            URL bodyUrl = getClass().getResource(templateDir + notificationType.toString() + "-body.html");
            File bodyFile = ResourceUtils.getFile(bodyUrl);
            String body = new String(Files.readAllBytes(bodyFile.toPath()));
            template.setBody(body);
        } catch (Exception ex) {
            URL bodyUrl = getClass().getResource(defaultDir + notificationType.toString() + "-body.html");
            File bodyFile = ResourceUtils.getFile(bodyUrl);
            String body = new String(Files.readAllBytes(bodyFile.toPath()));
            template.setBody(body);
        }

        return template;
    }
}

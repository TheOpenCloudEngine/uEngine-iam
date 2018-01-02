package org.uengine.iam.mail;

import com.samskivert.mustache.Mustache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.notification.Template;
import org.uengine.iam.notification.TemplateService;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.scheduler.JobScheduler;
import org.uengine.iam.util.ServiceException;

import java.net.URL;
import java.util.*;


@Service
public class MailServiceImpl implements MailService {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private TemplateService templateService;

    @Autowired
    private JobScheduler jobScheduler;

    @Override
    public void notificationMail(OauthClient oauthClient, OauthUser oauthUser, NotificationType notificationType, String redirect_url, String token) throws Exception {

        //클라이언트에 노티타입이 허용인지 확인한다.
        String[] notification = oauthClient.getNotification();
        boolean enableNotification = false;
        if (Arrays.asList(notification).contains("ALL")) {
            enableNotification = true;
        } else if (Arrays.asList(notification).contains(notificationType.toString())) {
            enableNotification = true;
        }
        if (!enableNotification) {
            return;
        }

        //이메일이 없다면 에러.
        Map<String, Object> metaData = oauthUser.getMetaData();
        if (!metaData.containsKey("email")) {
            throw new ServiceException("OauthUser email field is required. " + oauthUser.getUserName());
        }
        String email = metaData.get("email").toString();

        //템플릿 가져오기
        Template template = templateService.selectByClientKeyAndType(oauthClient.getClientKey(), notificationType);

        //템플릿을 찾지 못했다면 에러
        if (template == null) {
            throw new ServiceException("Failed to find notification template for " + notificationType.toString());
        }

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("client", oauthClient);
        model.put("user", oauthUser);

        //SIGN_UP 이거나 FORGOT_PASSWORD 일 경우 link 를 생성한다.
        if (notificationType.equals(NotificationType.SIGN_UP)
                || notificationType.equals(NotificationType.FORGOT_PASSWORD)) {

            String query = new URL(redirect_url).getQuery();
            if (StringUtils.isEmpty(query)) {
                model.put("link", MessageFormatter.arrayFormat(redirect_url + "?token={}", new Object[]{token}).getMessage());
            } else {
                model.put("link", MessageFormatter.arrayFormat(redirect_url + "&token={}", new Object[]{token}).getMessage());
            }
        }

        String body = executeTemplateText(template.getBody(), model);
        String subject = executeTemplateText(template.getSubject(), model);

        Map map = new HashMap();
        map.put("subject", subject);
        map.put("body", body);
        map.put("toUser", email);

        jobScheduler.startJobImmediatly(UUID.randomUUID().toString(), "notificationMail", map);
    }

    public String executeTemplateText(final String templateText, final Map<String, Object> data) {
        final com.samskivert.mustache.Template template = Mustache.compiler().nullValue("").compile(templateText);
        return template.execute(data);
    }
}
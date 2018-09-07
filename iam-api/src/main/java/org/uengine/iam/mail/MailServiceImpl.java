package org.uengine.iam.mail;

import com.samskivert.mustache.Mustache;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.uengine.iam.notification.NotificationType;
import org.uengine.iam.notification.Template;
import org.uengine.iam.notification.TemplateService;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthuser.OauthUser;
import org.uengine.iam.util.ApplicationContextRegistry;
import org.uengine.iam.util.ServiceException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintStream;
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
    private MailConfig mailConfig;

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
        String toUser = metaData.get("email").toString();

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

        this.sendMail(body, subject, toUser);
    }

    public String executeTemplateText(final String templateText, final Map<String, Object> data) {
        final com.samskivert.mustache.Template template = Mustache.compiler().nullValue("").compile(templateText);
        return template.execute(data);
    }


    @Async
    public void sendMail(String body, String subject, String toUser) {

        Session session = this.setMailProperties(toUser);
        try {
            InternetAddress from = new InternetAddress(mailConfig.getFromAddress(), mailConfig.getFromName());
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toUser));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);

            logger.info("{} 메일주소로 메일을 발송했습니다.", toUser);
        } catch (Exception e) {
            throw new ServiceException("메일을 발송할 수 없습니다.", e);
        }
    }

    private Session setMailProperties(final String toUser) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", mailConfig.isSmtpAuth() + "");
        props.put("mail.smtp.starttls.enable", mailConfig.isSmtpStarttlsEnable() + "");
        props.put("mail.smtp.host", mailConfig.getHost());
        props.put("mail.smtp.port", mailConfig.getPort());
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");

        LogOutputStream loggerToStdOut = new LogOutputStream() {
            @Override
            protected void processLine(String line, int level) {
                logger.debug("[JavaMail] [{}] {}", toUser, line);
            }
        };

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailConfig.getUsername(), mailConfig.getPassword());
                    }
                }
        );
        session.setDebug(true);
        session.setDebugOut(new PrintStream(loggerToStdOut));
        return session;
    }
}
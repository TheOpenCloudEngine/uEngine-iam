/**
 * Copyright (C) 2011 Flamingo Project (http://www.cloudine.io).
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.uengine.iam.scheduler;

import org.apache.commons.exec.LogOutputStream;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.uengine.iam.mail.MailConfig;
import org.uengine.iam.util.ApplicationContextRegistry;
import org.uengine.iam.util.ServiceException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MailJob implements Job {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(MailJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 클라이언트 잡 실행시 필요한 정보를 가져온다.
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();

        ApplicationContext applicationContext = ApplicationContextRegistry.getApplicationContext();
        Environment environment = applicationContext.getBean(Environment.class);
        MailConfig mailConfig = applicationContext.getBean(MailConfig.class);

        String subject = map.get("subject").toString();
        String body = map.get("body").toString();
        String toUser = map.get("toUser").toString();

        Session session = this.setMailProperties(mailConfig, toUser);

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

    private Session setMailProperties(final MailConfig mailConfig, final String toUser) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", mailConfig.getSmtpAuth() + "");
        props.put("mail.smtp.starttls.enable", mailConfig.getSmtpStarttlsEnable() + "");
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

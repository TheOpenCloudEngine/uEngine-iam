package org.uengine.iam.scheduler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthtoken.OauthTokenRepository;
import org.uengine.iam.util.ServiceException;

import java.util.Date;
import java.util.List;

/**
 * Created by uengine on 2017. 12. 4..
 */
@Component
public class CronTable implements InitializingBean {

    @Autowired
    private Environment environment;

    @Autowired
    OauthTokenRepository tokenRepository;

    @Autowired
    OauthClientService clientService;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    // 애플리케이션 시작 후 10초 후에 첫 실행, 그 후 매 10초마다 주기적으로 실행한다.
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void removeExpiredToken() throws Exception {
        try {
            List<OauthClient> clients = clientService.selectAll();
            for (OauthClient client : clients) {
                if (client.getAutoDeletionToken()) {
                    long nowTime = new Date().getTime();
                    long idleTime = 60; //sec
                    Long refreshTokenLifetime = client.getRefreshTokenLifetime();
                    Long accessTokenLifetime = client.getAccessTokenLifetime();
                    Long expirationTime = nowTime - (accessTokenLifetime * 1000) - (refreshTokenLifetime * 1000) - (idleTime * 1000);
                    tokenRepository.deleteExpiredToken(client.getClientKey(), expirationTime);
                }
            }
            System.out.println("RemoveExpiredToken!!");

        } catch (Exception ex) {
            throw new ServiceException("Unable to run removeExpiredToken job", ex);
        }
    }
}

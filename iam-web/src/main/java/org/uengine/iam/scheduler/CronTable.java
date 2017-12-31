package org.uengine.iam.scheduler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by uengine on 2017. 12. 4..
 */
@Component
public class CronTable implements InitializingBean {

    @Autowired
    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    // 애플리케이션 시작 후 1초 후에 첫 실행, 그 후 매 2초마다 주기적으로 실행한다.
    @Scheduled(initialDelay = 1000, fixedDelay = 2000)
    public void fetchDcosData() throws Exception {

    }

    // 애플리케이션 시작 후 10초 후에 첫 실행, 그 후 매 10초마다 주기적으로 실행한다.
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void removeSshContainer() throws Exception {

    }
}

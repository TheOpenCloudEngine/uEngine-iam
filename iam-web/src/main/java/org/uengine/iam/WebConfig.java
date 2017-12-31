package org.uengine.iam;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;
import org.uengine.iam.oauthuser.OauthUserRepository;
import org.uengine.iam.security.AESPasswordEncoder;
import org.uengine.iam.security.CorsFilter;
import org.uengine.iam.util.ApplicationContextRegistry;

/**
 * Created by uengine on 2017. 11. 1..
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment environment;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        configurer.setUrlPathHelper(urlPathHelper);
        configurer.setUseRegisteredSuffixPatternMatch(true);
    }


    @Bean
    public AESPasswordEncoder passwordEncoder() {
        AESPasswordEncoder passwordEncoder = new AESPasswordEncoder();
        passwordEncoder.setSecretKey1(environment.getProperty("security.password.encoder.secret1"));
        passwordEncoder.setSecretKey2(environment.getProperty("security.password.encoder.secret2"));
        return passwordEncoder;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public OauthUserRepository oauthUserRepository() throws Exception {
        try {
            Class<?> repository = Class.forName(environment.getProperty("user-provider.interface"));
            return (OauthUserRepository) repository.newInstance();
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    @Bean
    public ApplicationContextRegistry applicationContextRegistry() {
        ApplicationContextRegistry contextRegistry = new ApplicationContextRegistry();
        contextRegistry.setApplicationContext(applicationContext);
        return contextRegistry;
    }
}

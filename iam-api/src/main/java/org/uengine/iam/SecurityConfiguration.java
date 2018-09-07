package org.uengine.iam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;
import org.uengine.iam.oauth2.CustomAuthenticationSuccessHandler;
import org.uengine.iam.oauth2.OauthService;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.oauthclient.SocialClientResources;
import org.uengine.iam.oauthregist.OauthRegistService;
import org.uengine.iam.oauthuser.OauthUserRepository;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uengine on 2017. 11. 15..
 */
//@EnableWebSecurity
@EnableOAuth2Client
@Configuration
@EnableAuthorizationServer
@Order(200)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;
    @Autowired
    private Environment environment;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private OauthClientService clientService;
    @Autowired
    private OauthUserRepository userRepository;
    @Autowired
    private OauthRegistService registService;

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }
    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
    return new RequestContextListener();
}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
            .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
        ;
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(github(), "/login/github"));
        filters.add(ssoFilter(google(), "/login/google"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(SocialClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setSocialClientResources(client);
        customAuthenticationSuccessHandler.setEnvironment(environment);
        customAuthenticationSuccessHandler.setOauthService(oauthService);
        customAuthenticationSuccessHandler.setClientService(clientService);
        customAuthenticationSuccessHandler.setRegistService(registService);
        customAuthenticationSuccessHandler.setUserRepository(userRepository);

        filter.setAuthenticationSuccessHandler( customAuthenticationSuccessHandler);
        return filter;
    }

    @Bean
    @ConfigurationProperties("facebook")
    public SocialClientResources facebook() {
        return new SocialClientResources();
    }

    @Bean
    @ConfigurationProperties("github")
    public SocialClientResources github() {
        return new SocialClientResources();
    }

    @Bean
    @ConfigurationProperties("google")
    public SocialClientResources google() {
        return new SocialClientResources();
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }


}

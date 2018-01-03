package org.uengine.iam.security;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.uengine.iam.oauthclient.OauthClient;
import org.uengine.iam.oauthclient.OauthClientService;
import org.uengine.iam.util.ApplicationContextRegistry;
import org.uengine.iam.util.ServiceException;
import org.uengine.iam.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by uengine on 2016. 4. 22..
 */
@WebFilter
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            this.addCors((HttpServletResponse) res);
            chain.doFilter(req, res);
        }
        //avatar 얻기는 통과
        else if (request.getRequestURI().startsWith("/rest/v1/avatar") && request.getMethod().equals(HttpMethod.GET.toString())) {
            this.addCors((HttpServletResponse) res);
            chain.doFilter(req, res);
        }
        //rest api 는 검증
        else if (request.getRequestURI().startsWith("/rest/v1")) {
            boolean enable = false;
            Environment environment = ApplicationContextRegistry.getApplicationContext().getBean(Environment.class);
            OauthClientService clientService = ApplicationContextRegistry.getApplicationContext().getBean(OauthClientService.class);
            String adminUsername = environment.getProperty("iam.admin.username");
            String adminPassword = environment.getProperty("iam.admin.password");

            Map<String, String> headers = this.getHeaders(request);
            String clientKey = headers.get("client-key");
            String clientSecret = headers.get("client-secret");

            //필요 헤더가 없으면 인증 불가.
            if (StringUtils.isEmpty(clientKey) || StringUtils.isEmpty(clientSecret)) {
                enable = false;
            }
            //어드민으로 권한 파악.
            else if (adminUsername.equals(clientKey) && adminPassword.equals(clientSecret)) {
                enable = true;
            }
            //클라이언트로 권한 파악
            else {
                OauthClient client = clientService.selectByClientKey(clientKey);
                if (client != null && client.getAccessRestEnable() && client.getClientSecret().equals(clientSecret)) {
                    enable = true;
                }
            }
            if (enable) {
                this.addCors((HttpServletResponse) res);
                chain.doFilter(req, res);
            } else {
                this.addCors((HttpServletResponse) res);
                response.setStatus(401);
            }
        } else {
            this.addCors((HttpServletResponse) res);
            chain.doFilter(req, res);
        }
    }

    private void addCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept, " +
                "management-key, management-secret, client-key, client-secret, authorization, Location, access_token, PRIVATE-TOKEN");
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        List<String> list = Collections.list(headerNames);
        Map<String, String> map = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i);
            map.put(name.toLowerCase(), request.getHeader(name));
        }
        return map;
    }

    @Override
    public void destroy() {

    }
}

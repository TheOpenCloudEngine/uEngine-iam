package org.uengine.iam.oauthclient;


import lombok.Data;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Data
public class OauthClient {

    private String name;
    private String description;
    private String clientKey;
    private String clientSecret;
    private String[] enableScopes;
    private boolean accessRestEnable;
    private boolean activeClient;
    private String[] authorizedGrantTypes;
    private String webServerRedirectUri;
    private boolean refreshTokenValidity;
    private boolean autoDeletionToken;
    private String[] requiredContext;
    private String jwtAlgorithm;
    private Long codeLifetime;
    private Long refreshTokenLifetime;
    private Long accessTokenLifetime;
    private String[] notification;
    private boolean userScopeCheckAll;
}

package org.uengine.iam.oauthclient;


/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthClient {

    private String name;
    private String description;
    private String clientKey;
    private String clientSecret;
    private String[] enableScopes;
    private boolean accessRestEnable;
    private String activeClient;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String refreshTokenValidity;
    private String autoDeletionToken;
    private String requiredContext;
    private String jwtAlgorithm;
    private Integer codeLifetime;
    private Integer refreshTokenLifetime;
    private Integer accessTokenLifetime;
    private String[] notification;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String[] getEnableScopes() {
        return enableScopes;
    }

    public void setEnableScopes(String[] enableScopes) {
        this.enableScopes = enableScopes;
    }

    public boolean getAccessRestEnable() {
        return accessRestEnable;
    }

    public void setAccessRestEnable(boolean accessRestEnable) {
        this.accessRestEnable = accessRestEnable;
    }

    public String getActiveClient() {
        return activeClient;
    }

    public void setActiveClient(String activeClient) {
        this.activeClient = activeClient;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public String getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(String refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAutoDeletionToken() {
        return autoDeletionToken;
    }

    public void setAutoDeletionToken(String autoDeletionToken) {
        this.autoDeletionToken = autoDeletionToken;
    }

    public String getRequiredContext() {
        return requiredContext;
    }

    public void setRequiredContext(String requiredContext) {
        this.requiredContext = requiredContext;
    }

    public String getJwtAlgorithm() {
        return jwtAlgorithm;
    }

    public void setJwtAlgorithm(String jwtAlgorithm) {
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public Integer getCodeLifetime() {
        return codeLifetime;
    }

    public void setCodeLifetime(Integer codeLifetime) {
        this.codeLifetime = codeLifetime;
    }

    public Integer getRefreshTokenLifetime() {
        return refreshTokenLifetime;
    }

    public void setRefreshTokenLifetime(Integer refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public Integer getAccessTokenLifetime() {
        return accessTokenLifetime;
    }

    public void setAccessTokenLifetime(Integer accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public String[] getNotification() {
        return notification;
    }

    public void setNotification(String[] notification) {
        this.notification = notification;
    }
}

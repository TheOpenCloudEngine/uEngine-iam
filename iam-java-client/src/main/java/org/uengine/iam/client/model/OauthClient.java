package org.uengine.iam.client.model;


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
    private boolean userScopeCheck;
    private String[] secureMetadataFields;

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

    public boolean getActiveClient() {
        return activeClient;
    }

    public void setActiveClient(boolean activeClient) {
        this.activeClient = activeClient;
    }

    public String[] getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String[] authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public boolean getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(boolean refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public boolean getAutoDeletionToken() {
        return autoDeletionToken;
    }

    public void setAutoDeletionToken(boolean autoDeletionToken) {
        this.autoDeletionToken = autoDeletionToken;
    }

    public String[] getRequiredContext() {
        return requiredContext;
    }

    public void setRequiredContext(String[] requiredContext) {
        this.requiredContext = requiredContext;
    }

    public String getJwtAlgorithm() {
        return jwtAlgorithm;
    }

    public void setJwtAlgorithm(String jwtAlgorithm) {
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public Long getCodeLifetime() {
        return codeLifetime;
    }

    public void setCodeLifetime(Long codeLifetime) {
        this.codeLifetime = codeLifetime;
    }

    public Long getRefreshTokenLifetime() {
        return refreshTokenLifetime;
    }

    public void setRefreshTokenLifetime(Long refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public Long getAccessTokenLifetime() {
        return accessTokenLifetime;
    }

    public void setAccessTokenLifetime(Long accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public String[] getNotification() {
        return notification;
    }

    public void setNotification(String[] notification) {
        this.notification = notification;
    }

    public boolean getUserScopeCheck() {
        return userScopeCheck;
    }

    public void setUserScopeCheck(boolean userScopeCheck) {
        this.userScopeCheck = userScopeCheck;
    }

    public String[] getSecureMetadataFields() {
        return secureMetadataFields;
    }

    public void setSecureMetadataFields(String[] secureMetadataFields) {
        this.secureMetadataFields = secureMetadataFields;
    }
}

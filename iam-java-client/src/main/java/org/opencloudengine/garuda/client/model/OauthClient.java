package org.opencloudengine.garuda.client.model;


import org.opencloudengine.garuda.client.couchdb.CouchDAO;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthClient extends CouchDAO {

    private String managementId;
    private String name;
    private String description;
    private String clientKey;
    private String clientSecret;
    private String clientTrust;
    private String clientType;
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
    private Integer jwtTokenLifetime;
    private Long regDate;
    private Long updDate;

    public String getManagementId() {
        return managementId;
    }

    public void setManagementId(String managementId) {
        this.managementId = managementId;
    }

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

    public String getClientTrust() {
        return clientTrust;
    }

    public void setClientTrust(String clientTrust) {
        this.clientTrust = clientTrust;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
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

    public Integer getJwtTokenLifetime() {
        return jwtTokenLifetime;
    }

    public void setJwtTokenLifetime(Integer jwtTokenLifetime) {
        this.jwtTokenLifetime = jwtTokenLifetime;
    }

    public Long getRegDate() {
        return regDate;
    }

    public void setRegDate(Long regDate) {
        this.regDate = regDate;
    }

    public Long getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Long updDate) {
        this.updDate = updDate;
    }

    @Override
    public String toString() {
        return "OauthClient{" +
                "managementId='" + managementId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", clientTrust='" + clientTrust + '\'' +
                ", clientType='" + clientType + '\'' +
                ", activeClient='" + activeClient + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
                ", refreshTokenValidity='" + refreshTokenValidity + '\'' +
                ", autoDeletionToken='" + autoDeletionToken + '\'' +
                ", requiredContext='" + requiredContext + '\'' +
                ", jwtAlgorithm='" + jwtAlgorithm + '\'' +
                ", codeLifetime=" + codeLifetime +
                ", refreshTokenLifetime=" + refreshTokenLifetime +
                ", accessTokenLifetime=" + accessTokenLifetime +
                ", jwtTokenLifetime=" + jwtTokenLifetime +
                ", regDate=" + regDate +
                ", updDate=" + updDate +
                '}';
    }
}

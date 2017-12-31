package org.opencloudengine.garuda.client.model;


import org.opencloudengine.garuda.client.couchdb.CouchDAO;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthAccessToken extends CouchDAO {

    private String type;
    private String scopes;
    private String token;
    private String oauthUserId;
    private String managementId;
    private String clientId;
    private String refreshToken;
    private String oldRefreshToken;
    private Long regDate;
    private Long updDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOauthUserId() {
        return oauthUserId;
    }

    public void setOauthUserId(String oauthUserId) {
        this.oauthUserId = oauthUserId;
    }

    public String getManagementId() {
        return managementId;
    }

    public void setManagementId(String managementId) {
        this.managementId = managementId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOldRefreshToken() {
        return oldRefreshToken;
    }

    public void setOldRefreshToken(String oldRefreshToken) {
        this.oldRefreshToken = oldRefreshToken;
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
        return "OauthAccessToken{" +
                "type='" + type + '\'' +
                ", scopes='" + scopes + '\'' +
                ", token='" + token + '\'' +
                ", oauthUserId='" + oauthUserId + '\'' +
                ", managementId='" + managementId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", oldRefreshToken='" + oldRefreshToken + '\'' +
                ", regDate=" + regDate +
                ", updDate=" + updDate +
                '}';
    }
}

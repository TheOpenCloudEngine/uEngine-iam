package org.uengine.iam.client.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthAccessToken {

    private long id;
    private String type;
    private String token;
    private List<String> scopes;
    private String userName;
    private String clientKey;
    private String refreshToken;
    private String oldRefreshToken;
    private long regDate;
    private long updDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
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

    public long getRegDate() {
        return regDate;
    }

    public void setRegDate(long regDate) {
        this.regDate = regDate;
    }

    public long getUpdDate() {
        return updDate;
    }

    public void setUpdDate(long updDate) {
        this.updDate = updDate;
    }
}

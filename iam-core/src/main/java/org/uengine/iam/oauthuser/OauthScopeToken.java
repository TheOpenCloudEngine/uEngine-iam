package org.uengine.iam.oauthuser;

import java.io.Serializable;

/**
 * Created by uengine on 2016. 4. 19..
 */
public class OauthScopeToken implements Serializable {
    private String token;
    private boolean validated;
    private String userName;
    private String clientKey;
    private String scopes;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
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

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        return "OauthScopeToken{" +
                "token='" + token + '\'' +
                ", validated=" + validated +
                ", userName='" + userName + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", scopes='" + scopes + '\'' +
                '}';
    }
}

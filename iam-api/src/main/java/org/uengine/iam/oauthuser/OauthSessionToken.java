package org.uengine.iam.oauthuser;

import java.io.Serializable;

/**
 * Created by uengine on 2016. 4. 19..
 */
public class OauthSessionToken implements Serializable {
    private String token;
    private boolean validated;
    private String userName;

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

    @Override
    public String toString() {
        return "OauthSessionToken{" +
                "token='" + token + '\'' +
                ", validated=" + validated +
                ", userName='" + userName + '\'' +
                '}';
    }
}

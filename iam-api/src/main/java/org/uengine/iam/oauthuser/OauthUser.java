package org.uengine.iam.oauthuser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthUser {

    private String userName;
    private String userPassword;
    private Map<String, Object> metaData;
    private long regDate;
    private long updDate;
    private String provider;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

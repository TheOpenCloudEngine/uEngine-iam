package org.uengine.iam.oauthtoken;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import org.uengine.iam.util.JsonUtils;

import javax.persistence.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Entity
@Table(name = "oauth_token")
public class OauthAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String type;

    @JsonIgnore
    private String scopesString;

    @Column(columnDefinition = "TEXT")
    private String token;
    private String userName;
    private String clientKey;
    private String refreshToken;
    private String oldRefreshToken;

    @Column(name = "regDate", nullable = false, updatable = false, insertable = true)
    private long regDate;

    @Column(name = "updDate", nullable = false, updatable = true, insertable = true)
    private long updDate;

    @PrePersist
    void preInsert() {
        this.regDate = new Date().getTime();
        this.updDate = new Date().getTime();
    }

    @PreUpdate
    void preUpdate() {
        this.updDate = new Date().getTime();
    }

    public List<String> getScopes() {
        try {
            return Arrays.asList(this.scopesString.split(","));
        } catch (Exception ex) {
            return new ArrayList<String>();
        }
    }

    public void setScopes(List<String> scopes) {
        try {
            this.scopesString = Joiner.on(",").join(scopes);
        } catch (Exception ex) {
            this.scopesString = "";
        }
    }

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

    public String getScopesString() {
        return scopesString;
    }

    public void setScopesString(String scopesString) {
        this.scopesString = scopesString;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

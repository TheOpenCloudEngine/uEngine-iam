package org.uengine.iam.oauthtoken;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by uengine on 2015. 6. 3..
 */
@Entity
public class OauthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String clientKey;
    private String userName;
    private String code;
    @JsonIgnore
    private String scopesString;
    @Column(name = "regDate", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date regDate;
    @Column(name = "updDate", nullable = false, updatable = true, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date updDate;

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

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScopesString() {
        return scopesString;
    }

    public void setScopesString(String scopesString) {
        this.scopesString = scopesString;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }
}
